package uk.ac.hud.tjm3.helpme.exceptions;

/**
 * Created by tmkn on 04/04/2016.
 */
public class InvalidLoginCredentialsRuntimeException extends RuntimeException {
    public String toString() {
        return "Provided credentials are not valid";
    }
}
