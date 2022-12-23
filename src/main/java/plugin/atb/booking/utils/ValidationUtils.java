package plugin.atb.booking.utils;

import java.time.*;

import plugin.atb.booking.exception.*;

public class ValidationUtils {

    public static void checkPageSize(int pageSize, int maxValue) {
        if (pageSize < 1 || pageSize > maxValue) {
            throw new IncorrectArgumentException(String.format(
                "Некорректное количество запрашиваемых объектов: 1 <= %s <= 20",
                pageSize
            ));
        }
    }

    public static void checkId(long id) {
        if (id < 1) {
            throw new IncorrectArgumentException("Id не может быть меньше 1");
        }
    }

    public static void checkInterval(LocalDateTime start, LocalDateTime end) {

        if (start == null) {
            throw new IncorrectArgumentException("Начало временного интервала Date/Time не указано");
        }

        if (end == null) {
            throw new IncorrectArgumentException("Конец временного интервала Date/Time не указан");
        }

        if (end.isBefore(start)) {
            throw new IncorrectArgumentException(String.format(
                "Конец интервала не может быть раньше начала: %s < %s",
                end, start
            ));
        }
    }

    public static void checkInterval(LocalTime start, LocalTime end) {

        if (start == null) {
            throw new IncorrectArgumentException("Начало временного интервала Time не указано");
        }

        if (end == null) {
            throw new IncorrectArgumentException("Конец временного интервала Time не указан");
        }

        if (end.isBefore(start)) {
            throw new IncorrectArgumentException(String.format(
                "Конец интервала не может быть раньше начала: %s < %s",
                end, start
            ));
        }
    }

}
