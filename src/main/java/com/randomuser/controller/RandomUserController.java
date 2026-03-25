package com.randomuser.controller;

import com.randomuser.model.ApiResponse;
import com.randomuser.model.RandomUser;
import com.randomuser.service.RandomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RandomUserController {

    @Autowired
    private RandomUserService randomUserService;

    @GetMapping("/random")
    public ResponseEntity<ApiResponse> fetchRandomUsers(
            @RequestParam(value = "count", defaultValue = "1") int count) {
        try {
            List<RandomUser> users = randomUserService.fetchRandomUsers(count);
            ApiResponse response = new ApiResponse(
                    true,
                    "Random users fetched successfully",
                    users,
                    users.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to fetch random users: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/random/nationality/{nat}")
    public ResponseEntity<ApiResponse> fetchRandomUserByNationality(
            @PathVariable String nat) {
        try {
            List<RandomUser> users = randomUserService.fetchRandomUserByNationality(nat);
            ApiResponse response = new ApiResponse(
                    true,
                    "Users fetched by nationality successfully",
                    users,
                    users.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to fetch users by nationality: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<RandomUser> users = randomUserService.getAllUsers();
            ApiResponse response = new ApiResponse(
                    true,
                    "All users retrieved successfully",
                    users,
                    users.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to retrieve users: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        try {
            RandomUser user = randomUserService.getUserById(id);
            if (user == null) {
                ApiResponse response = new ApiResponse(
                        false,
                        "User not found with id: " + id,
                        null,
                        0
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse response = new ApiResponse(
                    true,
                    "User retrieved successfully",
                    user,
                    1
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to retrieve user: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUsersByCountry(
            @RequestParam(value = "country") String country) {
        try {
            List<RandomUser> users = randomUserService.searchUsersByCountry(country);
            ApiResponse response = new ApiResponse(
                    true,
                    "Users searched by country successfully",
                    users,
                    users.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to search users: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody RandomUser user) {
        try {
            RandomUser createdUser = randomUserService.createUser(user);
            ApiResponse response = new ApiResponse(
                    true,
                    "User created successfully",
                    createdUser,
                    1
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to create user: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long id,
            @RequestBody RandomUser user) {
        try {
            RandomUser updatedUser = randomUserService.updateUser(id, user);
            if (updatedUser == null) {
                ApiResponse response = new ApiResponse(
                        false,
                        "User not found with id: " + id,
                        null,
                        0
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse response = new ApiResponse(
                    true,
                    "User updated successfully",
                    updatedUser,
                    1
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to update user: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = randomUserService.deleteUser(id);
            if (!deleted) {
                ApiResponse response = new ApiResponse(
                        false,
                        "User not found with id: " + id,
                        null,
                        0
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ApiResponse response = new ApiResponse(
                    true,
                    "User deleted successfully",
                    null,
                    0
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    false,
                    "Failed to delete user: " + e.getMessage(),
                    null,
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
