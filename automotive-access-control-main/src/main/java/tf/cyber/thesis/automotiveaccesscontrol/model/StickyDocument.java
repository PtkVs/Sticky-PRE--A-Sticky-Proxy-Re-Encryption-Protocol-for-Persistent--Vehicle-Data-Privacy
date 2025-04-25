package tf.cyber.thesis.automotiveaccesscontrol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("stickydocuments")
@NoArgsConstructor
public class StickyDocument {

    @Id
    private String id;
    private String hashofpolicy;
    private String data;

    public StickyDocument(String hashofpolicy, String data) {
        this.hashofpolicy = hashofpolicy;
        this.data = data;
    }

    public String getHashofpolicy() {
        return hashofpolicy;
    }

    public void setHashofpolicy(String hashofpolicy) {
        this.hashofpolicy = hashofpolicy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "StickyDocument{" +
                "id='" + id + '\'' +
                ", hashofpolicy='" + hashofpolicy + '\'' +
                ", data=" + data +
                '}';
    }

}
