package com.ctv.conference.persistence;

import com.ctv.conference.persistence.adapter.ConferenceRepository;
import com.ctv.conference.persistence.adapter.dto.Conference;
import org.springframework.stereotype.Repository;

/**
 * @author Dmitry Kovalchuk
 */
@Repository
public interface SpringConferenceRepository extends ConferenceRepository,
        org.springframework.data.repository.Repository<Conference, Integer> {
}
