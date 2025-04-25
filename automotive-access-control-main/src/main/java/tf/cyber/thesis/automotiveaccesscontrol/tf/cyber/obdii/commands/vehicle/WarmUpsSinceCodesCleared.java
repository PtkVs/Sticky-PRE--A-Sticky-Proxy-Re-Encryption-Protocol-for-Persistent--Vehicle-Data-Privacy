package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class WarmUpsSinceCodesCleared extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 30";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Warm-ups since codes cleared";
    }

    @Override
    public String getKey() {
        return "warmups_since_codes_cleared";
    }
}
