package tf.cyber.obdii.commands.connection;

import tf.cyber.obdii.commands.OBD2Command;

public class ResetELM extends OBD2Command<String> {

    @Override
    public String command() {
        return "atz";
    }

    @Override
    public String result() {
        return rawData;
    }

    @Override
    public String getFriendlyName() {
        return "Reset ELM327";
    }

    @Override
    public String getKey() {
        return "reset_elm";
    }
}
