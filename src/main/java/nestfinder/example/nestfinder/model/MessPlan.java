package nestfinder.example.nestfinder.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "mess_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private Double price;

    private String location;

    private String menu;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;


    @ElementCollection
    @CollectionTable(name = "mess_photos", joinColumns = @JoinColumn(name = "mess_id"))
    @Column(name = "photo_url")
    private List<String> photos;
}
