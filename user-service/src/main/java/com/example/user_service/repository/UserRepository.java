package com.example.user_service.repository;

import com.example.user_service.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);

}
