package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface ConferenceMemberRepository extends JpaRepository<ConferenceMemberEntity, Long> {

    Page<ConferenceMemberEntity> findAllByBookingId(Long bookingId, Pageable pageable);

    ConferenceMemberEntity findByBookingId(Long bookingId);

    ConferenceMemberEntity findByEmployeeId(Long employeeId);

    boolean existsByEmployeeAndBooking(EmployeeEntity employee, BookingEntity booking);

    /**
     * Возвращает отсортированную страницу сущностей (участников переговоров),
     * соответствующих указанному сотруднику и актуальной брони.
     * <ul>
     * <li> Сотрудник являющийся участником переговорки/конференции соответствует параметру 'employee'
     * <li> Время окончания брони из БД (dateTimeOfEnd) должно быть позже данного момента.
     * <li> Статус об удалении брони - false (не удалена).
     * <li> Данные сортируются по возрастанию по дате/времени начала брони
     * </ul>
     * Согласно полученным параметрам пагинации формируется страница
     *
     * @param employee сотрудник
     * @param pageable параметры пагинации
     *
     * @return страница участников переговорок
     */
    @Query(value = "select conferee from ConferenceMemberEntity conferee " +
                   "left join BookingEntity booking on conferee.booking = booking " +
                   "where conferee.employee = :employee and " +
                   "booking.dateTimeOfEnd > current_timestamp and " +
                   "booking.isDeleted is false " +
                   "order by booking.dateTimeOfStart asc")
    Page<ConferenceMemberEntity> findAllActualByEmployee(
    @Param("employee") EmployeeEntity employee,
    @Param("pageable") Pageable pageable
    );

}
