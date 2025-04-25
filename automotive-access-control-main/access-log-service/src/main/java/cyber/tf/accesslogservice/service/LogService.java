package cyber.tf.accesslogservice.service;

import cyber.tf.accesslogservice.model.LogEntry;
import cyber.tf.accesslogservice.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class LogService {

    @Autowired
    LogRepository logRepository;

    public LogEntry storeEntry(LogEntry entry) {
        entry.setTime(Calendar.getInstance().getTimeInMillis());
        return logRepository.save(entry);
    }

    public LogEntry queryLastAccess(LogEntry entry) {
        if (entry.getSubject() == null) {
            throw new IllegalArgumentException("Subject must not be null!");
        }

        if (entry.getAction() == null) {
            throw new IllegalArgumentException("Action must not be null!");
        }

        if (entry.getResource() == null) {
            throw new IllegalArgumentException("Resource must not be null!");
        }

        return logRepository.findFirstBySubjectAndActionAndResourceOrderByTimeDesc(entry.getSubject(),
                                                                           entry.getAction(),
                                                                           entry.getResource());
    }
}
