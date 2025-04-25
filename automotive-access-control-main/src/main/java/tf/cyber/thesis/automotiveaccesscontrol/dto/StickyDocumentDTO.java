package tf.cyber.thesis.automotiveaccesscontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class StickyDocumentDTO {

    private String hashofpolicy;
    private String data;

    public String getHashofpolicy() {
        return hashofpolicy;
    }

    public void setHashofpolicy(String hashofpolicy) {
        this.hashofpolicy = hashofpolicy;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
