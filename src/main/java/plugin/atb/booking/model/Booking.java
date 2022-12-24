package plugin.atb.booking.model;

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
@Table(name = "bookings",
    indexes = {@Index(name = "holder", columnList = "holder_id"),
               @Index(name = "maker", columnList = "maker_id"),
               @Index(name = "workplace_bookings", columnList = "workplace_id"),
               @Index(name = "booking_start", columnList = "date_time_of_start"),
               @Index(name = "booking_end", columnList = "date_time_of_end"),
               @Index(name = "in_period",
                   columnList = "workplace_id, date_time_of_start, date_time_of_end, is_deleted"),
               @Index(name = "actual",
                   columnList = "holder_id, date_time_of_start, date_time_of_end, is_deleted")})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id")
    @ToString.Exclude
    private Employee holder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    @ToString.Exclude
    private Employee maker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    @ToString.Exclude
    private WorkPlace workPlace;

    @Column(name = "date_time_of_start")
    private LocalDateTime dateTimeOfStart;

    @Column(name = "date_time_of_end")
    private LocalDateTime dateTimeOfEnd;

    @Column(columnDefinition = "integer default 0")
    private Integer guests;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

}
