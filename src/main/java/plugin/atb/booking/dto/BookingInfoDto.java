package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingInfoDto {

    private BookingPartInfoDto bookingDto;

    private BookingInfoPlaceDto placeDto;

    private BookingInfoOfficeDto officeDto;

}
