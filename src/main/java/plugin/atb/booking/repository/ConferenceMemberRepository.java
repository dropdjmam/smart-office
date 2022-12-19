package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface ConferenceMemberRepository extends JpaRepository<ConferenceMemberEntity, Long> {

    Page<ConferenceMemberEntity> findAllByBookingId(Long bookingId, Pageable pageable);

    ConferenceMemberEntity findByBookingId(Long bookingId);

    ConferenceMemberEntity findByEmployeeId(Long employeeId);

    boolean existsByEmployeeAndBooking(EmployeeEntity employee, BookingEntity booking);

}
