package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class FuelRailAbsolutePressure extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 59";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return 10 * (256 * a + b);
    }

    @Override
    public String getFriendlyName() {
        return "Fuel rail absolute pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "fuel_rail_absolute_pressure";
    }
}
