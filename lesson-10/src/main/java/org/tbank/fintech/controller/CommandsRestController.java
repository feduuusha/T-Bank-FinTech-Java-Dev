package org.tbank.fintech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tbank.fintech.service.CommandsService;

@Tag(name = "Commands controller", description = "A controller for processing commands")
@RestController
@RequestMapping("api/v1/commands")
@RequiredArgsConstructor
public class CommandsRestController {

    private final CommandsService commandsService;

    @Operation(description = "An endpoint with the required 'type' parameter, executes the command and returns its result in the response",
    responses = {
            @ApiResponse(description = "Return command answer when type is exist and command worked correctly", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(description = "Return when type is not exist", responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping
    public ResponseEntity<?> invokeCommand(@Valid @NotBlank @RequestParam String type) {
        var commandResult = this.commandsService.invokeCommandOfType(type);
        return ResponseEntity.ok(commandResult);

    }
}
