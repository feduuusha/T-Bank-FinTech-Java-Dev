package org.tbank.fintech.lesson_9.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.tbank.fintech.lesson_9.controller.payload.user.LoginPayload;
import org.tbank.fintech.lesson_9.controller.payload.user.NewUserPayload;
import org.tbank.fintech.lesson_9.controller.payload.user.ResetPasswordPayload;
import org.tbank.fintech.lesson_9.controller.payload.user.UpdateUserPayload;
import org.tbank.fintech.lesson_9.entity.user.ApiUser;
import org.tbank.fintech.lesson_9.exception.ExceptionMessage;
import org.tbank.fintech.lesson_9.service.AuthService;

import java.util.Map;


@Tag(name="Auth controller", description = "Controller for registration, login, logout etc.")
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;


    @Operation(description = "Endpoint for register new user with role USER",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return User if payload is correct" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiUser.class))),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect or when user with specified username already exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Returning when unexpected error on server", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PostMapping("/register")
    public ResponseEntity<ApiUser> registerUser(@Valid @RequestBody NewUserPayload payload,
                                                BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        ApiUser user = this.authService.registerUser(payload.email(), payload.username(), payload.password(),
                payload.firstName(), payload.lastName());
        return ResponseEntity.ok(user);
    }


    @Operation(description = "Endpoint for login and get JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return token if payload is correct" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Returning when request params is incorrect", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Returning when invalid username or password", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Returning when unexpected error on server", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginPayload loginPayload,
                                              BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        String jwtToken = this.authService.authenticateUser(loginPayload.username(), loginPayload.password(), loginPayload.rememberMe());
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }


    @Operation(description = "Endpoint for logout authorized user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful logout" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Returning when invalid username or password", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Returning when unexpected error on server", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(name = "Authorization") String token) {
        this.authService.logoutUser(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Endpoint for reset user password, it send jwt token on user email",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Token successful send" ,content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Returning when user with specified email do not exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Returning when unexpected error on server", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetUserPasswordByEmail(@Valid @RequestBody ResetPasswordPayload payload) {
        this.authService.resetUserPasswordByEmail(payload.email());
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Endpoint for change user data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful changed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiUser.class))),
                    @ApiResponse(responseCode = "400", description = "Returning when user with specified email already exist", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Returning when user do not authorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Returning when unexpected error on server", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionMessage.class)))})
    @PostMapping("/change-user")
    public ResponseEntity<ApiUser> changeUserData(@Valid @RequestBody UpdateUserPayload payload,
                                            BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        ApiUser user = this.authService.changeUserData(payload.email(), payload.firstName(), payload.lastName(), payload.password());
        return ResponseEntity.ok(user);

    }
}
