package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class AbsoluteBarometricPressure extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 33";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Absolute Barometric Pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "absolute_barometric_pressure_kpa";
    }
}
