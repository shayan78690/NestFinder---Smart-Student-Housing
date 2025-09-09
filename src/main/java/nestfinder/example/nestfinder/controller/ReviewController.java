package nestfinder.example.nestfinder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nestfinder.example.nestfinder.model.Review;
import nestfinder.example.nestfinder.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<Review> addReview(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long messPlanId,
            @Valid @RequestBody Review review) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Review createdReview = reviewService.addReviewByUserEmail(email, review, propertyId, messPlanId);
        // Average rating update inside service method

        return ResponseEntity.ok(createdReview);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @Valid @RequestBody Review review) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Review updatedReview = reviewService.updateReviewByUserEmail(id, email, review);
            // Average rating update inside service method

            return ResponseEntity.ok(updatedReview);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Fetch reviews by Property
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Review>> getReviewsByProperty(@PathVariable Long propertyId) {
        List<Review> reviews = reviewService.getReviewsByProperty(propertyId);
        return ResponseEntity.ok(reviews);
    }

    // Fetch reviews by MessPlan
    @GetMapping("/messplan/{messPlanId}")
    public ResponseEntity<List<Review>> getReviewsByMessPlan(@PathVariable Long messPlanId) {
        List<Review> reviews = reviewService.getReviewsByMessPlan(messPlanId);
        return ResponseEntity.ok(reviews);
    }

    // Delete review - verify user is author
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            reviewService.deleteReviewByUserEmail(id, email);
            // Average rating update inside service method

            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
