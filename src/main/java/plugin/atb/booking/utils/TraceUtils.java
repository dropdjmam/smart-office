package plugin.atb.booking.utils;

import java.util.*;

public class TraceUtils {

    /** Метод ищет наименование метода по stack trace-у согласно указанной позиции класса в нем.
     *
     * @param tracePosition - позиция класса в stack trace, не менее 1
     * @return methodName
     * @throws IllegalArgumentException
     */
    public static String getMethodName(int tracePosition) throws IllegalArgumentException{
        if (tracePosition < 1) {
            throw new IllegalArgumentException("tracePosition can't be less than 1");
        }
        Optional<String> methodName = StackWalker.getInstance()
            .walk(stackFrameStream -> stackFrameStream
                .skip(tracePosition)
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName));
        return methodName.orElse(null);
    }

}
