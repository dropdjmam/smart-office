package plugin.atb.booking.entity;

import java.time.*;

import javax.persistence.*;

import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "offices")
public class OfficeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    @ToString.Exclude
    private CityEntity city;

    private String address;

    private String workNumber;

    private LocalTime startOfDay;

    private LocalTime endOfDay;

    private Integer bookingRange;

}
