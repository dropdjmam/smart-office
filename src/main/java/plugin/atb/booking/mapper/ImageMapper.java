package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class ImageMapper {

    public ImageDto imageToDto(ImageEntity image) {
        var dto = new ImageDto(
            image.getId(),
            image.getContent(),
            image.getName());
        return dto;
    }

    public ImageEntity dtoToImage(MultipartFile multipartImage) throws Exception {
        var image = new ImageEntity()
            .setName(multipartImage.getName())
            .setContent(multipartImage.getBytes());
        return image;
    }

}
