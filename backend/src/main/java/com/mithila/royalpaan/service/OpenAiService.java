package com.mithila.royalpaan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.0-flash}")
    private String model;

    @Value("${gemini.api.max-tokens:1024}")
    private int maxTokens;

    @Value("${gemini.api.temperature:0.85}")
    private double temperature;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are "Paan Sahayak" (पान सहायक), the official AI assistant for Mithila Royal Paan — a premium betel leaf farming and catering company based in Madhubani, Bihar, India, established in 1974.

            CRITICAL BEHAVIORAL INSTRUCTIONS:
            - You must behave like an intelligent, dynamic AI assistant (like ChatGPT), NOT a fixed rule-based or keyword-based FAQ bot.
            - ALWAYS generate unique, dynamic, and context-aware responses. Never repeat the same response structure or phrasing twice.
            - You have full access to conversation history. Remember previous messages in the same conversation and use them as context.
            - Remember customer information already provided in the chat (name, phone, location) and do not ask for it again.
            - Support Hindi, English, and Hinglish naturally, adapting to the user's language.
            - Avoid repetitive greetings (don't say "Namaste" in every message).
            - Avoid robotic, repeated promotional messages unless directly relevant to the user's current intent.
            - If the customer asks general conversational questions, answer normally and intelligently.
            - Understand the user's underlying intent before answering. Ask natural follow-up questions when needed to clarify.
            - Use different wording, sentence structures, and tone variation in each response.

            === ABOUT THE COMPANY ===
            Name: Mithila Royal Paan
            Founded: 1974
            Location: Madhubani Farms, Darbhanga, Bihar, India
            Contact: +91 70614 05543 | support@mithilaroyalpaan.com
            WhatsApp: +91 70614 05543

            === PRODUCTS ===
            1. Fresh Mithila Betel Leaves — Premium organic betel leaves from Bihar farms. Price: ₹50–₹500/bundle depending on quantity.
            2. Sweet Paan — Traditional meetha paan with gulkand, coconut, fennel. Price: ₹20–₹50/piece.
            3. Chocolate Paan — Indulgent paan dipped in dark/milk chocolate. Price: ₹40–₹80/piece.
            4. Fire Paan — Spectacular camphor-lit paan experience. Price: ₹60–₹100/piece.
            5. Dry Fruit Paan — Loaded with almonds, cashews, raisins, saffron. Price: ₹50–₹120/piece.
            6. Premium Gift Boxes — Curated paan gift sets for weddings, festivals. Price: ₹299–₹2499/box.
            7. Bulk Orders — Minimum 500 pieces for bulk/event orders. Special pricing available.

            === SERVICES ===
            1. Wedding Paan Counter — Live paan counter setup at wedding receptions. Includes 2 trained paan masters, decorated counter with Mithila motifs, 5–10 varieties of paan. Starts at ₹15,000 per event. Minimum 200 guests.
            2. Event Paan Counter — For corporate events, kitty parties, cultural events. Starts at ₹8,000. Minimum 50 guests.
            3. Reception Services — Full-service paan counter for reception ceremonies. Starts at ₹12,000.
            4. Corporate Event Services — Professional branded paan counters for corporate gatherings. Starts at ₹10,000.
            5. Bulk Export — Export of premium Magahi betel leaves globally. Certified organic. Temperature-controlled packaging. Contact for pricing.

            === BOOKING INFORMATION ===
            - Advance booking required: Minimum 7 days for events, 30 days for weddings.
            - Cities served: Bihar, Jharkhand, UP, Delhi, Mumbai, Kolkata (and more on request).
            - Payment: 50% advance, 50% on day of event.
            - Customization: Custom Mithila painting themes, personalized paan boxes available.

            === DELIVERY INFORMATION ===
            - Fresh paan delivery available in Bihar and nearby states.
            - Gift boxes shipped pan-India via cold-chain courier.
            - Delivery time: 2–5 business days.
            - Minimum order for delivery: ₹500.

            === LEAD COLLECTION INSTRUCTIONS ===
            When a user shows buying intent (booking, ordering, pricing for an event), intelligently and naturally collect their details.
            Collect: Full Name → Mobile Number → WhatsApp Number → Email → City → Requirement.
            Do NOT ask all at once — collect them conversationally over multiple messages. If they already gave their name, use it!
            When you have collected at least Name + Mobile, include this JSON block at the VERY END of your response (after your text), on a new line:
            LEAD_DATA:{"name":"<name>","mobile":"<mobile>","whatsapp":"<whatsapp or same as mobile>","email":"<email or empty>","city":"<city or empty>","requirement":"<brief requirement>"}

            === WHAT YOU MUST NOT DO ===
            - Do NOT make up prices or services that are not listed above.
            - Do NOT discuss competitors.
            - Do NOT promise guaranteed delivery dates.
            - Do NOT handle payments or collect card details.
            - Stay focused on Mithila Royal Paan and general helpful conversation.
            """;

    /**
     * Send conversation history + new user message to Google Gemini and get AI
     * reply.
     *
     * @param conversationHistory List of {role, content} maps representing prior
     *                            messages
     * @param userMessage         The latest user message
     * @return The AI-generated reply text
     */
    public String chat(List<Map<String, String>> conversationHistory, String userMessage) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            System.err.println("GEMINI ERROR: API Key is missing or not configured in application.properties!");
            return "⚠️ **System Error:** The Gemini API key is missing. Please open `src/main/resources/application.properties` and set your `gemini.api.key`.";
        }

        // Build request body once, retry up to 3 times on 503/429 errors
        String requestJson = buildGeminiRequest(conversationHistory, userMessage);
        String url = String.format(GEMINI_URL, model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", apiKey);

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.POST, entity, String.class);

                JsonNode responseJson = objectMapper.readTree(response.getBody());

                // Extract text from Gemini response
                JsonNode candidates = responseJson.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode parts = candidates.get(0).path("content").path("parts");
                    if (parts.isArray() && parts.size() > 0) {
                        return parts.get(0).path("text").asText();
                    }
                }

                return "I'm sorry, I couldn't generate a response. Please try again.";

            } catch (Exception e) {
                String errorMsg = e.getMessage();
                boolean isRetryable = errorMsg != null &&
                        (errorMsg.contains("503") || errorMsg.contains("429") || errorMsg.contains("UNAVAILABLE"));

                if (isRetryable && attempt < maxRetries) {
                    System.out.println("GEMINI: Attempt " + attempt + " failed (retryable). Retrying in "
                            + (attempt * 2) + " seconds...");
                    try {
                        Thread.sleep(attempt * 2000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                System.err.println("GEMINI API CALL FAILED (attempt " + attempt + "): " + errorMsg);
                if (isRetryable) {
                    return "🙏 Server thoda busy hai abhi. Please 10 second wait karke dobara try karein!";
                }
                return "⚠️ **AI Connection Error:** " + errorMsg;
            }
        }

        return "🙏 Server busy hai. Please thodi der mein try karein ya WhatsApp karein: **+91 70614 05543**";
    }

    /**
     * Build the Gemini API request JSON.
     */
    private String buildGeminiRequest(List<Map<String, String>> conversationHistory, String userMessage) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            // --- System Instruction (with real-time date/time injected) ---
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            String currentDateTime = now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm a"));
            String dynamicPrompt = SYSTEM_PROMPT + "\n\n=== CURRENT DATE & TIME ===\nToday is: " + currentDateTime
                    + " (IST - Indian Standard Time).\nAlways use this as the current date and time when the user asks.";

            ObjectNode systemInstruction = objectMapper.createObjectNode();
            ObjectNode systemPart = objectMapper.createObjectNode();
            systemPart.put("text", dynamicPrompt);
            ArrayNode systemParts = objectMapper.createArrayNode();
            systemParts.add(systemPart);
            systemInstruction.set("parts", systemParts);
            requestBody.set("systemInstruction", systemInstruction);

            // --- Conversation Contents ---
            ArrayNode contents = objectMapper.createArrayNode();

            // Add conversation history (last 20 messages for context)
            int start = Math.max(0, conversationHistory.size() - 20);
            for (int i = start; i < conversationHistory.size(); i++) {
                Map<String, String> msg = conversationHistory.get(i);
                ObjectNode contentNode = objectMapper.createObjectNode();
                String role = msg.get("role").equalsIgnoreCase("ASSISTANT") ? "model" : "user";
                contentNode.put("role", role);

                ArrayNode parts = objectMapper.createArrayNode();
                ObjectNode textPart = objectMapper.createObjectNode();
                textPart.put("text", msg.get("content"));
                parts.add(textPart);
                contentNode.set("parts", parts);

                contents.add(contentNode);
            }

            // Add new user message
            ObjectNode userContent = objectMapper.createObjectNode();
            userContent.put("role", "user");
            ArrayNode userParts = objectMapper.createArrayNode();
            ObjectNode userTextPart = objectMapper.createObjectNode();
            userTextPart.put("text", userMessage);
            userParts.add(userTextPart);
            userContent.set("parts", userParts);
            contents.add(userContent);

            requestBody.set("contents", contents);

            // --- Generation Config ---
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            generationConfig.put("topP", 0.95);
            generationConfig.put("topK", 40);
            requestBody.set("generationConfig", generationConfig);

            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Gemini request", e);
        }
    }

    /**
     * Extract lead JSON data from AI response if present.
     */
    public JsonNode extractLeadData(String aiReply) {
        try {
            int idx = aiReply.indexOf("LEAD_DATA:");
            if (idx == -1)
                return null;
            String jsonStr = aiReply.substring(idx + "LEAD_DATA:".length()).trim();
            int start = jsonStr.indexOf('{');
            int end = jsonStr.lastIndexOf('}');
            if (start == -1 || end == -1)
                return null;
            jsonStr = jsonStr.substring(start, end + 1);
            return objectMapper.readTree(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clean the AI reply by removing the LEAD_DATA JSON block before showing to
     * user.
     */
    public String cleanReply(String aiReply) {
        int idx = aiReply.indexOf("LEAD_DATA:");
        if (idx == -1)
            return aiReply.trim();
        return aiReply.substring(0, idx).trim();
    }
}
