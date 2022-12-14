package plugin.atb.booking.utils;

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

}
