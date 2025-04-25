package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class AbsoluteEvaporationSystemVaporPressure extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 53";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        int a = bytes[bytes.length - 2];
        int b = bytes[bytes.length - 1];

        return ((256 * a + b) / 200d);
    }

    @Override
    public String getFriendlyName() {
        return "Absolute Evap system Vapor Pressure (kPa)";
    }

    @Override
    public String getKey() {
        return "absolute_evaporation_system_vapor_pressure_kpa";
    }
}
