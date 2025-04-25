package tf.cyber.resourcemanager.pep.mapper.toxacml;

public class ShortValueMapper implements JavaToXACMLMapper<Short> {
    @Override
    public String map(Short obj) {
        return obj.toString();
    }
}
