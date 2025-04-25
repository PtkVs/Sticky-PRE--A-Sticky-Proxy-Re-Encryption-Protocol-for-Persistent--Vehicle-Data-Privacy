package tf.cyber.resourcemanager.pep.mapper.toxacml;

import java.net.URI;

public class URIValueMapper implements JavaToXACMLMapper<URI> {
    @Override
    public String map(URI obj) {
        return obj.toString();
    }
}
