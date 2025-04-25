package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class VehicleSpeed extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 0D";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Vehicle Speed";
    }

    @Override
    public String getKey() {
        return "vehicle_speed";
    }
}
