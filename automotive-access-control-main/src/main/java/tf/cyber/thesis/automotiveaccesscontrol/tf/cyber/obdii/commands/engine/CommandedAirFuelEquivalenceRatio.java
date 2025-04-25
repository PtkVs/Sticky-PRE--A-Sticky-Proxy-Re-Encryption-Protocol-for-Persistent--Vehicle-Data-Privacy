package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class CommandedAirFuelEquivalenceRatio extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 44";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);

        int a  = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return (2 / 65536d) * (256 * a + b);
    }

    @Override
    public String getFriendlyName() {
        return "Commanded Air-Fuel Equivalence Ratio (lambda,λ)";
    }

    @Override
    public String getKey() {
        return "commanded_air_fuel_equivalence_ratio";
    }
}
