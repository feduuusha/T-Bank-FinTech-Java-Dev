package org.tbank.fintech.lesson_9.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.fintech.lesson_9.controller.payload.user.NewUserPayload;
import org.tbank.fintech.lesson_9.controller.payload.user.UpdateUserPayload;
import org.tbank.fintech.lesson_9.entity.user.ApiUser;
import org.tbank.fintech.lesson_9.repository.ApiUserRepository;
import org.tbank.fintech.lesson_9.security.jwt.JwtTokenProvider;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public class AuthE2ETests {

    @Autowired
    private ApiUserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        propertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        propertyRegistry.add("jwt.token.expiryDate.remember", () -> "10s");
        propertyRegistry.add("jwt.token.expiryDate.notRemember", () -> "5s");
    }

    @BeforeEach
    public void cleanDataBase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/register should register new user and save it it db")
    @WithAnonymousUser
    public void registerUserTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor@mail.ru", "Fyodor", "Voropaev", "admin", "password1");

        // Act
        String response = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        ApiUser user = objectMapper.readValue(response, ApiUser.class);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(user.getUsername()).isEqualTo(payload.username());
        softly.assertThat(user.getEmail()).isEqualTo(payload.email());
        softly.assertThat(user.getFirstName()).isEqualTo(payload.firstName());
        softly.assertThat(user.getLastName()).isEqualTo(payload.lastName());
        var repoUser = userRepository.findByUsername(payload.username());
        softly.assertThat(repoUser).isPresent();
        softly.assertThat(user.getRoles().stream().filter(role -> role.getName().equals("USER")).findFirst()).isPresent();

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/register should return bad request because with specified username already exist")
    @WithAnonymousUser
    public void registerUserUnSuccessfulTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor@mail.ru", "Fyodor", "Voropaev", "admin", "password1");
        userRepository.save(new ApiUser(null, payload.username(), payload.email(), payload.password(), payload.firstName(), payload.lastName(), null, null));

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/login should return login token that will be valid for 5s because username and password is correct and rememberMe is false")
    @WithAnonymousUser
    public void loginUserWithNotRememberMeSuccessfulTest() throws Exception {
        // Arrange
        String email = "vor@mail.ru";
        String username = "feduuusha";
        String password = "fedor123";
        userRepository.save(new ApiUser(null, username, email, passwordEncoder.encode(password), null, null, null, null));

        // Act
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password, "rememberMe", "false"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        String token = response.substring(10, response.length()-1);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(tokenProvider.validateToken(token)).isTrue();
        softly.assertThat(tokenProvider.getUsername(token)).isEqualTo(username);
        Thread.sleep(5000);
        softly.assertThat(tokenProvider.validateToken(tokenProvider.resolveToken(response))).isFalse();

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/login should return login token that will be correct fot 10s because username and password is correct and rememberMe is true")
    @WithAnonymousUser
    public void loginUserWithRememberMeSuccessfulTest() throws Exception {
        // Arrange
        String email = "vor@mail.ru";
        String username = "feduuusha";
        String password = "fedor123";
        userRepository.save(new ApiUser(null, username, email, passwordEncoder.encode(password), null, null, null, null));

        // Act
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password, "rememberMe", "true"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        String token = response.substring(10, response.length()-1);

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(tokenProvider.validateToken(token)).isTrue();
        softly.assertThat(tokenProvider.getUsername(token)).isEqualTo(username);
        Thread.sleep(10000);
        softly.assertThat(tokenProvider.validateToken(tokenProvider.resolveToken(response))).isFalse();

        softly.assertAll();
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/logout should add token in blacklist")
    public void logoutUserTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor@mail.ru", "Fyodor", "Voropaev", "admin", "password1");


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        payload.username(),
                        payload.password()
                )
        );
        String token = tokenProvider.createToken(authentication, true);
        mockMvc.perform(get("/api/v1/events").header("Authorization", "Bearer " + token))
                .andExpect(status().is(200));

        // Act
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().is(204));

        // Assert
        mockMvc.perform(get("/api/v1/events").header("Authorization", "Bearer " + token))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/reset-password should send token on mail(can not check) and endpoint should be available for not auth-ed users")
    @WithAnonymousUser
    public void resetPasswordTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor@mail.ru", "Fyodor", "Voropaev", "admin", "password1");
        userRepository.save(new ApiUser(null, payload.username(), payload.email(), passwordEncoder.encode(payload.password()), null, null, null, null));

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", payload.email()))))
                .andExpect(status().is(204));
    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/reset-password should return 400 because email is not exist")
    @WithAnonymousUser
    public void resetPasswordUnSuccessfulTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "randomEmail@mail.com"))))
                .andExpect(status().is(404));
    }


    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/change-user should be authenticated and should change user in db, but email and username should be unique")
    public void changeUserSuccessfulTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor2@mail.ru", "Fyodor", "Voropaev", "admin2", "password1");


        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        payload.username(),
                        payload.password()
                )
        );
        String token = tokenProvider.createToken(authentication, true);
        var updatePayload = new UpdateUserPayload("newEmail3@mail.com", "newPassword123", "newName", "newName");

        // Act
        mockMvc.perform(post("/api/v1/auth/change-user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                        .andExpect(status().isOk());
        var repoUser = userRepository.findByUsername(payload.username());

        // Assert
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(repoUser.get().getEmail()).isEqualTo(updatePayload.email());
        softly.assertThat(passwordEncoder.matches(updatePayload.password(), repoUser.get().getPassword())).isTrue();
        softly.assertThat(repoUser.get().getFirstName()).isEqualTo(updatePayload.firstName());
        softly.assertThat(repoUser.get().getLastName()).isEqualTo(updatePayload.lastName());

        softly.assertAll();

    }

    @Test
    @DisplayName("Method: POST, Endpoint: /api/v1/auth/change-user should be authenticated and should return 400 because, user with specified email already exist")
    public void changeUserUnSuccessfulTest() throws Exception {
        // Arrange
        var payload = new NewUserPayload("vor3@mail.ru", "Fyodor", "Voropaev", "admin3", "password1");


        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        payload.username(),
                        payload.password()
                )
        );
        String token = tokenProvider.createToken(authentication, true);
        userRepository.save(new ApiUser(null, "username15", "newEmail5@mail.com", passwordEncoder.encode(payload.password()), null, null, null, null));
        var updatePayload = new UpdateUserPayload("newEmail5@mail.com", "newPassword123", "newName", "newName");
        mockMvc.perform(get("/api/v1/events").header("Authorization", "Bearer " + token))
                .andExpect(status().is(200));


        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/change-user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isBadRequest());
    }

}
