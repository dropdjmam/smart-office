package plugin.atb.booking.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @Column(name = "zone_id", nullable = false, columnDefinition = "VARCHAR(255) default 'UTC'")
    private String zoneId;

}
