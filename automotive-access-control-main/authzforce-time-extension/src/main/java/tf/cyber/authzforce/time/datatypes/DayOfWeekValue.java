package tf.cyber.authzforce.time.datatypes;

import net.sf.saxon.s9api.XPathCompiler;
import org.ow2.authzforce.core.pdp.api.value.AttributeDatatype;
import org.ow2.authzforce.core.pdp.api.value.BaseAttributeValueFactory;
import org.ow2.authzforce.core.pdp.api.value.SimpleValue;
import tf.cyber.authzforce.time.datatypes.java.DayOfWeekType;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DayOfWeekValue extends SimpleValue<DayOfWeekType> {
    public static final AttributeDatatype<DayOfWeekValue> TYPE =
            new AttributeDatatype<>(DayOfWeekValue.class,
                                    "urn:oasis:names:tc:xacml:3.0:data‑type:dayOfWeek",
                                    "urn:oasis:names:tc:xacml:3.0:data‑type:dayOfWeek");

    private static final String VALUE_REGEX = "^[1-7]((\\+|-)(0\\d|1[0-3]):[0-5]\\d)?$";


    protected DayOfWeekValue(DayOfWeekType val) throws IllegalArgumentException {
        super(val);
    }

    @Override
    public String printXML() {
        DayOfWeekType type = this.getUnderlyingValue();

        StringBuilder sb = new StringBuilder();
        sb.append(type.getDayOfWeek().getValue());
        sb.append(type.isPositiveShift() ? "+" : "-");
        sb.append(String.format("%02d", value.getHours()));
        sb.append(":");
        sb.append(String.format("%02d", value.getMinutes()));

        return sb.toString();
    }

    @Override
    public Map<QName, String> getXmlAttributes() {
        return new HashMap<QName, String>();
    }

    public static final class Factory extends BaseAttributeValueFactory<DayOfWeekValue> {
        public Factory() {
            super(TYPE);
        }

        @Override
        public DayOfWeekValue getInstance(List<Serializable> content,
                                          Map<QName, String> otherAttributes,
                                          XPathCompiler xPathCompiler) throws IllegalArgumentException {
            Iterator<Serializable> iterator = content.iterator();

            while (iterator.hasNext()) {
                Serializable serializable = iterator.next();
                if (!(serializable instanceof String)) {
                    throw new IllegalArgumentException("Invalid encoding for " + TYPE.getId());
                }

                String value = (String) serializable;

                if (value.matches(VALUE_REGEX)) {
                    DayOfWeek dayOfWeek = DayOfWeek.of(Integer.parseInt(value.substring(0, 1)));
                    DayOfWeekType res = new DayOfWeekType();

                    if (value.length() != 1) {
                        boolean positiveShift = value.charAt(1) == '+';

                        String timezoneComplete = value.substring(2);
                        String[] timezoneParts = timezoneComplete.split(":");

                        int hours = Integer.parseInt(timezoneParts[0]);
                        int minutes = Integer.parseInt(timezoneParts[1]);

                        res.setPositiveShift(positiveShift);
                        res.setHours(hours);
                        res.setMinutes(minutes);
                    }

                    res.setDayOfWeek(dayOfWeek);
                    return new DayOfWeekValue(res);
                } else {
                    throw new IllegalArgumentException("Invalid format for " + TYPE.getId());
                }
            }
            throw new IllegalArgumentException("Invalid format for " + TYPE.getId());
        }
    }
}
