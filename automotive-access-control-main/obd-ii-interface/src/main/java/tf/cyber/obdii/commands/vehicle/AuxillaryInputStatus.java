package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;

public class AuxillaryInputStatus extends OBD2Command<AuxillaryInputStatus.AuxillaryInputStatusValue> {
    @Override
    public String command() {
        return "01 1E";
    }

    @Override
    public AuxillaryInputStatusValue result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return AuxillaryInputStatusValue.fromResponse(bytes[bytes.length - 1]);
    }

    @Override
    public String getFriendlyName() {
        return "Auxiliary input status";
    }

    @Override
    public String getKey() {
        return "auxillary_input_status";
    }

    public enum AuxillaryInputStatusValue {
        PTO_INACTIVE(0), // power take off (e.g. used in tractors)
        PTO_ACTIVE(1);

        final int value;

        AuxillaryInputStatusValue(int value) {
            this.value = value;
        }

        static AuxillaryInputStatus.AuxillaryInputStatusValue fromResponse(int status) {
            return Arrays.stream(AuxillaryInputStatus.AuxillaryInputStatusValue.values())
                    .filter(val -> val.value == status)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
