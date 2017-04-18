package act.angelman.network.service;

import java.io.Serializable;

public class UrlShortenerResponseData implements Serializable{

    String longUrl;
    String kind;
    String id;

    public UrlShortenerResponseData(String longUrl, String kind, String id) {
        this.longUrl = longUrl;
        this.kind = kind;
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }
    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}
