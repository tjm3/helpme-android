package uk.ac.hud.tjm3.helpme;

import java.util.List;

/**
 * Created by tmkn8 on 26/02/16.
 */
public class HelpRequest {

    private int id;
    private String title;
    private User author;
    private String datetime;
    private String locationName;
    private float locationLatitude;
    private float getLocationLongitude;
    private String content;
    private boolean isClosed;
    private List<HelpRequestReply> helpRequestReplies;


    public List<HelpRequestReply> getHelpRequestReplies() {
        return helpRequestReplies;
    }

    public void setHelpRequestReplies(List<HelpRequestReply> helpRequestReplies) {
        this.helpRequestReplies = helpRequestReplies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(float locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public float getGetLocationLongitude() {
        return getLocationLongitude;
    }

    public void setGetLocationLongitude(float getLocationLongitude) {
        this.getLocationLongitude = getLocationLongitude;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }
}
