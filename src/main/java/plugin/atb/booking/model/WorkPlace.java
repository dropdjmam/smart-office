package plugin.atb.booking.model;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "workplaces")
public class WorkPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplacetype_id")
    @ToString.Exclude
    private WorkPlaceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    @ToString.Exclude
    private Floor floor;

    private Integer capacity;

    @Column(name = "place_name", nullable = false,
        columnDefinition = "varchar(255) default concat('№ ', lastval())")
    private String placeName;

}
