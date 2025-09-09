package nestfinder.example.nestfinder.repository;


import nestfinder.example.nestfinder.model.MessPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessPlanRepository extends JpaRepository<MessPlan, Long> {
}
