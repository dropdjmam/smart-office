package plugin.atb.booking.entity;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "workplaces")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class WorkPlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)

    private Long id;

    private String capacity;

}
