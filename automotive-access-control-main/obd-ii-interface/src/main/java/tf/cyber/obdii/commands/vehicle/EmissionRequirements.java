package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;

public class EmissionRequirements extends OBD2Command<String> {
    @Override
    public String command() {
        return "01 5F";
    }

    @Override
    public String result() {
        return rawData;
    }

    @Override
    public String getFriendlyName() {
        return "Emission requirements to which the vehicle is designed ";
    }

    @Override
    public String getKey() {
        return "emission_requirements";
    }
}
