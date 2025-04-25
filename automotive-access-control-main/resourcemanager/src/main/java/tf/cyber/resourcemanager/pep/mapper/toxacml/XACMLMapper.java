package tf.cyber.resourcemanager.pep.mapper.toxacml;

import tf.cyber.resourcemanager.pep.data.UnsupportedDataTypeException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Map;

public class XACMLMapper {
    public final static Map<Class<?>, JavaToXACMLMapper> dataMapper = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(String.class, new StringValueMapper()),
            new AbstractMap.SimpleEntry<>(Boolean.class, new BooleanValueMapper()),
            new AbstractMap.SimpleEntry<>(Integer.class, new IntegerValueMapper()),
            new AbstractMap.SimpleEntry<>(int.class, new IntegerValueMapper()),
            new AbstractMap.SimpleEntry<>(Double.class, new DoubleValueMapper()),
            new AbstractMap.SimpleEntry<>(double.class, new DoubleValueMapper()),
            new AbstractMap.SimpleEntry<>(Short.class, new ShortValueMapper()),
            new AbstractMap.SimpleEntry<>(short.class, new ShortValueMapper()),
            new AbstractMap.SimpleEntry<>(Long.class, new LongValueMapper()),
            new AbstractMap.SimpleEntry<>(long.class, new LongValueMapper()),
            new AbstractMap.SimpleEntry<>(LocalTime.class, new LocalTimeValueMapper()),
            new AbstractMap.SimpleEntry<>(LocalDate.class, new LocalDateValueMapper()),
            new AbstractMap.SimpleEntry<>(LocalDateTime.class, new LocalDateTimeValueMapper()),
            new AbstractMap.SimpleEntry<>(URI.class, new URIValueMapper())
    );

    private XACMLMapper() {
        // do not instantiate.
    }

    public static String map(Object o) {
        if (dataMapper.containsKey(o.getClass())) {
            return dataMapper.get(o.getClass()).map(o);
        } else {
            throw new UnsupportedDataTypeException(o.getClass());
        }
    }
}
