package com.mithila.royalpaan.dto;

public class ChatResponse {

    private String sessionId;
    private String reply;
    private boolean leadCaptured;

    public ChatResponse() {}

    public ChatResponse(String sessionId, String reply, boolean leadCaptured) {
        this.sessionId = sessionId;
        this.reply = reply;
        this.leadCaptured = leadCaptured;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public boolean isLeadCaptured() { return leadCaptured; }
    public void setLeadCaptured(boolean leadCaptured) { this.leadCaptured = leadCaptured; }
}
