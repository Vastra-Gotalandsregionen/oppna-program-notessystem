package se.vgregion.core.domain.calendar;

import java.io.Serializable;

/**
 * @author Patrik Bergström
 */
public class ExternalSource implements Serializable {

    private static final long serialVersionUID = -7927581046256212908L;

    private String key;
    private String url;

    public ExternalSource(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExternalSource that = (ExternalSource) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
