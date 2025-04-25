package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class DistanceTraveledSinceCodesCleared extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 31";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return 256 * bytes[bytes.length - 2] + bytes[bytes.length -1];
    }

    @Override
    public String getFriendlyName() {
        return "Distance traveled since codes cleared";
    }

    @Override
    public String getKey() {
        return "distance_traveled_since_codes_cleared";
    }
}
