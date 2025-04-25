package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;
import tf.cyber.obdii.util.Pair;

public class OxygenSensor8Tertiary extends OBD2Command<Pair<Double, Double>> {
    @Override
    public String command() {
        return "01 3B";
    }

    @Override
    public Pair<Double, Double> result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 4];
        int b = bytes[bytes.length - 3];
        int c = bytes[bytes.length - 2];
        int d = bytes[bytes.length - 1];

        double ratio = (2 / 65536d) * (256 * a + b);
        double current = ((256 * c + d) / 256d) - 128;

        return Pair.of(ratio, current);
    }

    @Override
    public String getFriendlyName() {
        return "Oxygen Sensor 8 - (Air-Fuel Equivalence Ratio, Current)";
    }

    @Override
    public String getKey() {
        return "oxygen_sensor8_afer_current";
    }
}
