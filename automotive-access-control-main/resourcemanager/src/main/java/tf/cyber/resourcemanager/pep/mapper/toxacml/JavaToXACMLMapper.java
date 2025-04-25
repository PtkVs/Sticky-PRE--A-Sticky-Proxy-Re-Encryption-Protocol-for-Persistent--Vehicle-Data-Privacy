package tf.cyber.resourcemanager.pep.mapper.toxacml;

public interface JavaToXACMLMapper<T extends Object> {
    public String map(T obj);
}
