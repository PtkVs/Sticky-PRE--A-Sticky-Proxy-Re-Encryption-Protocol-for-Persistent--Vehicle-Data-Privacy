package cyber.tf.accesslogservice.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "logs")
public class LogEntry extends BaseEntity {
    public String subject;
    public String action;
    public String resource;

    public long time;

    public LogEntry() {

    }

    public LogEntry(String subject, String action, String resource, long time) {
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry logEntry = (LogEntry) o;
        return time == logEntry.time && Objects.equals(subject, logEntry.subject) && Objects.equals(action, logEntry.action) && Objects.equals(resource, logEntry.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, action, resource, time);
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", action='" + action + '\'' +
                ", resource='" + resource + '\'' +
                ", time=" + time +
                '}';
    }
}
