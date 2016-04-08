package uk.ac.hud.tjm3.helpme;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by tmkn8 on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelpRequest {
    @JsonProperty("id") private int id;
    @JsonIgnore private User author;
    @JsonProperty("title") private String title;
    @JsonProperty("author_name") private String authorName;
    @JsonProperty("datetime") private Date datetime;
    @JsonProperty("meeting_datetime") private Date meetingDatetime;
    @JsonProperty("location_name") private String locationName;
    @JsonProperty("location_latitude") private Double locationLatitude;
    @JsonProperty("location_longitude") private Double getLocationLongitude;
    @JsonProperty("content") private String content;
    @JsonProperty("is_closed") private boolean isClosed;
    @JsonIgnore private List<HelpRequestReply> helpRequestReplies;


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

    public Date getDatetime() {
        return datetime;
    }

    public Date getMeetingDatetime() {
        return meetingDatetime;
    }

    public void setMeetingDatetime(Date meetingDatetime) {
        this.meetingDatetime = meetingDatetime;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public Double getGetLocationLongitude() {
        return getLocationLongitude;
    }

    public void setGetLocationLongitude(Double getLocationLongitude) {
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

    public String toString() {
        return this.title + " - " + this.authorName + " - " + this.meetingDatetime.toString() + " - " + this.locationName;
    }

    public String getAuthorName() {
        return authorName;
    }
}
