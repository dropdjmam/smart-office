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
@Table(name = "feedbacks", indexes = @Index(name = "employee_feedbacks", columnList = "employee_id"))
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @ToString.Exclude
    private Employee employee;

    @Column(columnDefinition = "timestamp without time zone default now() at time zone 'UTC'",
        insertable = false)
    private LocalDateTime timeStamp;

    private String title;

    @Column(length = 500)
    private String text;

}
