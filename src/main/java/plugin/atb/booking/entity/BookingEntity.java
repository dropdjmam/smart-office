package plugin.atb.booking.entity;

import java.time.*;

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
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id")
    @ToString.Exclude
    private EmployeeEntity holder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    @ToString.Exclude
    private EmployeeEntity maker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    @ToString.Exclude
    private WorkPlaceEntity workPlace;

    private LocalDateTime dateTimeOfStart;

    private LocalDateTime dateTimeOfEnd;

    private int guests;

}
