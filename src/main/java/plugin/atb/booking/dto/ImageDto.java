package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ImageDto {

    private Long id;

    private byte[] content;

    private String name;

}
