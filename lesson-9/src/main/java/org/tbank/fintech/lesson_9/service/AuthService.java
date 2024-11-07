package org.tbank.fintech.lesson_9.service;

import org.tbank.fintech.lesson_9.entity.user.ApiUser;

public interface AuthService {
    ApiUser registerUser(String email, String username, String password, String firstName, String lastName);

    String authenticateUser(String username, String password, Boolean rememberMe);

    void logoutUser(String token);

    void resetUserPasswordByEmail(String email);

    ApiUser changeUserData(String email, String firstName, String lastName, String password);
}
