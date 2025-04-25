package tf.cyber.obdii.commands.fuel;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class ShortTermFuelTrimBank1 extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 06";
    }

    @Override
    public Double result() {
        int[] bytes = ByteUtils.extractBytes(rawData);
        return ((100d / 128d) * bytes[bytes.length - 1] - 100d);
    }

    @Override
    public String getFriendlyName() {
        return "Short Term Fuel Trim — Bank 1 (%)";
    }

    @Override
    public String getKey() {
        return "short_term_fuel_trim_bank1";
    }
}
