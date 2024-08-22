package com.project.gamingservice.controller;

import com.project.gamingservice.config.KafkaConfigProperties;
import com.project.gamingservice.contants.Constants;
import com.project.gamingservice.model.Score;
import com.project.gamingservice.service.Top5CalculationService;
import com.project.gamingservice.service.KStreamService;
import com.project.gamingservice.service.Scores;
import com.project.gamingservice.util.DisplayUtils;
import com.project.gamingservice.util.ScoreRowFormatter;
import com.project.gamingservice.util.ResourceUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Controller
public class HighScoreController {

    private final Map<String, String> cache = new HashMap<>();
    private final KafkaConfigProperties kafkaConfigProperties;
    private final KStreamService scoreStreams;

    @Autowired
    public HighScoreController(KStreamService scoreStreams, KafkaConfigProperties kafkaConfigProperties) {
        this.scoreStreams = scoreStreams;
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.scoreStreams.start();
    }

    @GetMapping("/")
    public String getHomePage(Model model) throws IOException {
        String page = cache.get("/");
        if (page != null) {
            model.addAttribute("page", page);
            return "home";
        }

        page = new String(ResourceUtil.getResource("templates/home.html"));
        cache.put("/", page);
        model.addAttribute("page", page);
        return "home";
    }

    @PostMapping("/newScore")
    public String postNewScore(@RequestBody String body) {
        System.out.println("Inside postNewScore");
        ScoreRowFormatter parser = new ScoreRowFormatter(body);
        String user = parser.get("user");
        int score = Integer.parseInt(parser.get("score"));

        Producer<String, Integer> kafkaProducer = kafkaConfigProperties.createKafkaProducer();

        kafkaProducer.send(new ProducerRecord<String, Integer>(Constants.INPUT_TOPIC, user, score), (rm, exc) -> {
            if (exc != null) {
                System.out.println("Error sending message to Kafka: " + exc.getMessage());
            }
        });

        return "redirect:/";
    }

    @GetMapping("/events")
    public SseEmitter getEvents() {
        System.out.println("Inside getEvents");
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                Scores scores = new Scores();
                Iterator<Score> iterator = scores.iterator();
                while (iterator.hasNext()) {
                    Score score = iterator.next();

                    try {
                        emitter.send(SseEmitter.event().data(score.formatScoreRow()));
                    } catch (IOException e) {
                        // Handle IO exceptions (e.g., client disconnected)
                        emitter.completeWithError(e);
                        return;
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    @GetMapping("/highScores")
    public SseEmitter getHighScores() {
        System.out.println("Inside getHighScores");
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                Top5CalculationService highScores = new Top5CalculationService();
                Iterator<List<Score>> iterator = highScores.iterator();
                while (iterator.hasNext()) {
                    List<Score> scores = iterator.next();

                    try {
                        emitter.send(SseEmitter.event().data(DisplayUtils.formatScoreRow(scores)));
                    } catch (IOException e) {
                        // Handle IO exceptions (e.g., client disconnected)
                        emitter.completeWithError(e);
                        return;
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    public void stop() {
        scoreStreams.stopStreaming();
    }
}
