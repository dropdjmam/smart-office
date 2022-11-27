package plugin.atb.booking.repository;

import java.time.*;
import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Возвращает отсортированный список сущностей (бронирований),
     * для которого выполняются следующие условия:
     * <ul>
     * <li> Рабочее место соответствует искомому (параметру workplace)
     * <li> Начало полученной брони раньше конца брони из БД (т.е. start < dateTimeOfEnd)
     * <li> Конец полученной брони позже начала брони из БД (т.е. end > dateTimeOfStart)
     * <li> Данные сортируются по убыванию по дате/времени начала брони.
     * </ul>
     *
     * @param workPlace бронируемое рабочее место
     * @param startOfNewBooking дата/время начала новой брони, end
     * @param endOfNewBooking дата/время окончания новой брони, start
     *
     * @return список бронирований
     */
    @Query(value = "select booking from BookingEntity booking " +
                   "where booking.workPlace = :workPlace and " +
                   "(booking.dateTimeOfStart < :end and " +
                   "booking.dateTimeOfEnd > :start) " +
                   "order by booking.dateTimeOfStart desc")
    List<BookingEntity> findAllInPeriod(
        @Param("workPlace") WorkPlaceEntity workPlace,
        @Param("start") LocalDateTime startOfNewBooking,
        @Param("end") LocalDateTime endOfNewBooking

    );

    /**
     * Возвращает отсортированный список сущностей (актуальных бронирований),
     * для которого выполняются следующие условия:
     * <ul>
     * <li> Возвращаются брони согласно полученному идентификатору пользователя
     * (он же держатель брони/holder).
     * <li> Время окончания брони из БД (dateTimeOfEnd) должно быть позже данного момента.
     * <li> Данные сортируются по убыванию по дате/времени начала брони.
     * </ul>
     *
     * @param id идентификатор пользователя, holder_id
     *
     * @return список бронирований
     */
    @Query("select booking from BookingEntity booking " +
           "where booking.holder.id = :holder_id and " +
           "booking.dateTimeOfEnd > current_timestamp " +
           "order by booking.dateTimeOfStart desc")
    List<BookingEntity> findAllActual(@Param("holder_id") Long id);

}
