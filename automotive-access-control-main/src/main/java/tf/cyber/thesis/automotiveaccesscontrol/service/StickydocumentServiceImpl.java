package tf.cyber.thesis.automotiveaccesscontrol.service;

//import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tf.cyber.thesis.automotiveaccesscontrol.model.StickyDocument;
import tf.cyber.thesis.automotiveaccesscontrol.repository.StickyDocumentRepository;

import java.util.List;

@Service
@Repository

public class StickydocumentServiceImpl implements StickyDocumentService {

    @Autowired
    private StickyDocumentRepository stickyDocumentRepository;

    @Override
    public List<StickyDocument> findAll() {
        return stickyDocumentRepository.findAll();
    }

    @Override
    public StickyDocument findByHashOfPolicy(String hashofpolicy) {
        return stickyDocumentRepository.findByHashofpolicy(hashofpolicy);
    }

    @Override
    public StickyDocument saveStickyDocument(StickyDocument stickyDocument) {
        return stickyDocumentRepository.save(stickyDocument);
    }
}
