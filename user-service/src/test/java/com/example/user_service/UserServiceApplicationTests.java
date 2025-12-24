package com.example.user_service;

import com.example.common.dto.UserResponseDTO;
import com.example.common.exception.UserNotFoundException;
import com.example.user_service.dto.UserRequestDTO;
import com.example.user_service.model.UserModel;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc  // для тестирования контроллеров через MockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // чтобы setup один раз
@Transactional // откатывает изменения после каждого теста
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserServiceApplicationTests {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc; // для тестов контроллера

    @Test
    void contextLoads() {
    }

    @Test
    void createUser_success() {
        UserRequestDTO request = new UserRequestDTO("Masha", "masha@example.com");
        UserResponseDTO response = userService.createUser(request);

        assertNotNull(response.id());
        assertEquals("Masha", response.name());
        assertEquals("masha@example.com", response.email());

        // Проверяем реально в базе
        Optional<UserModel> userInDb = userRepository.findById(response.id());
        assertTrue(userInDb.isPresent());
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        UserRequestDTO request = new UserRequestDTO("Masha", "masha@example.com");
        userService.createUser(request);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void findById_success() {
        UserRequestDTO request = new UserRequestDTO("Masha", "masha@example.com");
        UserResponseDTO created = userService.createUser(request);

        UserResponseDTO found = userService.findById(created.id());
        assertEquals(created.id(), found.id());
        assertEquals("Masha", found.name());
    }

    @Test
    void findById_notFound_throwsException() {
        assertThrows(UserNotFoundException.class, () -> userService.findById("non-existent-id"));
    }

    @Test
    void createUserController_success() throws Exception {
        String json = """
                {
                    "name": "Masha",
                    "email": "masha@example.com"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Masha"))
                .andExpect(jsonPath("$.email").value("masha@example.com"));
    }

}
