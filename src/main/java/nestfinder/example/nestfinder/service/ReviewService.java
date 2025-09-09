package nestfinder.example.nestfinder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nestfinder.example.nestfinder.model.*;
import nestfinder.example.nestfinder.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final MessPlanRepository messPlanRepository;

    // Add review by user email from JWT
    public Review addReviewByUserEmail(String email, Review review, Long propertyId, Long messPlanId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        review.setUser(user);

        if (propertyId != null) {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new EntityNotFoundException("Property not found"));
            review.setProperty(property);
        } else if (messPlanId != null) {
            MessPlan messPlan = messPlanRepository.findById(messPlanId)
                    .orElseThrow(() -> new EntityNotFoundException("Mess Plan not found"));
            review.setMessPlan(messPlan);
        } else {
            throw new IllegalArgumentException("Either propertyId or messPlanId must be provided");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review saved = reviewRepository.save(review);
        updateAverageRating(saved); // Update avg rating after saving review
        return saved;
    }

    // Update review with ownership check by user email
    public Review updateReviewByUserEmail(Long reviewId, String email, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!existing.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Not authorized to update this review");
        }

        if (updatedReview.getRating() < 1 || updatedReview.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());

        Review saved = reviewRepository.save(existing);
        updateAverageRating(saved); // Update avg rating after update
        return saved;
    }

    // Fetch reviews by Property
    public List<Review> getReviewsByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        return reviewRepository.findByProperty(property);
    }

    // Fetch reviews by MessPlan
    public List<Review> getReviewsByMessPlan(Long messPlanId) {
        MessPlan messPlan = messPlanRepository.findById(messPlanId)
                .orElseThrow(() -> new EntityNotFoundException("Mess Plan not found"));
        return reviewRepository.findByMessPlan(messPlan);
    }

    // Delete review with ownership check
    public void deleteReviewByUserEmail(Long reviewId, String email) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!existing.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Not authorized to delete this review");
        }
        reviewRepository.delete(existing);
        updateAverageRating(existing); // Update avg rating after deletion
    }

    private double calculateAverageRatingForProperty(Property property) {
        List<Review> reviews = reviewRepository.findByProperty(property);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private double calculateAverageRatingForMessPlan(MessPlan messPlan) {
        List<Review> reviews = reviewRepository.findByMessPlan(messPlan);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private void updateAverageRating(Review review) {
        if (review.getProperty() != null) {
            Property property = review.getProperty();
            property.setAverageRating(calculateAverageRatingForProperty(property));
            propertyRepository.save(property);
        } else if (review.getMessPlan() != null) {
            MessPlan messPlan = review.getMessPlan();
            messPlan.setAverageRating(calculateAverageRatingForMessPlan(messPlan));
            messPlanRepository.save(messPlan);
        }
    }
}
