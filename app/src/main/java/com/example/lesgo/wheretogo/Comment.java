package com.example.lesgo.wheretogo;

/**
 * Created by lesgo on 11/21/2016.
 */
public class Comment {
    String comment, username, rating;

    public Comment(String comment, String username, String rating) {
        this.comment = comment;
        this.username = username;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
