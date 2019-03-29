package ua.com.arturmamedov.magist.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class GistFile {
    private String filename;
    private String type;
    private String language;
    @SerializedName("raw_url")
    private String rawUrl;
    private Long size;
    private Boolean truncated;
    private String content;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GistFile gistFile = (GistFile) o;
        return Objects.equals(filename, gistFile.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename);
    }
}
