package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class RelativeThrottlePosition extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 45";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (100 / 255d) * bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Relative Throttle Position (%)";
    }

    @Override
    public String getKey() {
        return "relative_throttle_position";
    }
}
