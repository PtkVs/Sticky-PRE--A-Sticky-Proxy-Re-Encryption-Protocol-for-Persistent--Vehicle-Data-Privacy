package tf.cyber.resourcemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystem;
import tf.cyber.resourcemanager.cgroup.CGroupFileSystemImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class CGroupService {
    @Autowired
    private Environment env;

    private Map<String, CGroupFileSystem> cgroups;

    public CGroupService() {
        this.cgroups = new HashMap<>();
    }

    public CGroupFileSystem getCGroupFileSystem() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (env.getProperty("cgroup.fs.homedir") == null) {
            throw new IllegalArgumentException("Could not find cgroup.fs.homedir in application.properties." +
                                                       "Please fix your configuration.");
        }

        String cgroupHome = env.getProperty("cgroup.fs.homedir");

        String user = authentication.getName();
        cgroups.putIfAbsent(user, new CGroupFileSystemImpl(Path.of(cgroupHome), user));
        return cgroups.get(user);
    }
}
