package com.techelevator.model.ai;

import java.util.ArrayList;
import java.util.List;

public class ChatRequestDto {
    private String model;
    private double temperature;
    private List<Message> messages = new ArrayList<>();

    public ChatRequestDto(String model, double temperature) {
        this.model = model;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
