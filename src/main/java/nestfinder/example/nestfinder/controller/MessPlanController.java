package nestfinder.example.nestfinder.controller;


import lombok.RequiredArgsConstructor;
import nestfinder.example.nestfinder.model.MessPlan;
import nestfinder.example.nestfinder.repository.MessPlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mess-plans")
@RequiredArgsConstructor
public class MessPlanController {

    private final MessPlanRepository messPlanRepository;

    @GetMapping
    public List<MessPlan> getAllMessPlans() {
        return messPlanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessPlan> getMessPlanById(@PathVariable Long id) {
        return messPlanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('MESS_OWNER')")
    @PostMapping
    public MessPlan createMessPlan(@RequestBody MessPlan messPlan) {
        return messPlanRepository.save(messPlan);
    }


    @PreAuthorize("hasRole('MESS_OWNER')")
    @PutMapping("/{id}")
    public ResponseEntity<MessPlan> updateMessPlan(@PathVariable Long id, @RequestBody MessPlan messPlanDetails) {
        return messPlanRepository.findById(id).map(messPlan -> {
            messPlan.setTitle(messPlanDetails.getTitle());
            messPlan.setDescription(messPlanDetails.getDescription());
            messPlan.setPrice(messPlanDetails.getPrice());
            messPlan.setLocation(messPlanDetails.getLocation());
            messPlan.setMenu(messPlanDetails.getMenu());
            messPlan.setPhotos(messPlanDetails.getPhotos());
            messPlanRepository.save(messPlan);
            return ResponseEntity.ok(messPlan);
        }).orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('MESS_OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMessPlan(@PathVariable Long id) {
        return messPlanRepository.findById(id).map(messPlan -> {
            messPlanRepository.delete(messPlan);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
