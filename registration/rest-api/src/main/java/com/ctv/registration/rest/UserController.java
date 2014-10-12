package com.ctv.registration.rest;

import com.ctv.registration.adapter.rest.UserMvcAdapter;
import com.ctv.registration.adapter.rest.dto.User;
import com.ctv.security.config.client.CtvUserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ctv.registration.rest.Endpoint.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Dmitry Kovalchuk
 */
@RestController
@RequestMapping(USER_PATH)
public class UserController {

    private UserMvcAdapter userMvcAdapter;

    public UserController(UserMvcAdapter userMvcAdapter) {
        this.userMvcAdapter = userMvcAdapter;
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createUser(@RequestBody User user) {
        userMvcAdapter.createUser(user);
    }

    @ResponseStatus(OK)
    @RequestMapping(headers = X_AUTH_TOKEN, method = PUT)
    public void updateUser(@RequestBody User user) {
        userMvcAdapter.updateUser(user);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(headers = X_AUTH_TOKEN, method = DELETE)
    public void deleteUser(@AuthenticationPrincipal CtvUserDetails userDetails) {
        Integer id = userDetails.getId();
        userMvcAdapter.deleteUser(id);
    }

    @ResponseStatus(OK)
    @RequestMapping(value = BY_ID, method = GET)
    public User findUser(@PathVariable Integer id) {
        return userMvcAdapter.findUserById(id);
    }

    @ResponseStatus(OK)
    @RequestMapping(params = {PAGE_PARAM, SIZE_PARAM}, method = GET)
    public List<User> findUsers(@RequestParam(defaultValue = START_PAGE) int page,
                                @RequestParam(defaultValue = PAGE_SIZE) int size) {
        return userMvcAdapter.findAllUsers(page, size);
    }
}
