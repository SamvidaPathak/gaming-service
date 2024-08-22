package com.project.gamingservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreRowFormatterTest {

    @Test
    public void testScoreRowFormatter()
    {
        String body = "name=Samvida&score=100";
        ScoreRowFormatter scoreRowFormatter=new ScoreRowFormatter(body);

        assertEquals(scoreRowFormatter.get("name"),"Samvida");
    }



}