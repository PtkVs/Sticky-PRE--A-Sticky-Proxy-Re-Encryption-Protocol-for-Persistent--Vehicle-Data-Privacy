package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class AcceleratorPedalPositionD extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 49";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (100 / 255d) * bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Accelerator pedal position D (%)";
    }

    @Override
    public String getKey() {
        return "accelerator_pedal_position_d";
    }
}
