package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;
import tf.cyber.obdii.util.Pair;

public class LongTermSecondaryOxygenSensorTrimBank1Bank3 extends OBD2Command<Pair<Double, Double>> {
    @Override
    public String command() {
        return "01 56";
    }

    @Override
    public Pair<Double, Double> result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        double aRes = (100 / 128d) * a - 100;
        double bRes = (100 / 128d) * b - 100;

        return Pair.of(aRes, bRes);
    }

    @Override
    public String getFriendlyName() {
        return "Long term secondary oxygen sensor trim, A: bank 1, B: bank 3 (%)";
    }

    @Override
    public String getKey() {
        return "long_term_secondary_oxygen_sensor_trim_bank1_bank3";
    }
}
