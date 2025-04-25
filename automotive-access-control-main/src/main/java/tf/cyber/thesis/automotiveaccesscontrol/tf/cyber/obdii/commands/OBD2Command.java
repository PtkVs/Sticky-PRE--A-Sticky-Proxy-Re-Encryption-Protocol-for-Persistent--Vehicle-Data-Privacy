package tf.cyber.obdii.commands;

import jssc.SerialPortException;
import tf.cyber.obdii.exceptions.NoDataException;
import tf.cyber.obdii.io.OBD2Connection;
import tf.cyber.obdii.exceptions.CANErrorException;
import tf.cyber.obdii.exceptions.InvalidCommandException;

public abstract class OBD2Command<R> {
    protected String rawData = null;

    public abstract String command();

    public abstract R result();

    public abstract String getFriendlyName();

    public abstract String getKey();

    public void execute(OBD2Connection connection) throws SerialPortException, InterruptedException {
        String res = null;

        synchronized (connection) {
            connection.write(this);
            res = connection.read();
        }

        if (res.equals("?")) {
            throw new InvalidCommandException();
        }

        if (res.equals("CAN ERROR")) {
            throw new CANErrorException();
        }

        if (res.equals("NO DATA")) {
            throw new NoDataException();
        }

        this.rawData = res;
    }
}
