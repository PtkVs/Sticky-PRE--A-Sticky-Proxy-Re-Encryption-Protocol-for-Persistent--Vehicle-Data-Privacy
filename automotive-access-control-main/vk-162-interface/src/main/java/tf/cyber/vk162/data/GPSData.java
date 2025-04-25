package tf.cyber.vk162.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class GPSData {
    private String header;
    private double timestamp;

    private BigDecimal longitude;
    private BigDecimal latitude;

    private GPSQuality quality;
    private int usedSatellites;

    private double antennaAltitude;
    private AltitudeUnit antennaAltitudeUnit;

    public enum GPSQuality {
        NO_CONNECTION,
        UNCORRECTED_COORDINATE,
        DIFFERENTIALLY_CORRECT_COORDINATE,
        RTK_FIX_COORDINATE,
        RTK_FLOAT;

        public static GPSQuality fromResponse(int id) {
            if (id == 0) {
                return NO_CONNECTION;
            }

            if (id == 1) {
                return UNCORRECTED_COORDINATE;
            }

            if (id == 2) {
                return DIFFERENTIALLY_CORRECT_COORDINATE;
            }

            if (id == 4) {
                return RTK_FIX_COORDINATE;
            }

            if (id == 5) {
                return RTK_FLOAT;
            }

            throw new RuntimeException("Could not determine GPS quality level!");
        }
    }

    public enum AltitudeUnit {
        METER, FEET;

        public static AltitudeUnit fromResponse(String id) {
            if (id.equals("M")) {
                return METER;
            }

            if (id.equals("F")) {
                return FEET;
            }

            throw new RuntimeException("Could not determine antenna height unit!");
        }
    }

    @Override
    public String toString() {
        return "GPSData{" +
                "header='" + header + '\'' +
                ", timestamp=" + timestamp +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", quality=" + quality +
                ", usedSatellites=" + usedSatellites +
                ", antennaAltitude=" + antennaAltitude +
                ", antennaAltitudeUnit=" + antennaAltitudeUnit +
                '}';
    }
}
