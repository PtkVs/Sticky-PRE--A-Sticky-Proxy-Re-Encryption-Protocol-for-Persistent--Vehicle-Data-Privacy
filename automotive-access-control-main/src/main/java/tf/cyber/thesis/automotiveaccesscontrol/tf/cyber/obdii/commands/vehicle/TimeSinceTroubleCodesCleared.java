package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class TimeSinceTroubleCodesCleared extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 4E";
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
        return "Time since trouble codes cleared";
    }

    @Override
    public String getKey() {
        return "time_since_clodes_cleared";
    }
}
