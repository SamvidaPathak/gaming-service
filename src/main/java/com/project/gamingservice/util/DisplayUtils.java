package com.project.gamingservice.util;

import com.project.gamingservice.model.Score;

import java.util.List;

public class DisplayUtils {

    public static String formatScoreRow(List<Score> scores) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (Score score : scores) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(score.formatScoreRow());
            i++;
        }
        sb.append("]");
        return sb.toString();
    }
}
