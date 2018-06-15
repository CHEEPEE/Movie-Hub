package com.kennethjiepadasas.moviehub.moviehub.model;

public class MoviesModel {
    private int id;
    private String title;
    private int vote_average;
    private String poster_path;
    private String overview;
    public String getTitle(){
        return title;
    }
    public String getPoster_path(){
        return poster_path;
    }
    public int getid(){
        return id;
    }
    public int getVote_average(){
        return vote_average;
    }
    public String getOverview(){
        return overview;
    }
    public void setOverview(String overview){
        this.overview = overview;
    }

    public void setId(int Id){
        this.id = Id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setVote_average(int vote_average){
        this.vote_average = vote_average;
    }

    public void setPoster_path(String poster_path){
        this.poster_path = poster_path;
    }
}
