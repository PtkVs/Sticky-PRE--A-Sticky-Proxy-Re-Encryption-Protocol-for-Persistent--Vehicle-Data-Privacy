package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

public interface JavaToXACMLMapper<T extends Object> {
    public String map(T obj);
}
