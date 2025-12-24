package com.example.user_service.controller;

import com.example.common.dto.UserResponseDTO;
import com.example.common.exception.UserNotFoundException;
import com.example.user_service.dto.UserRequestDTO;
import com.example.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Управление пользователями")
public class UserController {

    private final UserService userService;
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    @Operation(summary = "Создать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "409", description = "Не удалось создать (уже есть такой email)")
    })
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("POST /users – creating new user");
        try {
            UserResponseDTO resp = userService.createUser(userRequestDTO);
            return ResponseEntity.status(201).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти пользователя", description = "Найти пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует")
    })
    public ResponseEntity<?> getById(@Parameter(description = "ID пользователя", required = true)
                                     @PathVariable String id) {
        log.info("GET /users/{} – fetching user", id);

        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());

        }
    }


}
