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
@Table(name = "workplacetypes")
public class WorkPlaceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

}
