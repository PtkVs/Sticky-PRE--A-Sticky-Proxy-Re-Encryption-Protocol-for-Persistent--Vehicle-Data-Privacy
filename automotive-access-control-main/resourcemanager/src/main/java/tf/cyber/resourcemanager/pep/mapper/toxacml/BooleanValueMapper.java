package tf.cyber.resourcemanager.pep.mapper.toxacml;

public class BooleanValueMapper implements JavaToXACMLMapper<Boolean> {
    @Override
    public String map(Boolean obj) {
        return obj.toString();
    }
}
