package plugin.atb.booking.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;
import lombok.experimental.*;
import org.hibernate.annotations.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "images")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] content;

    private String name;

}
