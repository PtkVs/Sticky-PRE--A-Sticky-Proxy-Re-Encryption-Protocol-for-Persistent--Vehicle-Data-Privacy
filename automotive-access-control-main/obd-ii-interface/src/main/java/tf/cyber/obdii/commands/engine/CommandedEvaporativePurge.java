package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class CommandedEvaporativePurge extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 2E";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return (100 / 255d) * bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Commanded evaporative purge (%)";
    }

    @Override
    public String getKey() {
        return "commanded_evaporative_purge";
    }
}
