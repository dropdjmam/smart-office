package plugin.atb.booking.repository;

import java.time.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.lang.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.model.*;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Удаляет брони связанные с указанным местом, которое планируется удалить,
     * и возвращает набор удаленных бронирований.
     * <ul>
     *     Устанавливает статус об удалении - true и внешний ключ места - null, при условии
     *     соответствия внешнего ключа с переданным параметром
     * </ul>
     *
     * @param place место
     *
     * @return набор броней
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
        value = "with deleted_bookings as " +
                "(update bookings " +
                "set is_deleted = true, workplace_id = null " +
                "where workplace_id = ?1 returning *) " +
                "select * from deleted_bookings")
    Set<Booking> deleteAllByWorkPlace(@Param("place") @NonNull WorkPlace place);

    /**
     * Удаляет брони связанные с указанным держателем брони, которого планируется удалить,
     * и возвращает набор удаленных бронирований.
     * <ul>
     *     Устанавливает статус об удалении - true и внешний ключ держателя брони - null,
     *     при условии соответствия внешнего ключа с переданным параметром
     * </ul>
     *
     * @param holder сотрудник/держатель брони
     *
     * @return набор броней
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
        value = "with deleted_bookings as " +
                "(update bookings " +
                "set is_deleted = true, holder_id = null " +
                "where holder_id = ?1 returning *) " +
                "select * from deleted_bookings")
    Set<Booking> deleteAllByHolder(@Param("holder") @NonNull Employee holder);

    /**
     * Удаляет брони связанные с указанным создателем брони, которого планируется удалить,
     * и возвращает набор удаленных бронирований.
     * <ul>
     *     Устанавливает статус об удалении - true и внешний ключ создателя брони - null,
     *     при условии соответствия внешнего ключа с переданным параметром
     * </ul>
     *
     * @param maker сотрудник/создатель брони
     *
     * @return набор броней
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
        value = "with deleted_bookings as " +
                "(update bookings " +
                "set is_deleted = true, maker_id = null " +
                "where maker_id = ?1 returning *) " +
                "select * from deleted_bookings")
    Set<Booking> deleteAllByMaker(@Param("maker") @NonNull Employee maker);

    Page<Booking> findAllByHolderAndIsDeletedIsFalse(Employee holder, Pageable pageable);

    /**
     * Возвращает отсортированную страницу сущностей (бронирований),
     * для которой выполняются следующие условия:
     * <ul>
     * <li> Рабочее место соответствует искомому (параметру workplace)
     * <li> Начало полученной брони раньше конца брони из БД (т.е. start < dateTimeOfEnd)
     * <li> Конец полученной брони позже начала брони из БД (т.е. end > dateTimeOfStart)
     * <li> Статус об удалении брони - false (не удалена)
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
    @Query("select booking from Booking booking " +
           "where booking.workPlace = :workPlace and " +
           "(booking.dateTimeOfStart < :end and " +
           "booking.dateTimeOfEnd > :start) and " +
           "booking.isDeleted is false " +
           "order by booking.dateTimeOfStart asc")
    Page<Booking> findAllInPeriod(
        @Param("workPlace") WorkPlace workPlace,
        @Param("start") LocalDateTime startOfNewBooking,
        @Param("end") LocalDateTime endOfNewBooking,
        @Param("pageable") Pageable pageable
    );

    /**
     * Возвращает отсортированную страницу сущностей (актуальных бронирований),
     * для которой выполняются следующие условия:
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
    @Query("select booking from Booking booking " +
           "where booking.holder = :holder and " +
           "booking.dateTimeOfEnd > current_timestamp and " +
           "booking.isDeleted is false " +
           "order by booking.dateTimeOfStart asc")
    Page<Booking> findAllActual(
        @Param("holder") Employee holder,
        @Param("pageable") Pageable pageable
    );

    /**
     * В зависимости от параметра 'isActual' возвращает отсортированную страницу сущностей
     * (бронирований в офисе) - актуальных или нет, для которой выполняются следующие условия:
     * <ul>
     * <li> Тип места у указанных в бронях мест соответствует искомому
     * <li> Офис, в котором находятся места из броней соответствует искомому
     * <li> В зависимости от значения isActual - true или false, выполняется или нет следующее условие
     * <ul>
     *     <li> Время окончания брони 'dateTimeOfEnd' должно быть позже данного момента
     * </ul>
     * <li> Данные сортируются по возрастанию по дате/времени начала брони
     * </ul>
     * Согласно полученным параметрам пагинации формируется страница
     *
     * @param office офис
     * @param type тип места
     * @param isActual булевый параметр: актуальные брони/все брони
     * @param pageable параметры пагинации
     *
     * @return страница бронирований в офисе
     */
    @Query("select booking from Booking booking " +
           "left join WorkPlace place on booking.workPlace.id = place.id " +
           "left join WorkPlaceType type on place.type.id = type.id " +
           "left join Floor level on place.floor.id = level.id " +
           "left join Office office on level.office.id = office.id " +
           "where (:isActual is false and type = :type and office = :office) " +
           "or (:isActual is true and booking.dateTimeOfEnd > current_timestamp and " +
           "type = :type and office = :office) " +
           "order by booking.dateTimeOfStart asc")
    Page<Booking> findAllByOffice(
        @Param("office") Office office,
        @Param("type") WorkPlaceType type,
        @Param("isActual") Boolean isActual,
        @Param("pageable") Pageable pageable
    );

}
