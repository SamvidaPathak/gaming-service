package com.project.gamingservice.util;

import java.util.HashMap;
import java.util.Map;

public class ScoreRowFormatter {
    private Map<String,String> formData = new HashMap<>();

    public ScoreRowFormatter(String body) {

        String[] data = body.split("&");

        for (String datum : data) {

            String[] keyValue = datum.split("=");

            formData.put( keyValue[0] , keyValue[1] );

        }

    }

    public String get(String formElement) {
        return formData.get(formElement);
    }
}
