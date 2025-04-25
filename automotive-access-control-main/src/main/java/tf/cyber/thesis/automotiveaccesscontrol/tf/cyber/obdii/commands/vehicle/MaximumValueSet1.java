package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

import java.util.List;

public class MaximumValueSet1 extends OBD2Command<List<Integer>> {
    @Override
    public String command() {
        return "01 4F";
    }

    @Override
    public List<Integer> result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a = bytes[bytes.length - 4];
        int b = bytes[bytes.length - 3];
        int c = bytes[bytes.length - 2];
        int d = bytes[bytes.length - 1];

        return List.of(a, b, c, d * 10);
    }

    @Override
    public String getFriendlyName() {
        return "Maximum value for Fuel–Air equivalence ratio (%), oxygen sensor voltage (V), oxygen sensor current (mA), and intake manifold absolute pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "max_fuel_air_eq_voltage_current_pressure";
    }
}
