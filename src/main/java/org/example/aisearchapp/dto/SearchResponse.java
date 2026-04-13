package org.example.aisearchapp.dto;

import java.util.List;

public class SearchResponse {

    private String answer;
    private List<String> keyPoints;
    private List<String> sources;
    private List<String> followUpQuestions;

    public SearchResponse() {
    }

    public SearchResponse(String answer, List<String> keyPoints, List<String> sources, List<String> followUpQuestions) {
        this.answer = answer;
        this.keyPoints = keyPoints;
        this.sources = sources;
        this.followUpQuestions = followUpQuestions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(List<String> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(List<String> followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }
}