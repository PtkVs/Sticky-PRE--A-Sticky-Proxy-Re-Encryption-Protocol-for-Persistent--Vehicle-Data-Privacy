package tf.cyber.thesis.automotiveaccesscontrol.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tf.cyber.thesis.automotiveaccesscontrol.model.StickyDocument;

@Repository
public interface StickyDocumentRepository extends MongoRepository<StickyDocument, String> {

    StickyDocument findByHashofpolicy(String hashofpolicy);

}
