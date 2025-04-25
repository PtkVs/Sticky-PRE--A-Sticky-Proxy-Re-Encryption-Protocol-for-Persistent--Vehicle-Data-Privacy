package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class AbsoluteLoadValue extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 43";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a  = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (100d / 255d) * (256 * a + b);
    }

    @Override
    public String getFriendlyName() {
        return "Absolute load value (%)";
    }

    @Override
    public String getKey() {
        return "absolute_load_value";
    }
}
