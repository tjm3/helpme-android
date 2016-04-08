package uk.ac.hud.tjm3.helpme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by tmkn8 on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private int id;
    private String username;
    private String email;
    private String picture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
