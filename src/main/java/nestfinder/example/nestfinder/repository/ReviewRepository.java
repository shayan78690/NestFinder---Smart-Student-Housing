package nestfinder.example.nestfinder.repository;


import nestfinder.example.nestfinder.model.Review;
import nestfinder.example.nestfinder.model.Property;
import nestfinder.example.nestfinder.model.MessPlan;
import nestfinder.example.nestfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProperty(Property property);
    List<Review> findByMessPlan(MessPlan messPlan);
    List<Review> findByUser(User user);
}
