package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class EngineSpeed extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 0C";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (256 * a + b) / 4d;
    }

    @Override
    public String getFriendlyName() {
        return "Engine Speed (rpm)";
    }

    @Override
    public String getKey() {
        return "engine_speed";
    }
}
