package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeEntity, Long> {

    Page<OfficeEntity> findAllByAddressContaining(String address, Pageable pageable);

    boolean existsByAddress(String address);

}
