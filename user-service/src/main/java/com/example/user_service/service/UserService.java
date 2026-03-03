package com.example.user_service.service;

import com.example.common.dto.UserResponseDTO;
import com.example.common.exception.UserNotFoundException;
import com.example.user_service.dto.UserRequestDTO;
import com.example.user_service.model.UserModel;
import com.example.user_service.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Timer findByIdTimer;
    private final Timer createUserTimer;
    private final AtomicInteger cacheInFlight = new AtomicInteger();


    public UserService(UserRepository userRepository, MeterRegistry registry) {
        this.userRepository = userRepository;
        // Таймеры для latency
        this.findByIdTimer = Timer.builder("cache_access_seconds")
                .description("Time to get user from cache or DB by id")
                .tag("service", "userService : findById")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        this.createUserTimer = Timer.builder("db_write_seconds")
                .description("Time to create user in DB")
                .tag("service", "userService : createUser")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
        Gauge.builder("cache_in_flight", cacheInFlight, AtomicInteger::get)
                .description("Number of cache requests currently processing")
                .register(registry);
        Counter hits = Counter.builder("cache_hits_total")
                .tag("cache", "inventory")
                .register(registry);
        Counter misses = Counter.builder("cache_misses_total")
                .tag("cache", "inventory")
                .register(registry);
    }

    @CachePut(value = "users", key = "#result.id")
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        cacheInFlight.incrementAndGet();
        try {
            return createUserTimer.record(() -> {
                if (userRepository.existsByEmail(userRequestDTO.email())) {
                    throw new IllegalArgumentException("User with this email already exists");
                }
                UserModel user = new UserModel(userRequestDTO.name(), userRequestDTO.email());
                userRepository.save(user);
                return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
            });
        } finally {
            cacheInFlight.decrementAndGet();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "users",
            key = "#id"
    )
    public UserResponseDTO findById(String id) {
        cacheInFlight.incrementAndGet();
        try {
            return findByIdTimer.record(() -> {
                UserModel userModel = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found"));
                return new UserResponseDTO(userModel.getId(), userModel.getName(), userModel.getEmail());
            });
        } finally {
            cacheInFlight.decrementAndGet();
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        cacheInFlight.incrementAndGet();
        try {
            UserModel userModel = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with id = " + email + " not found"));

            return new UserResponseDTO(userModel.getId(), userModel.getName(), userModel.getEmail());
        } finally {
            cacheInFlight.decrementAndGet();
        }
    }


}
