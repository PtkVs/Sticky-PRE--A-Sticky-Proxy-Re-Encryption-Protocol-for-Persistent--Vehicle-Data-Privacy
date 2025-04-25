package tf.cyber.thesis.automotiveaccesscontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import tf.cyber.thesis.automotiveaccesscontrol.pep.XACMLInterceptor;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AspectConfig {
    @Bean
    public XACMLInterceptor xacmlInterceptor() {
        return new XACMLInterceptor();
    }
}
