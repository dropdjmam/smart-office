package plugin.atb.booking.entity;

import javax.persistence.*;

import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "floors")

public class FloorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Integer floorNumber;

    private String mapFloor;

}
