package tf.cyber.obdii.commands.protocol;

import tf.cyber.obdii.commands.OBD2Command;

public class ProtocolSelector extends OBD2Command<String>  {
    Protocol protocol;

    public ProtocolSelector(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public String command() {
        return "atsp" + protocol.identifier;
    }

    @Override
    public String result() {
        return rawData;
    }

    @Override
    public String getFriendlyName() {
        return "Select protocol";
    }

    @Override
    public String getKey() {
        return "protocol_selector";
    }

    public enum Protocol {
        AUTOMATIC(0),
        SAE_J1850_PWM(1),
        SAE_J1850_VPW(2),
        ISO_9141_2(3),
        ISO_142340_4_KWP(4),
        ISO_142340_4_KWP_FAST_INIT(5),
        ISO_15765_4_CAN_11BIT_500BAUD(6),
        ISO_15765_4_CAN_29BIT_500BAUD(7),
        ISO_15765_4_CAN_11BIT_250BAUD(8),
        ISO_15765_4_CAN_29BIT_250BAUD(9);

        private final int identifier;

        Protocol(int identifier) {
            this.identifier = identifier;
        }
    }
}
