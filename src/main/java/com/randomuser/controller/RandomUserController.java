package com.randomuser.controller;

import com.randomuser.model.ApiResponse;
import com.randomuser.model.RandomUser;
import com.randomuser.service.RandomUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Random Users", description = "Endpoints to fetch random user profiles from RandomUser.me and manage them via CRUD operations")
public class RandomUserController {

    @Autowired
    private RandomUserService randomUserService;

    @Operation(
        summary = "Fetch random users from RandomUser.me",
        description = "Calls the RandomUser.me external API to generate random user profiles. The fetched users are stored in the local in-memory store and returned. Use the 'count' parameter to control how many users to generate."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Random users fetched and stored successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(name = "Success", value = "{\"success\":true,\"message\":\"Random users fetched successfully\",\"data\":[{\"id\":1,\"gender\":\"female\",\"firstName\":\"Emily\",\"lastName\":\"Thompson\",\"email\":\"emily.thompson@example.com\",\"phone\":\"041-123-4567\",\"city\":\"Melbourne\",\"country\":\"Australia\",\"picture\":\"https://randomuser.me/api/portraits/women/55.jpg\",\"nationality\":\"AU\"}],\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failed to reach RandomUser.me API or internal error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\":false,\"message\":\"Failed to fetch random users from API\",\"data\":null,\"totalRecords\":0}")))
    })
    @GetMapping("/random")
    public ResponseEntity<ApiResponse> fetchRandomUsers(
            @Parameter(description = "Number of random users to fetch (default: 1)", example = "5")
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

    @Operation(
        summary = "Fetch random users by nationality",
        description = "Fetches a random user filtered by nationality code from RandomUser.me. Supported codes include: AU, BR, CA, CH, DE, DK, ES, FI, FR, GB, IE, IN, IR, MX, NL, NO, NZ, RS, TR, UA, US."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users fetched by nationality successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"Users fetched by nationality successfully\",\"data\":[{\"id\":2,\"gender\":\"male\",\"firstName\":\"Arjun\",\"lastName\":\"Sharma\",\"email\":\"arjun.sharma@example.com\",\"phone\":\"091-9876543210\",\"city\":\"Mumbai\",\"country\":\"India\",\"picture\":\"https://randomuser.me/api/portraits/men/10.jpg\",\"nationality\":\"IN\"}],\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Failed to reach RandomUser.me API or invalid nationality code")
    })
    @GetMapping("/random/nationality/{nat}")
    public ResponseEntity<ApiResponse> fetchRandomUserByNationality(
            @Parameter(description = "ISO nationality code (e.g. US, IN, FR, GB, AU)", example = "IN", required = true)
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

    @Operation(
        summary = "Get all stored users",
        description = "Returns all user records currently held in the in-memory store. This includes users fetched from RandomUser.me and manually created users."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All stored users returned successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(
        summary = "Get stored user by ID",
        description = "Retrieves a single user from the in-memory store using their locally assigned ID."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found and returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"User retrieved successfully\",\"data\":{\"id\":1,\"gender\":\"female\",\"firstName\":\"Emily\",\"lastName\":\"Thompson\",\"email\":\"emily@example.com\",\"phone\":\"041-123-4567\",\"city\":\"Melbourne\",\"country\":\"Australia\",\"picture\":\"https://randomuser.me/api/portraits/women/55.jpg\",\"nationality\":\"AU\"},\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found in local store",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\":false,\"message\":\"User not found with id: 99\",\"data\":null,\"totalRecords\":0}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(
            @Parameter(description = "Local ID of the stored user", example = "1", required = true)
            @PathVariable Long id) {
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

    @Operation(
        summary = "Search stored users by country",
        description = "Filters users in the local in-memory store by country name (case-insensitive). Use the full country name, e.g. 'Australia', 'India', 'United States'."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users matching country returned (empty list if none found)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"Users searched by country successfully\",\"data\":[{\"id\":1,\"firstName\":\"Emily\",\"country\":\"Australia\"}],\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUsersByCountry(
            @Parameter(description = "Full country name to filter by (case-insensitive)", example = "Australia", required = true)
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

    @Operation(
        summary = "Create a user manually",
        description = "Manually add a user to the local in-memory store without calling the RandomUser.me API. A local ID is auto-assigned."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created and stored successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"User created successfully\",\"data\":{\"id\":4,\"gender\":\"male\",\"firstName\":\"Vijay\",\"lastName\":\"Kumar\",\"email\":\"vijay@example.com\",\"phone\":\"9876543210\",\"city\":\"Chennai\",\"country\":\"India\",\"picture\":\"https://example.com/photo.jpg\",\"nationality\":\"IN\"},\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User profile to create. The 'id' field is ignored and auto-assigned.",
                required = true,
                content = @Content(schema = @Schema(implementation = RandomUser.class),
                    examples = @ExampleObject(value = "{\"gender\":\"male\",\"firstName\":\"Vijay\",\"lastName\":\"Kumar\",\"email\":\"vijay@example.com\",\"phone\":\"9876543210\",\"city\":\"Chennai\",\"country\":\"India\",\"picture\":\"https://example.com/photo.jpg\",\"nationality\":\"IN\"}"))
            )
            @RequestBody RandomUser user) {
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

    @Operation(
        summary = "Update a stored user",
        description = "Updates the profile of an existing user in the local in-memory store. The ID in the path takes precedence; the ID field in the request body is ignored."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"User updated successfully\",\"data\":{\"id\":4,\"gender\":\"male\",\"firstName\":\"Vijay\",\"lastName\":\"Kumar\",\"email\":\"vijay.updated@example.com\",\"city\":\"Bangalore\",\"country\":\"India\",\"nationality\":\"IN\"},\"totalRecords\":1}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found in local store",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\":false,\"message\":\"User not found with id: 99\",\"data\":null,\"totalRecords\":0}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @Parameter(description = "Local ID of the user to update", example = "4", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated user profile fields",
                required = true,
                content = @Content(schema = @Schema(implementation = RandomUser.class),
                    examples = @ExampleObject(value = "{\"gender\":\"male\",\"firstName\":\"Vijay\",\"lastName\":\"Kumar\",\"email\":\"vijay.updated@example.com\",\"phone\":\"9876543210\",\"city\":\"Bangalore\",\"country\":\"India\",\"picture\":\"https://example.com/photo.jpg\",\"nationality\":\"IN\"}"))
            )
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

    @Operation(
        summary = "Delete a stored user",
        description = "Permanently removes a user from the local in-memory store by their locally assigned ID."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"User deleted successfully\",\"data\":null,\"totalRecords\":0}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found in local store",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"success\":false,\"message\":\"User not found with id: 99\",\"data\":null,\"totalRecords\":0}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @Parameter(description = "Local ID of the user to delete", example = "1", required = true)
            @PathVariable Long id) {
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
