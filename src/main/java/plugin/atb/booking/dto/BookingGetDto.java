package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingGetDto {

    private InfoBookingDto infoBooking;

    private InfoPlaceDto infoPlace;

    private InfoOfficeDto infoOffice;

}
