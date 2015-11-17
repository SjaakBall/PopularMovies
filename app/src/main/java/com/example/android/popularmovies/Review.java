package com.example.android.popularmovies;

/**
 * Created by JanHerman on 13/11/2015.
 */
public class Review {
    private String movieId;
    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String movieId, String id, String author, String content, String url) {
        this.movieId = movieId;
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
