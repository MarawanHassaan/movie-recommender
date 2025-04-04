package com.contenetwise.recommender.controller;

import com.contenetwise.recommender.controllers.UserController;
import com.contenetwise.recommender.domain.User;
import com.contenetwise.recommender.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .build();

        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUserShouldReturnCreatedUser() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john_doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testCreateUserShouldReturnConflictIfUsernameExists() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john_doe\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testCreateUserShouldReturnBadRequestIfUsernameIsBlank() throws Exception {
        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john_doe"));
    }

    @Test
    void testGetUserByIdShouldReturnUserWhenExists() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testGetUserByIdShouldReturnNotFoundWhenNotExists() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserShouldReturnUpdatedUser() throws Exception {
        User updatedUser = User.builder().username("john_updated").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john_updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_updated"));
    }

    @Test
    void testUpdateUserShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john_updated\"}"))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void testDeleteUserShouldReturnSuccessMessage() throws Exception {
//        // Mock that the user exists and return a user object
//        User existingUser = new User();
//        existingUser.setId(1L);
//        existingUser.setUsername("testUser");
//        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
//
//        mockMvc.perform(delete("/api/users/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User deleted successfully."));
//
//        // Verify deleteById was called once
//        verify(userRepository, times(1)).deleteById(1L);
//    }

    @Test
    void testDeleteUserShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
