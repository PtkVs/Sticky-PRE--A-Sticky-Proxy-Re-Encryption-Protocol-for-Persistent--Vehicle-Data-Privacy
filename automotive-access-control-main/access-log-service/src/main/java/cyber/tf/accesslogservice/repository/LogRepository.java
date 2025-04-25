package cyber.tf.accesslogservice.repository;

import cyber.tf.accesslogservice.model.LogEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends CrudRepository<LogEntry, Long> {
    LogEntry findFirstBySubjectAndActionAndResourceOrderByTimeDesc(String subject, String Action, String Resource);
}
