package tf.cyber.thesis.automotiveaccesscontrol.pep.data;

import org.apache.commons.collections4.MapUtils;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public final class DataTypes {
    public final static Map<Class<?>, String> dataTypes = Map.of(
            String.class, "http://www.w3.org/2001/XMLSchema#string",
            Boolean.class, "http://www.w3.org/2001/XMLSchema#boolean",
            Integer.class, "http://www.w3.org/2001/XMLSchema#integer",
            Double.class, "http://www.w3.org/2001/XMLSchema#double",
            LocalTime.class, "http://www.w3.org/2001/XMLSchema#time",
            LocalDate.class, "http://www.w3.org/2001/XMLSchema#date",
            LocalDateTime.class, "http://www.w3.org/2001/XMLSchema#dateTime",
            URI.class, "http://www.w3.org/2001/XMLSchema#anyURI"
    );

    public final static Map<String, Class<?>> dataTypesReverse = MapUtils.invertMap(dataTypes);

    private DataTypes() {
        // do not instantiate.
    }

    public static String map(Class<?> o) {
        if (dataTypes.containsKey(o)) {
            return dataTypes.get(o);
        } else {
            throw new UnsupportedDataTypeException();
        }
    }

    public static Class<?> map(String s) {
        if (dataTypesReverse.containsKey(s)) {
            return dataTypesReverse.get(s);
        } else {
            throw new UnsupportedDataTypeException();
        }
    }
}
