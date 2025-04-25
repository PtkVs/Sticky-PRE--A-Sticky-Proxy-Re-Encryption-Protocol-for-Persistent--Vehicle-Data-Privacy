package tf.cyber.obdii.commands.vehicle;

import tf.cyber.obdii.commands.OBD2Command;

public class Odometer extends OBD2Command<Double> {
    @Override
    public String command() {
        return "01 A6";
    }

    @Override
    public Double result() {
        System.out.println(rawData);
        return 0.0d;
    }

    @Override
    public String getFriendlyName() {
        return "Odometer";
    }

    @Override
    public String getKey() {
        return "odometer";
    }
}
