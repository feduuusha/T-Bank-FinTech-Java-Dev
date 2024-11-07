package org.tbank.fintech.lesson_9.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tbank.fintech.lesson_9.entity.user.ApiUser;
import org.tbank.fintech.lesson_9.entity.user.Role;
import org.tbank.fintech.lesson_9.repository.ApiUserRepository;
import org.tbank.fintech.lesson_9.repository.RoleRepository;
import org.tbank.fintech.lesson_9.security.jwt.JwtTokenProvider;
import org.tbank.fintech.lesson_9.service.AuthService;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ApiUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ApiUser registerUser(String email, String username, String password, String firstName, String lastName) {
        var userOptionByUsername = this.userRepository.findByUsername(username);
        var userOptionByEmail = this.userRepository.findByEmail(email);
        if (userOptionByEmail.isEmpty() && userOptionByUsername.isEmpty()) {
            var role = this.roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role USER do not exist in db, check liquibase configuration"));
            var roles = new ArrayList<Role>();
            roles.add(role);
            return userRepository.save(new ApiUser(null, username, email, passwordEncoder.encode(password), firstName, lastName, roles, new ArrayList<>(role.getAuthorities())));
        } else {
            throw new EntityExistsException("User with username: " + username + " or with email: " + email + "  already exist");
        }
    }

    @Override
    public String authenticateUser(String username, String password, Boolean rememberMe) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.createToken(authentication, rememberMe);
    }

    @Override
    public void logoutUser(String token) {
        String jwtToken = jwtTokenProvider.resolveToken(token);
        jwtTokenProvider.addTokenToBlackList(jwtToken);
    }

    @Override
    public void resetUserPasswordByEmail(String email) {
        var user = this.userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User with email: " + email + " not found"));
        String token = jwtTokenProvider.createResetToken(user.getUsername());
        // Отправляем токен на почту в письме...
        log.info("Reset token: " + token);
    }

    @Override
    public ApiUser changeUserData(String email, String firstName, String lastName, String password) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        var user = this.userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new NoSuchElementException("User with username: " + userDetails.getUsername() + " not found"));
        var userByEmail = this.userRepository.findByEmail(email);
        if (userByEmail.isPresent() && !userByEmail.get().getUsername().equals(user.getUsername())) throw new EntityExistsException("User with email: " + email + " alrady exist");
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        return this.userRepository.save(user);
    }
}
