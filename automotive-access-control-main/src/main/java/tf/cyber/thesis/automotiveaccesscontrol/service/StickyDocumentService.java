package tf.cyber.thesis.automotiveaccesscontrol.service;

import org.springframework.stereotype.Service;
import tf.cyber.thesis.automotiveaccesscontrol.model.StickyDocument;

import java.util.List;


public interface StickyDocumentService {


    List<StickyDocument> findAll();
    StickyDocument findByHashOfPolicy(String hashofpolicy);
    StickyDocument saveStickyDocument(StickyDocument stickyDocument);

}
