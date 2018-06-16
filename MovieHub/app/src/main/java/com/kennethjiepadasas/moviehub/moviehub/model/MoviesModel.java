package com.kennethjiepadasas.moviehub.moviehub.model;

public class MoviesModel {
    private int id;
    private String title;
    private double vote_average;
    private String poster_path;
    private String overview;
    private String release_date;
    private long popularity;
    private double voteCount;

    public double getVoteCount(){
        return voteCount;
    }
    public String getTitle(){
        return title;
    }
    public String getPoster_path(){
        return poster_path;
    }
    public int getid(){
        return id;
    }
    public double getVote_average(){
        return vote_average;
    }
    public String getOverview(){
        return overview;
    }
    public String getRelease_date(){return release_date;}
    public long getPopularity(){
        return popularity;
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

    public void setVote_average(double vote_average){
        this.vote_average = vote_average;
    }

    public void setPoster_path(String poster_path){
        this.poster_path = poster_path;
    }
    public void setRelease_date(String release_date){this.release_date = release_date;}
    public void setPopularity(long popularity){
        this.popularity = popularity;
    }
    public void setVoteCount(double voteCount){
        this.voteCount = voteCount;
    }


}
