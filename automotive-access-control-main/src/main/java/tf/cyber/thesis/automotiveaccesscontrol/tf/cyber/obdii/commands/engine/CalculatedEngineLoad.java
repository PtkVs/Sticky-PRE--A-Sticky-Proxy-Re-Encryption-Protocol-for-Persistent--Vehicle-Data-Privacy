package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;

public class CalculatedEngineLoad extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 04";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1] / 255d;
    }

    @Override
    public String getFriendlyName() {
        return "Calculated Engine Load (%)";
    }

    @Override
    public String getKey() {
        return "calculated_engine_load";
    }
}
