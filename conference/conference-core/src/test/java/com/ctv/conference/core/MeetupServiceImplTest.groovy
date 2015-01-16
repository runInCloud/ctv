package com.ctv.conference.core
import com.ctv.conference.core.adapter.ConferencePersistenceAdapter
import com.ctv.conference.core.adapter.MeetupPersistenceAdapter
import com.ctv.conference.core.config.CoreTestConfig
import com.ctv.conference.core.model.Meetup
import com.ctv.shared.model.ErrorCode
import com.ctv.test.Spec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ContextConfiguration

import static com.ctv.conference.core.ConferenceErrorCode.MEETUP_ID_NOT_NULL
import static com.ctv.conference.core.ConferenceErrorCode.MEETUP_NOT_FOUND
import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*
/**
 * @author Dmitry Kovalchuk
 */
@ContextConfiguration(classes = CoreTestConfig)
class MeetupServiceImplTest extends Spec {

    public static final int CONFERENCE_ID = 1
    public static final int USER_ID = 2
    public static final int MEETUP_ID = 3

    @Autowired
    MeetupService meetupService

    @Autowired
    MeetupPersistenceAdapter meetupPersistenceAdapter

    @Autowired
    ConferencePersistenceAdapter conferencePersistenceAdapter

    Meetup meetupIdEqNull = new Meetup()
    Meetup meetupIdNotNull = new Meetup(id: MEETUP_ID)

    def "create meetup when meetup id == null"() {
        when:
        meetupService.createMeetup(meetupIdEqNull, CONFERENCE_ID, USER_ID);

        then:
        mockito {
            verify(conferencePersistenceAdapter).isConferenceOwnedByUser(CONFERENCE_ID, USER_ID);
        }
    }

    def "create meetup when meetup id != null"() {
        when:
        meetupService.createMeetup(meetupIdNotNull, CONFERENCE_ID, USER_ID)

        then:
        thrown(DataConflictExceptions)
        mockito {
            verifyZeroInteractions(conferencePersistenceAdapter)
        }
    }

    def "create meetup when conference is not owned by user should throw exception"() {
        given:
        doThrow(PermissionDeniedException).when(conferencePersistenceAdapter).isConferenceOwnedByUser(CONFERENCE_ID, USER_ID)

        when:
        meetupService.createMeetup(meetupIdEqNull, CONFERENCE_ID, USER_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "create meetup when conference is owned by user"() {
        when:
        meetupService.createMeetup(meetupIdEqNull, CONFERENCE_ID, USER_ID)

        then:
        mockito {
            verify(conferencePersistenceAdapter).isConferenceOwnedByUser(CONFERENCE_ID, USER_ID)
            verify(meetupPersistenceAdapter).createMeetup(meetupIdEqNull, CONFERENCE_ID)
        }
    }

    def "update meetup when meetup id = null should throw exception"() {
        when:
        meetupService.updateMeetup(meetupIdEqNull, USER_ID)

        then:
        DataConflictExceptions e = thrown()
        verifyErrorMessage(e, MEETUP_ID_NOT_NULL)
    }

    def "update meetup when meetup id != null"() {
        when:
        meetupService.updateMeetup(meetupIdNotNull, USER_ID)

        then:
        mockito {
            verify(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)
            verify(meetupPersistenceAdapter).updateMeetup(meetupIdNotNull)
        }
    }

    def "update meetup when user does not have permission for this operation should throw exception"() {
        given:
        doThrow(PermissionDeniedException).when(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)

        when:
        meetupService.updateMeetup(meetupIdNotNull, USER_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "update meetup when user has permission for this operation"() {
        when:
        meetupService.updateMeetup(meetupIdNotNull, USER_ID)

        then:
        mockito {
            verify(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)
            verify(meetupPersistenceAdapter).updateMeetup(meetupIdNotNull)
        }
    }

    def "find all meetups which belong to particular conference"() {
        when:
        meetupService.findMeetupsByConferenceId(CONFERENCE_ID)

        then:
        mockito {
            verify(meetupPersistenceAdapter).findMeetupsByConferenceId(CONFERENCE_ID)
        }
    }

    def "find meet up when meetup exists"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(meetupIdNotNull)

        when:
        meetupService.findMeetup(MEETUP_ID)

        then:
        noExceptionThrown()
    }

    def "find meet up when meetup does not exist should throw exception"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(null)

        when:
        meetupService.findMeetup(MEETUP_ID)

        then:
        ResourceNotFoundException e = thrown()
        verifyErrorMessage(e, MEETUP_NOT_FOUND)
    }

    def "hide meetup when user does not have permission should throw exception"() {
        given:
        doThrow(PermissionDeniedException).when(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)

        when:
        meetupService.hideMeetup(MEETUP_ID, USER_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "hide meetup when user has permission hidden flag should be set to true"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(meetupIdNotNull);

        when:
        meetupService.hideMeetup(MEETUP_ID, USER_ID)

        then:
        mockito {
            assertThat(meetupIdNotNull.isHidden()).isTrue()
            verify(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)
            verify(meetupPersistenceAdapter).findMeetup(MEETUP_ID)
            verify(meetupPersistenceAdapter).hideMeetup(meetupIdNotNull)
        }
    }

    def "hide meetup when meetup does not exists should throw exception"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(null);

        when:
        meetupService.hideMeetup(MEETUP_ID, USER_ID)

        then:
        ResourceNotFoundException e = thrown()
        verifyErrorMessage(e, MEETUP_NOT_FOUND)
    }

    def "join as a speaker when meetup exists should add user id into speaker list"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(meetupIdNotNull)

        when:
        meetupService.joinAsSpeaker(MEETUP_ID, USER_ID);

        then:
        mockito {
            assertThat(meetupIdNotNull.getSpeakers()).containsOnly(USER_ID)
            verify(meetupPersistenceAdapter).joinAsSpeaker(meetupIdNotNull)
        }
    }

    def "join as a speaker when meetup does not exist should throw exception"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(null)

        when:
        meetupService.joinAsSpeaker(MEETUP_ID, USER_ID);

        then:
        ResourceNotFoundException e = thrown()
        verifyErrorMessage(e, MEETUP_NOT_FOUND)
    }

    def "archive meetup when user does not have permission should throw exception"() {
        given:
        doThrow(PermissionDeniedException).when(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)

        when:
        meetupService.archiveMeetup(MEETUP_ID, USER_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "archive meetup when meetup was not found should throw exception"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(null);

        when:
        meetupService.archiveMeetup(MEETUP_ID, USER_ID)

        then:
        ResourceNotFoundException e = thrown()
        verifyErrorMessage(e, MEETUP_NOT_FOUND)
    }

    def "archive meetup when meetup was found deleted flag should be set to true"() {
        given:
        when(meetupPersistenceAdapter.findMeetup(MEETUP_ID)).thenReturn(meetupIdNotNull);

        when:
        meetupService.archiveMeetup(MEETUP_ID, USER_ID)

        then:
        mockito {
            assertThat(meetupIdNotNull.isDeleted()).isTrue()
            verify(meetupPersistenceAdapter).isMeetupOwnedByUser(MEETUP_ID, USER_ID)
            verify(meetupPersistenceAdapter).archiveMeetup(meetupIdNotNull)
        }
    }

    //todo extract to Sepc using dynamic features of groovy
    def verifyErrorMessage(CoreException e, ErrorCode message) {
        e.getErrorCode() == message.getCode()
        e.getMessage() == message.getMessage()
    }

}