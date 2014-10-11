package com.ctv.registration.adapter.rest.converter

import com.ctv.registration.adapter.rest.dto.User
import com.ctv.registration.core.model.UserModel
import org.springframework.core.convert.converter.Converter

/**
 * @author Dmitry Kovalchuk
 */
class UserToUserModelConverter implements Converter<User, UserModel> {

    @Override
    UserModel convert(User source) {
        UserModel userModel = new UserModel()
        userModel.with {
            id = source.id
            username = source.username
            password = source.password
            email = source.email
            type = source.type
            site = source.site
        }
        userModel
    }

}
