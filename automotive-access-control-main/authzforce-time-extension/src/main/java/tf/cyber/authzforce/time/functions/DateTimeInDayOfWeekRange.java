package tf.cyber.authzforce.time.functions;

import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.MultiParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.*;
import tf.cyber.authzforce.time.datatypes.DayOfWeekValue;
import tf.cyber.authzforce.time.datatypes.java.DayOfWeekType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.util.Deque;
import java.util.List;

public class DateTimeInDayOfWeekRange extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
    public static final String FUNCTION_NAME = "urn:oasis:names:tc:xacml:3.0:function:dateTime-in-dayOfWeek-range";

    public DateTimeInDayOfWeekRange() {
        super(FUNCTION_NAME, StandardDatatypes.BOOLEAN, false, List.of(StandardDatatypes.DATETIME,
                                                                       DayOfWeekValue.TYPE,
                                                                       DayOfWeekValue.TYPE));
    }

    @Override
    public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions,
                                                        Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
        return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<>(functionSignature, argExpressions, remainingArgTypes) {
            @Override
            protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                XMLGregorianCalendar dateTimeValue = ((DateTimeValue) args.poll()).getUnderlyingValue();
                DayOfWeekType dayStartValue = ((DayOfWeekValue) args.poll()).getUnderlyingValue();
                DayOfWeekType dayEndValue = ((DayOfWeekValue) args.poll()).getUnderlyingValue();

                dateTimeValue = dateTimeValue.normalize();

                LocalDate currentDayOfWeekUTC = LocalDate.of(
                        dateTimeValue.getYear(),
                        dateTimeValue.getMonth(),
                        dateTimeValue.getDay());

                LocalTime currentTimeUTC = LocalTime.of(
                        dateTimeValue.getHour(),
                        dateTimeValue.getMinute(),
                        dateTimeValue.getSecond());

                LocalDateTime parameterDateTime = LocalDateTime.of(currentDayOfWeekUTC, currentTimeUTC);

                int startDay = dayStartValue.getDayOfWeek().getValue();
                int endDay = dayEndValue.getDayOfWeek().getValue();

                LocalDateTime normalizedDayStart = parameterDateTime;
                LocalDateTime normalizedDayEnd = parameterDateTime;

                // Run timezones against DateTime parameter, to see of day changes occur.
                if (dayStartValue.hasTimezone()) {
                    if (dayStartValue.isPositiveShift()) {
                        normalizedDayStart = parameterDateTime.plus(Duration.ofHours(dayStartValue.getHours()).plus(Duration.ofMinutes(dayStartValue.getMinutes())));
                    } else {
                        normalizedDayStart = parameterDateTime.minus(Duration.ofHours(dayStartValue.getHours()).plus(Duration.ofMinutes(dayStartValue.getMinutes())));
                    }
                }

                if (dayEndValue.hasTimezone()) {
                    if (dayEndValue.isPositiveShift()) {
                        normalizedDayEnd = parameterDateTime.plus(Duration.ofHours(dayEndValue.getHours()).plus(Duration.ofMinutes(dayEndValue.getMinutes())));
                    } else {
                        normalizedDayEnd = parameterDateTime.minus(Duration.ofHours(dayEndValue.getHours()).plus(Duration.ofMinutes(dayEndValue.getMinutes())));
                    }
                }

                int normalizedStartDay = startDay + (normalizedDayStart.getDayOfWeek().getValue() - parameterDateTime.getDayOfWeek().getValue());
                int normalizedEndDay = endDay + (normalizedDayEnd.getDayOfWeek().getValue() - parameterDateTime.getDayOfWeek().getValue());

                boolean res = false;

                if (normalizedStartDay <= normalizedEndDay) {
                    res = normalizedStartDay <= parameterDateTime.getDayOfWeek().getValue() &&
                            parameterDateTime.getDayOfWeek().getValue() <= normalizedEndDay;
                } else {
                    res = normalizedStartDay <= parameterDateTime.getDayOfWeek().getValue() ||
                            normalizedEndDay >= parameterDateTime.getDayOfWeek().getValue();
                }

                return new BooleanValue(res);
            }
        };
    }
}
