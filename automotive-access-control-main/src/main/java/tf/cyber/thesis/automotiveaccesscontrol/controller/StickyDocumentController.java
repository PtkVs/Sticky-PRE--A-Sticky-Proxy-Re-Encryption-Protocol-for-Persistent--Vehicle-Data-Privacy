package tf.cyber.thesis.automotiveaccesscontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tf.cyber.thesis.automotiveaccesscontrol.dto.StickyDocumentDTO;
import tf.cyber.thesis.automotiveaccesscontrol.model.StickyDocument;
import tf.cyber.thesis.automotiveaccesscontrol.service.StickyDocumentService;
import tf.cyber.thesis.automotiveaccesscontrol.util.ObjectMapperUtils;

import java.util.List;

@RestController
@RequestMapping("/stickydocuments")
public class StickyDocumentController {
    @Autowired
    private StickyDocumentService stickyDocumentService;

    @GetMapping(value = "/")
    public List<StickyDocumentDTO> getAllStickyDocument() {
        return ObjectMapperUtils.mapAll(stickyDocumentService.findAll(), StickyDocumentDTO.class);
    }
    @GetMapping(value = "/byHashOfPolicy/{hashofpolicy}")
    public StickyDocumentDTO getStickyDocumentByHash(@PathVariable("hashofpolicy") String hashOfPolicy) {
        return ObjectMapperUtils.map(stickyDocumentService.findByHashOfPolicy(hashOfPolicy), StickyDocumentDTO.class);
    }
    @PostMapping(value = "/save")
    public ResponseEntity<?> saveOrUpdateStickyDocument(@RequestBody StickyDocumentDTO stickyDocumentDTO) {
        stickyDocumentService.saveStickyDocument(ObjectMapperUtils.map(stickyDocumentDTO, StickyDocument.class));
        return new ResponseEntity("Sticky document  added successfully", HttpStatus.OK);
    }


}
