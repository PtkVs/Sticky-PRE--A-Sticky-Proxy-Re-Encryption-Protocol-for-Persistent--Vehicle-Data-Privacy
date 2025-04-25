package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class TimingAdvance extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 0E";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (bytes[bytes.length - 1] / 2d) - 64d;
    }

    @Override
    public String getFriendlyName() {
        return "Timing Advance (Ignition timing, degrees before TDC)";
    }

    @Override
    public String getKey() {
        return "ignition_timing_advance";
    }
}
