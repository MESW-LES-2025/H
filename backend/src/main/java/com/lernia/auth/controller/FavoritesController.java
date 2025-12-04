package com.lernia.auth.controller;

import com.lernia.auth.dto.FavoritesResponse;
import com.lernia.auth.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @GetMapping
    public ResponseEntity<FavoritesResponse> getOwnFavorites(
            @RequestParam("userId") Long userId
    ) {
        FavoritesResponse response = favoritesService.getFavoritesForUser(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/universities/{universityId}")
    public ResponseEntity<Void> addUniversityToFavorites(
            @RequestParam("userId") Long userId,
            @PathVariable Long universityId
    ) {
        favoritesService.addUniversityToFavorites(userId, universityId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/universities/{universityId}")
    public ResponseEntity<Void> removeUniversityFromFavorites(
            @RequestParam("userId") Long userId,
            @PathVariable Long universityId
    ) {
        favoritesService.removeUniversityFromFavorites(userId, universityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<Void> addCourseToFavorites(
            @RequestParam("userId") Long userId,
            @PathVariable Long courseId
    ) {
        favoritesService.addCourseToFavorites(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> removeCourseFromFavorites(
            @RequestParam("userId") Long userId,
            @PathVariable Long courseId
    ) {
        favoritesService.removeCourseFromFavorites(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
