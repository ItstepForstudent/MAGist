package ua.com.arturmamedov.magist.entities;

import com.google.gson.annotations.SerializedName;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@Getter @Setter
public class Gist {

    private String url;
    @SerializedName("forks_url")
    private String forksUrl;
    @SerializedName("commits_url")
    private String commitsUrl;
    private String id;
    @SerializedName("node_id")
    private String nodeId;
    @SerializedName("git_pull_url")
    private String gitPullUrl;
    @SerializedName("git_push_url")
    private String gitPushUrl;
    @SerializedName("html_url")
    private String htmlUrl;
    private Map<String, GistFile> files;
    private Boolean isPublic;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    private String description;
    private Integer comments;
    private String user;
    @SerializedName("commentsUrl")
    private String comments_url;
    private Boolean truncated;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gist gist = (Gist) o;
        return Objects.equals(id, gist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
