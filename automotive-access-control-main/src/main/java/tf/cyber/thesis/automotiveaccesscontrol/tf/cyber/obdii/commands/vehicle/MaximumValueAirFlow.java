package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class MaximumValueAirFlow extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 50";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 4];

        // B, C and D are reserved for future use.
        int b = bytes[bytes.length - 3];
        int c = bytes[bytes.length - 2];
        int d = bytes[bytes.length - 1];

        return a * 10;
    }

    @Override
    public String getFriendlyName() {
        return "Maximum value for air flow rate from mass air flow sensor (g/s)";
    }

    @Override
    public String getKey() {
        return "maximum_air_flow_rate";
    }
}
