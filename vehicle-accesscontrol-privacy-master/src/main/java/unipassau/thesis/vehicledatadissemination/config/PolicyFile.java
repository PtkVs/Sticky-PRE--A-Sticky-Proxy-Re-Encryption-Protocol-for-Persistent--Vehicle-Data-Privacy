package unipassau.thesis.vehicledatadissemination.config;

import java.io.File;

public class PolicyFile {
    private String name;
    private File file;

    public PolicyFile(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }
}
