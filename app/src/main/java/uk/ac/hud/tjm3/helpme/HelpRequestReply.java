package uk.ac.hud.tjm3.helpme;

/**
 * Created by tmkn8 on 26/02/16.
 */
public class HelpRequestReply {
    private HelpRequest helpRequest;

    public HelpRequest getHelpRequest() {
        return helpRequest;
    }

    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest = helpRequest;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    private String content;
    private String datetime;
    private User author;
}
