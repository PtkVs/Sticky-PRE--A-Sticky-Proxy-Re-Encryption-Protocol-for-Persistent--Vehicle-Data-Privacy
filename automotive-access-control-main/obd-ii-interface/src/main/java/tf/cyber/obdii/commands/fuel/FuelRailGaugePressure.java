package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class FuelRailGaugePressure extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 23";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return 10d * (256 * a + b);
    }

    @Override
    public String getFriendlyName() {
        return " Fuel Rail Gauge Pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "fuel_rail_gauge_pressure";
    }
}
