package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class ControlModuleVoltage extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 42";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a  = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256 * a + b) / 1000d;
    }

    @Override
    public String getFriendlyName() {
        return "Control module voltage";
    }

    @Override
    public String getKey() {
        return "control_module_voltage";
    }
}
