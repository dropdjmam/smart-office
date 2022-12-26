package plugin.atb.booking.config;

import java.util.*;

import javax.annotation.*;

import org.springframework.context.annotation.*;

@Configuration
public class ZoneConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
