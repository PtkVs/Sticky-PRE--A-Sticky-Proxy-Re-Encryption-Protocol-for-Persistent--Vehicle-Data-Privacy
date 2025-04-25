package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;
import java.util.List;

public class OBDStandardCompliance extends OBD2Command<OBDStandardCompliance.OBDStandardComplianceValue> {
    @Override
    public String command() {
        return "01 1C";
    }

    @Override
    public OBDStandardComplianceValue result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return OBDStandardComplianceValue.fromResponse(bytes[bytes.length - 1]);
    }

    @Override
    public String getFriendlyName() {
        return "Conforming OBD standards of the vehicle";
    }

    @Override
    public String getKey() {
        return "obd_compliance";
    }

    public enum OBDStandardComplianceValue {
        OBD2_CARB(1),
        OBD_EPA(2),
        OBD_AND_OBD2(3),
        OBD1(4),
        NOT_OBD_COMPLIANT(5),
        EOBD(6),
        EOBD_AND_OBD2(7),
        EOBD_AND_OBD1(8),
        EOBD_AND_OBD1_AND_OBD2(9),
        JOBD(10),
        JOBD_AND_OBD2(11),
        JOBD_AND_EOBD(12),
        JOBD_AND_EOBD_AND_OBD2(13),
        EMD(17),
        EMD_PLUS(18),
        HD_OBD_C(19),
        HD_OBD(20),
        WWH_OBD(21),
        HD_EOBD1(23),
        HD_EOBD1_N(24),
        HD_EOBD2(25),
        HD_EOBD2_N(26),
        OBDBr_1(28),
        OBDBr_2(29),
        KOBD(30),
        IOBD1(31),
        IPND2(32),
        HD_EOBD_IV(33),
        RESERVED(-1),
        NOT_AVAILABLE_FOR_ASSIGNMENT(-1);

        final int value;
        static final List<Integer> reserved = List.of(14, 15, 16, 22, 27);

        OBDStandardComplianceValue(int value) {
            this.value = value;
        }

        static OBDStandardCompliance.OBDStandardComplianceValue fromResponse(int status) {
            if (reserved.contains(status) || (status >= 34 && status <= 250)) {
                return OBDStandardComplianceValue.RESERVED;
            }

            if ((status >= 251 && status <= 255)) {
                return OBDStandardComplianceValue.NOT_AVAILABLE_FOR_ASSIGNMENT;
            }

            return Arrays.stream(OBDStandardCompliance.OBDStandardComplianceValue.values())
                    .filter(val -> val.value == status)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
