package com.mithila.royalpaan.dto;

public class ChatRequest {

    private String sessionId;
    private String message;
    private String visitorName;

    public ChatRequest() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
}
