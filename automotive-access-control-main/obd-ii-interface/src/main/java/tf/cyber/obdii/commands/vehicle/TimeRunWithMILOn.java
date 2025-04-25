package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class TimeRunWithMILOn extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 4D";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return 256 * a + b;
    }

    @Override
    public String getFriendlyName() {
        return null;
    }

    @Override
    public String getKey() {
        return "time_run_with_mil_on";
    }
}
