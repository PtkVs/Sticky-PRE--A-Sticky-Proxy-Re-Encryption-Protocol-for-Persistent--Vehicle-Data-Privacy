package tf.cyber.resourcemanager.pep.mapper.toxacml;

public class LongValueMapper implements JavaToXACMLMapper<Long> {
    @Override
    public String map(Long obj) {
        return obj.toString();
    }
}
