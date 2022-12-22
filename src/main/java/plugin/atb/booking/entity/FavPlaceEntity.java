package plugin.atb.booking.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fav_place",
    indexes = {@Index(name = "employee", columnList = "employee_id"),
               @Index(name = "workplace", columnList = "workplace_id"),
               @Index(name = "employee_place", columnList = "employee_id, workplace_id")})
public class FavPlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @ToString.Exclude
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    @ToString.Exclude
    private WorkPlaceEntity place;

}
