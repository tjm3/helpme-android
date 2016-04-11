package uk.ac.hud.tjm3.helpme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by tmkn8 on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelpRequestReply {
    @JsonProperty("id")
    private int id;
    @JsonProperty("content")
    private String content;
    @JsonProperty("datetime")
    private Date datetime;
    @JsonProperty("author_name")
    private String authorName;
    @JsonProperty("author")
    private int authorId;
    @JsonProperty("help_request")
    private int helpRequestId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getAuthorName() {
        return authorName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getHelpRequestId() {
        return helpRequestId;
    }

    public void setHelpRequestId(int helpRequestId) {
        this.helpRequestId = helpRequestId;
    }
}
