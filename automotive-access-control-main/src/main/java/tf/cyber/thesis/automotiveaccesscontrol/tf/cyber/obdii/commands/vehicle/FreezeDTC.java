package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;

public class FreezeDTC extends OBD2Command<String> {
    @Override
    public String command() {
        return "01 02";
    }

    @Override
    public String result() {
        return rawData;
    }

    @Override
    public String getFriendlyName() {
        return "Freeze DTC";
    }

    @Override
    public String getKey() {
        return "freeze_dtc";
    }
}
