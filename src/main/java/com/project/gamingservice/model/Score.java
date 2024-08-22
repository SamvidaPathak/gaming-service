package com.project.gamingservice.model;

import lombok.Data;

@Data
public class Score implements Comparable<Score> {
    private String name;
    private int score;
    private long date;
    public Score(String name, int score, long date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    @Override
    public int compareTo(Score o) {
        return this.score-o.score ;
    }

    public String formatScoreRow(){
        return String.format("{ \"time\" : \"%s\" , \"user\" : \"%s\" , \"score\" : %d }" , this.getDate() , this.getName() , this.getScore());
    }
}
