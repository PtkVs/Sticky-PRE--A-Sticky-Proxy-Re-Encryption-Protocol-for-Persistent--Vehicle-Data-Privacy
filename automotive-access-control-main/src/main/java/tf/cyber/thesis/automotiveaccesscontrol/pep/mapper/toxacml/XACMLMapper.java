package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

import tf.cyber.thesis.automotiveaccesscontrol.pep.data.UnsupportedDataTypeException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class XACMLMapper {
    public final static Map<Class<?>, JavaToXACMLMapper> dataMapper = Map.of(
            String.class, new StringValueMapper(),
            Boolean.class, new BooleanValueMapper(),
            Integer.class, new IntegerValueMapper(),
            Double.class, new DoubleValueMapper(),
            LocalTime.class, new LocalTimeValueMapper(),
            LocalDate.class, new LocalDateValueMapper(),
            LocalDateTime.class, new LocalDateTimeValueMapper(),
            URI.class, new URIValueMapper()
    );

    private XACMLMapper() {
        // do not instantiate.
    }

    public static String map(Object o) {
        if (dataMapper.containsKey(o.getClass())) {
            return dataMapper.get(o.getClass()).map(o);
        } else {
            throw new UnsupportedDataTypeException();
        }
    }
}
