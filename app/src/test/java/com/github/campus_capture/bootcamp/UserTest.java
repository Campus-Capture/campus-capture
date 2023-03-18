package com.github.campus_capture.bootcamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.github.campus_capture.bootcamp.authentication.User;

import org.junit.Test;

public class UserTest {

    private String userName;
    private String userUid;

    private void initializeUser(){
        userName = User.getName();
        userUid = User.getUid();
    }

    private void restoreUser(){
        User.setName(userName);
        User.setUid(userUid);
    }

    @Test
    public void setNameUserTest(){
        initializeUser();
        String MOCK_USER_NAME = "Roger Federer";
        User.setName(MOCK_USER_NAME);
        assertThat(User.getName(), is(MOCK_USER_NAME));
        restoreUser();
    }

    @Test
    public void setUidUserTest(){
        initializeUser();
        String MOCK_USER_UID = "123456789";
        User.setUid(MOCK_USER_UID);
        assertThat(User.getUid(), is(MOCK_USER_UID));
        restoreUser();
    }
}
