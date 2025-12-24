package com.example.user_service.service;

import com.example.common.dto.UserResponseDTO;
import com.example.common.exception.UserNotFoundException;
import com.example.user_service.dto.UserRequestDTO;
import com.example.user_service.model.UserModel;
import com.example.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        UserModel user = new UserModel(userRequestDTO.name(), userRequestDTO.email());
        userRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(String id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found"));
        return new UserResponseDTO(userModel.getId(), userModel.getName(), userModel.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + email + " not found"));
        return new UserResponseDTO(userModel.getId(), userModel.getName(), userModel.getEmail());
    }


}
