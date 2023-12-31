package plugin.atb.booking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

}
