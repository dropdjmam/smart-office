package plugin.atb.booking.entity;

import java.time.*;

import lombok.*;

import javax.persistence.*;

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

    private String address;

    private String workNumber;

    private LocalTime startOfDay;

    private LocalTime endOfDay;

    private Integer bookingRange;

}
