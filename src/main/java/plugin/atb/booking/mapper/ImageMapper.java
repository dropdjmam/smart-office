package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;
import plugin.atb.booking.model.*;

@Component
public class ImageMapper {

    public Image dtoToImage(MultipartFile multipartImage) throws Exception {
        return new Image()
            .setName(multipartImage.getName())
            .setContent(multipartImage.getBytes());
    }

}
