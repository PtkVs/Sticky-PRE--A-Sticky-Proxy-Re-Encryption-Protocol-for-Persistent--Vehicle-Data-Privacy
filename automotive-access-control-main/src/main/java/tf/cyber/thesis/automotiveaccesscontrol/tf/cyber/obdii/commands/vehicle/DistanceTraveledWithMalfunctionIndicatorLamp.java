package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class DistanceTraveledWithMalfunctionIndicatorLamp extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 21";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return 256 * bytes[bytes.length - 2] + bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Distance traveled with malfunction indicator lamp on (km)";
    }

    @Override
    public String getKey() {
        return "distance_traveled_with_indicator_lamp_on";
    }
}
