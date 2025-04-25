package tf.cyber.thesis.automotiveaccesscontrol.pep.mapper.toxacml;

import java.net.URI;

public class URIValueMapper implements JavaToXACMLMapper<URI> {
    @Override
    public String map(URI obj) {
        return obj.toString();
    }
}
