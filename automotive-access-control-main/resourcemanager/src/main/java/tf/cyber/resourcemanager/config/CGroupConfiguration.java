package tf.cyber.resourcemanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystemImpl;

import java.nio.file.Paths;

@Configuration
public class CGroupConfiguration {
    @Autowired
    Environment env;

    @Bean
    @Scope("singleton")
    public CGroupFileSystem getCGroupFileSystem() {
        return null;
    }
}
