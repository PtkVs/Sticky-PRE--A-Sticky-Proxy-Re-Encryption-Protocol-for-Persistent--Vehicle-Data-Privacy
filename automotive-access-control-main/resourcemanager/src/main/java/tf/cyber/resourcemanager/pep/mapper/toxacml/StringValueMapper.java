package tf.cyber.resourcemanager.pep.mapper.toxacml;

public class StringValueMapper implements JavaToXACMLMapper<String> {
    @Override
    public String map(String obj) {
        return obj;
    }
}
