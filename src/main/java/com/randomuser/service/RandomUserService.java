package com.randomuser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.randomuser.model.RandomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class RandomUserService {

    private final ConcurrentHashMap<Long, RandomUser> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    @Autowired
    private WebClient webClient;

    @Value("${randomuser.api.base-url}")
    private String apiBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RandomUser> fetchRandomUsers(int count) {
        try {
            String responseBody = webClient
                    .get()
                    .uri(apiBaseUrl + "?results=" + count)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<RandomUser> users = parseApiResponse(responseBody);
            for (RandomUser user : users) {
                user.setId(idCounter.getAndIncrement());
                userStore.put(user.getId(), user);
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch random users from API", e);
        }
    }

    public List<RandomUser> fetchRandomUserByNationality(String nat) {
        try {
            String responseBody = webClient
                    .get()
                    .uri(apiBaseUrl + "?nat=" + nat)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<RandomUser> users = parseApiResponse(responseBody);
            for (RandomUser user : users) {
                user.setId(idCounter.getAndIncrement());
                userStore.put(user.getId(), user);
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users by nationality from API", e);
        }
    }

    public List<RandomUser> getAllUsers() {
        return new ArrayList<>(userStore.values());
    }

    public RandomUser getUserById(Long id) {
        return userStore.get(id);
    }

    public RandomUser createUser(RandomUser user) {
        user.setId(idCounter.getAndIncrement());
        userStore.put(user.getId(), user);
        return user;
    }

    public RandomUser updateUser(Long id, RandomUser user) {
        if (!userStore.containsKey(id)) {
            return null;
        }
        user.setId(id);
        userStore.put(id, user);
        return user;
    }

    public boolean deleteUser(Long id) {
        return userStore.remove(id) != null;
    }

    public List<RandomUser> searchUsersByCountry(String country) {
        return userStore.values()
                .stream()
                .filter(user -> user.getCountry() != null &&
                        user.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    private List<RandomUser> parseApiResponse(String responseBody) throws Exception {
        List<RandomUser> users = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode resultsArray = rootNode.get("results");

        if (resultsArray != null && resultsArray.isArray()) {
            for (JsonNode resultNode : resultsArray) {
                RandomUser user = new RandomUser();

                user.setGender(resultNode.get("gender").asText());

                JsonNode nameNode = resultNode.get("name");
                if (nameNode != null) {
                    user.setFirstName(nameNode.get("first").asText());
                    user.setLastName(nameNode.get("last").asText());
                }

                user.setEmail(resultNode.get("email").asText());
                user.setPhone(resultNode.get("phone").asText());

                JsonNode locationNode = resultNode.get("location");
                if (locationNode != null) {
                    user.setCity(locationNode.get("city").asText());
                    user.setCountry(locationNode.get("country").asText());
                }

                JsonNode pictureNode = resultNode.get("picture");
                if (pictureNode != null) {
                    user.setPicture(pictureNode.get("large").asText());
                }

                user.setNationality(resultNode.get("nat").asText());

                users.add(user);
            }
        }

        return users;
    }

}
