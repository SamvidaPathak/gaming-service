package com.project.gamingservice.util;

import com.project.gamingservice.model.Score;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class DisplayUtilsTest {

    @Mock
    DisplayUtils displayUtils;

    @Test
    public void testformatScoreRow()
    {
        Score score = new Score("name", 100, 1L);
        Score score1 = new Score("name1", 101, 1L);
        List<Score> scores=new ArrayList<>();
        scores.add(score);
        scores.add(score1);
        assertEquals("[{ \"time\" : \"1\" , \"user\" : \"name\" , \"score\" : 100 },{ \"time\" : \"1\" , \"user\" : \"name1\" , \"score\" : 101 }]", displayUtils.formatScoreRow(scores));
    }
  
}