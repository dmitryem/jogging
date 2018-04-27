package yellow.jogging.beans;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "image")
public class Image {


    private String url;
    private int id;
    private int jogging_id;

    @Column(nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", updatable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @JsonIgnore
    @Column(name = "jogging_id")
    public int getJogging() {
        return jogging_id;
    }


    public void setJogging(int jogging) {
        this.jogging_id = jogging;
    }
}
