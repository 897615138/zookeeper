package framework.kafka.model;

import java.io.Serializable;

/**
 * @author JillW
 */
public class DemoObj implements Serializable {
    private static final long serialVersionUID = -8094247978023094250L;
    private Long id;
    private String data;

    public DemoObj() {

    }

    public DemoObj(Long id, String data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "DemoObj [id=" + id + ", data=" + data + "]";
    }

}
