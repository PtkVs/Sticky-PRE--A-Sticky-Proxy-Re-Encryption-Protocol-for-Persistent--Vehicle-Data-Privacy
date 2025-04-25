package tf.cyber.obdii.commands.engine;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.util.ByteUtils;

public class OxygenSensorsPresentFourBanks extends OBD2Command<Boolean[][]>{
    @Override
    public String command() {
        return "01 1D";
    }

    @Override
    public Boolean[][] result() {
        String[] bytesStr = rawData.split(" ");
        String charStr = bytesStr[bytesStr.length - 1];
        String[] chars = charStr.split("");

        // TODO: FIX THIS!
        Boolean[] bank1 = ByteUtils.byteToBooleanFourBits(Integer.parseInt(chars[0]));
        Boolean[] bank2 = ByteUtils.byteToBooleanFourBits(Integer.parseInt(chars[0]));

        return new Boolean[][] {
                bank1, bank2
        };
    }

    @Override
    public String getFriendlyName() {
        return "Oxygen sensors present (in 4 banks)";
    }

    @Override
    public String getKey() {
        return "oxygen_sensors_present_2";
    }
}
