package tf.cyber.obdii.commands.connection;

import tf.cyber.obdii.commands.OBD2Command;

public class DisableEcho extends OBD2Command<String> {
    @Override
    public String command() {
        return "ATE0";
    }

    @Override
    public String result() {
        return rawData;
    }

    @Override
    public String getFriendlyName() {
        return "Disable Echo";
    }

    @Override
    public String getKey() {
        return "disable_echo";
    }
}
