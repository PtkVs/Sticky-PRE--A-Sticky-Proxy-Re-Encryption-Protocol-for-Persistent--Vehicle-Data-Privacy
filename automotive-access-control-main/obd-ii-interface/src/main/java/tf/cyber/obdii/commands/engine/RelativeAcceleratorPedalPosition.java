package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class RelativeAcceleratorPedalPosition extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 5A";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 1];
        return (100 / 255d) * a;
    }

    @Override
    public String getFriendlyName() {
        return "Relative accelerator pedal position (%)";
    }

    @Override
    public String getKey() {
        return "relative_accelerator_pedal_position";
    }
}
