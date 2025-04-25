package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class FuelPressure extends OBD2Command<Integer> {
    @Override
    public String command() {
        return "01 0A";
    }

    @Override
    public Integer result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return 3 * bytes[bytes.length - 1];
    }

    @Override
    public String getFriendlyName() {
        return "Fuel Pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "fuel_pressure";
    }
}
