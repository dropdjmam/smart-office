package plugin.atb.booking.repository;

import java.time.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findAllByHolderAndIsDeletedIsFalse(
        EmployeeEntity holder,
        Pageable pageable
    );

    /**
     * Возвращает отсортированную страницу сущностей (бронирований),
     * для которого выполняются следующие условия:
     * <ul>
     * <li> Рабочее место соответствует искомому (параметру workplace)
     * <li> Начало полученной брони раньше конца брони из БД (т.е. start < dateTimeOfEnd)
     * <li> Конец полученной брони позже начала брони из БД (т.е. end > dateTimeOfStart)
     * <li> Статус об удалении брони - false (не удалена).
     * <li> Данные сортируются по возрастанию по дате/времени начала брони
     * </ul>
     * Согласно полученным параметрам пагинации формируется страница
     *
     * @param workPlace бронируемое рабочее место
     * @param startOfNewBooking дата/время начала новой брони, start
     * @param endOfNewBooking дата/время окончания новой брони, end
     * @param pageable параметры пагинации
     *
     * @return страница бронирований
     */
    @Query(value = "select booking from BookingEntity booking " +
                   "where booking.workPlace = :workPlace and " +
                   "(booking.dateTimeOfStart < :end and " +
                   "booking.dateTimeOfEnd > :start) and " +
                   "booking.isDeleted is false " +
                   "order by booking.dateTimeOfStart asc")
    Page<BookingEntity> findAllInPeriod(
        @Param("workPlace") WorkPlaceEntity workPlace,
        @Param("start") LocalDateTime startOfNewBooking,
        @Param("end") LocalDateTime endOfNewBooking,
        @Param("pageable") Pageable pageable
    );

    /**
     * Возвращает отсортированную страницу сущностей (актуальных бронирований),
     * для которого выполняются следующие условия:
     * <ul>
     * <li> Возвращаются брони согласно полученному пользователю (он же держатель брони/holder).
     * <li> Время окончания брони из БД (dateTimeOfEnd) должно быть позже данного момента.
     * <li> Статус об удалении брони - false (не удалена).
     * <li> Данные сортируются по возрастанию по дате/времени начала брони.
     * </ul>
     * Согласно полученным параметрам пагинации формируется страница
     *
     * @param holder держатель брони
     * @param pageable параметры пагинации
     *
     * @return страница бронирований
     */
    @Query("select booking from BookingEntity booking " +
           "where booking.holder = :holder and " +
           "booking.dateTimeOfEnd > current_timestamp and " +
           "booking.isDeleted is false " +
           "order by booking.dateTimeOfStart asc")
    Page<BookingEntity> findAllActual(
        @Param("holder") EmployeeEntity holder,
        @Param("pageable") Pageable pageable
    );

}
