package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;
import plugin.atb.booking.entity.*;

@Component
public class ImageMapper {

    public ImageEntity dtoToImage(MultipartFile multipartImage) throws Exception {
        var image = new ImageEntity()
            .setName(multipartImage.getName())
            .setContent(multipartImage.getBytes());
        return image;
    }

}
