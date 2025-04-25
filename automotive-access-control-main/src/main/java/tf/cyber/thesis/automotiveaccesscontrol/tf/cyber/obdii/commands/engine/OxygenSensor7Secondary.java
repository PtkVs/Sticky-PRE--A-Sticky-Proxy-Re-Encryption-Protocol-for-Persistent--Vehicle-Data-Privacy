package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;
import tf.cyber.obdii.util.Pair;

public class OxygenSensor7Secondary extends OBD2Command<Pair<Double, Double>> {
    @Override
    public String command() {
        return "01 2A";
    }

    @Override
    public Pair<Double, Double> result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 4];
        int b = bytes[bytes.length - 3];
        int c = bytes[bytes.length - 2];
        int d = bytes[bytes.length - 1];

        double ratio = (2 / 65536d) * (256 * a + b);
        double voltage = (8 / 65536d) * (256 * c + d);

        return Pair.of(ratio, voltage);
    }

    @Override
    public String getFriendlyName() {
        return "Oxygen Sensor 7 - (Air-Fuel Equivalence Ratio, Voltage)";
    }

    @Override
    public String getKey() {
        return "oxygen_sensor7_afer_voltage";
    }
}
