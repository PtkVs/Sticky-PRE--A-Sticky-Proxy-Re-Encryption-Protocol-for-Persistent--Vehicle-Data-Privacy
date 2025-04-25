package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EngineFuelRate extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 5E";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256d * a + b) / 128 - 210;
    }

    @Override
    public String getFriendlyName() {
        return "Engine fuel rate (L/h)";
    }

    @Override
    public String getKey() {
        return "engine_fuel_rate";
    }
}
