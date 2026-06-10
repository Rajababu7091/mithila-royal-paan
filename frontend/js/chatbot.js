/* ============================================================
   MITHILA ROYAL PAAN — AI Chatbot Widget JavaScript
   Powered by OpenAI GPT | Full conversation, lead capture,
   dark/light mode, quick actions, mobile responsive
   ============================================================ */

(function () {
    'use strict';

    // ===================== CONFIG =====================
    const CHAT_API = '/api/v1/chat/message';
    const SESSION_KEY = 'mrp_chat_session';
    const OPENED_KEY = 'mrp_chat_opened';

    // ===================== STATE =====================
    let sessionId = localStorage.getItem(SESSION_KEY) || generateSessionId();
    let isOpen = false;
    let isTyping = false;
    let chatInitialized = false;

    localStorage.setItem(SESSION_KEY, sessionId);

    function generateSessionId() {
        return 'mrp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    // ===================== HTML INJECTION =====================
    function injectChatbotHTML() {
        const html = `
        <!-- AI Chatbot Toggle Button -->
        <button id="chatbot-toggle-btn" aria-label="Open AI Chat Assistant" title="Chat with Paan Sahayak AI">
            <span id="chatbot-notif-dot"></span>
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
            </svg>
        </button>

        <!-- Chat Window -->
        <div id="chatbot-window" role="dialog" aria-label="Mithila Royal Paan AI Chat">

            <!-- Header -->
            <div id="chatbot-header">
                <div class="chatbot-avatar">🌿</div>
                <div class="chatbot-header-info">
                    <h6>Paan Sahayak AI</h6>
                    <small><span class="chatbot-online-dot"></span> Online · Powered by GPT</small>
                </div>
                <div class="chatbot-header-actions">
                    <button class="chatbot-header-btn" id="chatbot-clear-btn" title="New Conversation">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 14l-5-5 1.41-1.41L12 14.17l7.59-7.59L21 8l-9 9z"/>
                        </svg>
                    </button>
                    <button class="chatbot-header-btn" id="chatbot-close-btn" title="Close">
                        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                        </svg>
                    </button>
                </div>
            </div>

            <!-- Messages -->
            <div id="chatbot-messages"></div>

            <!-- Quick Actions -->
            <div id="chatbot-quick-actions">
                <button class="quick-action-btn" data-msg="Wedding Booking">💍 Wedding</button>
                <button class="quick-action-btn" data-msg="Event Booking">🎉 Event</button>
                <button class="quick-action-btn" data-msg="Bulk Orders">📦 Bulk Order</button>
                <button class="quick-action-btn" data-msg="Export Enquiry">✈️ Export</button>
                <button class="quick-action-btn" data-msg="What are your products and prices?">🌿 Products</button>
                <button class="quick-action-btn" data-msg="Contact Us">📞 Contact</button>
            </div>

            <!-- Input -->
            <div id="chatbot-input-area">
                <textarea id="chatbot-input" rows="1" placeholder="Type in Hindi, English, or Hinglish..." maxlength="500"></textarea>
                <button id="chatbot-send-btn" aria-label="Send message">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                    </svg>
                </button>
            </div>
        </div>
        `;

        const container = document.createElement('div');
        container.innerHTML = html;
        document.body.appendChild(container);
    }

    // ===================== INITIALIZATION =====================
    function initChatbot() {
        if (chatInitialized) return;
        chatInitialized = true;

        injectChatbotHTML();

        const toggleBtn = document.getElementById('chatbot-toggle-btn');
        const closeBtn = document.getElementById('chatbot-close-btn');
        const clearBtn = document.getElementById('chatbot-clear-btn');
        const sendBtn = document.getElementById('chatbot-send-btn');
        const input = document.getElementById('chatbot-input');
        const quickBtns = document.querySelectorAll('.quick-action-btn');

        // Toggle open/close
        toggleBtn.addEventListener('click', toggleChat);
        closeBtn.addEventListener('click', closeChat);

        // Clear / new conversation
        clearBtn.addEventListener('click', () => {
            sessionId = generateSessionId();
            localStorage.setItem(SESSION_KEY, sessionId);
            document.getElementById('chatbot-messages').innerHTML = '';
            showWelcomeMessage();
        });

        // Send button
        sendBtn.addEventListener('click', sendMessage);

        // Enter key (Shift+Enter for newline)
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });

        // Auto-resize textarea
        input.addEventListener('input', () => {
            input.style.height = 'auto';
            input.style.height = Math.min(input.scrollHeight, 90) + 'px';
        });

        // Quick action buttons
        quickBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const msg = btn.getAttribute('data-msg');
                if (msg) {
                    openChat();
                    sendMessageText(msg);
                }
            });
        });

        // Show notif dot after 3 seconds to attract attention
        setTimeout(() => {
            const dot = document.getElementById('chatbot-notif-dot');
            if (dot && !isOpen) dot.style.display = 'block';
        }, 3000);

        // If previously opened in this session, show welcome
        if (localStorage.getItem(OPENED_KEY) === 'true') {
            openChat();
        }
    }

    // ===================== OPEN / CLOSE =====================
    function openChat() {
        isOpen = true;
        const win = document.getElementById('chatbot-window');
        const btn = document.getElementById('chatbot-toggle-btn');
        const dot = document.getElementById('chatbot-notif-dot');

        if (win) win.classList.add('show');
        if (btn) btn.classList.add('open');
        if (dot) dot.style.display = 'none';

        localStorage.setItem(OPENED_KEY, 'true');

        const msgs = document.getElementById('chatbot-messages');
        if (msgs && msgs.children.length === 0) {
            showWelcomeMessage();
        }

        setTimeout(() => {
            const input = document.getElementById('chatbot-input');
            if (input) input.focus();
        }, 350);
    }

    function closeChat() {
        isOpen = false;
        const win = document.getElementById('chatbot-window');
        const btn = document.getElementById('chatbot-toggle-btn');
        if (win) win.classList.remove('show');
        if (btn) btn.classList.remove('open');
    }

    function toggleChat() {
        if (isOpen) closeChat();
        else openChat();
    }

    // ===================== WELCOME MESSAGE =====================
    function showWelcomeMessage() {
        const welcomeText = `🙏 **Namaste!** Mein **Paan Sahayak** hoon — Mithila Royal Paan ka AI assistant!

Mein aapki help kar sakta hoon:
• 💍 **Wedding & Event** paan counter booking
• 🌿 **Products** — Sweet, Fire, Chocolate, Dry Fruit Paan
• 📦 **Bulk Orders** & Gift Boxes
• ✈️ **Export** of premium Magahi betel leaves
• 📞 **Contact** and support

Aap Hindi, English, ya Hinglish mein baat kar sakte hain! Kaise help karoon? 😊`;

        appendMessage('bot', welcomeText);
    }

    // ===================== SEND MESSAGE =====================
    function sendMessage() {
        const input = document.getElementById('chatbot-input');
        if (!input) return;
        const text = input.value.trim();
        if (!text || isTyping) return;

        input.value = '';
        input.style.height = 'auto';
        sendMessageText(text);
    }

    function sendMessageText(text) {
        if (isTyping) return;

        // Show user message
        appendMessage('user', text);

        // Quick actions remain visible at the bottom
        // const qa = document.getElementById('chatbot-quick-actions');
        // if (qa) qa.style.display = 'none';

        // Show typing indicator
        showTyping();

        // Call API
        fetch(CHAT_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                sessionId: sessionId,
                message: text
            })
        })
        .then(res => {
            if (!res.ok) throw new Error('Server error: ' + res.status);
            return res.json();
        })
        .then(data => {
            hideTyping();
            if (data.reply) {
                appendMessage('bot', data.reply);
                if (data.leadCaptured) {
                    appendLeadBadge();
                }
            }
        })
        .catch(err => {
            hideTyping();
            appendMessage('bot', '⚠️ Kuch problem ho gayi. Please thodi der mein try karein ya WhatsApp karein: **+91 70614 05543**');
            console.error('Chatbot error:', err);
        });
    }

    // ===================== DOM HELPERS =====================
    function appendMessage(role, text) {
        const messages = document.getElementById('chatbot-messages');
        if (!messages) return;

        const isUser = role === 'user';
        const time = new Date().toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });

        const msgEl = document.createElement('div');
        msgEl.className = `chat-msg ${isUser ? 'user' : 'bot'}`;

        const avatarEl = document.createElement('div');
        avatarEl.className = 'chat-msg-avatar';
        avatarEl.textContent = isUser ? '👤' : '🌿';

        const bubbleWrap = document.createElement('div');

        const bubble = document.createElement('div');
        bubble.className = 'chat-bubble';
        bubble.innerHTML = formatMarkdown(text);

        const timeEl = document.createElement('div');
        timeEl.className = 'chat-time';
        timeEl.textContent = time;

        bubbleWrap.appendChild(bubble);
        bubbleWrap.appendChild(timeEl);

        msgEl.appendChild(avatarEl);
        msgEl.appendChild(bubbleWrap);

        messages.appendChild(msgEl);
        scrollToBottom();
    }

    function appendLeadBadge() {
        const messages = document.getElementById('chatbot-messages');
        if (!messages) return;
        const badge = document.createElement('div');
        badge.className = 'lead-badge';
        badge.innerHTML = '✅ Your details have been saved. Our team will contact you soon!';
        messages.appendChild(badge);
        scrollToBottom();
    }

    function showTyping() {
        isTyping = true;
        const messages = document.getElementById('chatbot-messages');
        if (!messages) return;

        const typingEl = document.createElement('div');
        typingEl.id = 'chatbot-typing';
        typingEl.className = 'chat-msg bot typing-indicator';
        typingEl.innerHTML = `
            <div class="chat-msg-avatar">🌿</div>
            <div class="typing-dots">
                <span></span><span></span><span></span>
            </div>
        `;
        messages.appendChild(typingEl);
        scrollToBottom();

        const sendBtn = document.getElementById('chatbot-send-btn');
        if (sendBtn) sendBtn.disabled = true;
    }

    function hideTyping() {
        isTyping = false;
        const typingEl = document.getElementById('chatbot-typing');
        if (typingEl) typingEl.remove();

        const sendBtn = document.getElementById('chatbot-send-btn');
        if (sendBtn) sendBtn.disabled = false;
    }

    function scrollToBottom() {
        const messages = document.getElementById('chatbot-messages');
        if (messages) {
            setTimeout(() => {
                messages.scrollTop = messages.scrollHeight;
            }, 50);
        }
    }

    /**
     * Convert basic markdown-like syntax to HTML for display.
     * Supports: **bold**, *italic*, bullet lists, line breaks.
     */
    function formatMarkdown(text) {
        if (!text) return '';

        // Escape HTML first
        let html = text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');

        // Bold **text**
        html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

        // Italic *text*
        html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');

        // Bullet points starting with •
        html = html.replace(/^[•\-]\s+(.+)$/gm, '<li>$1</li>');
        html = html.replace(/((<li>.*<\/li>\n?)+)/g, '<ul style="margin:6px 0 6px 16px;padding:0;">$1</ul>');

        // Line breaks
        html = html.replace(/\n/g, '<br>');

        return html;
    }

    // ===================== BOOT =====================
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initChatbot);
    } else {
        initChatbot();
    }

})();
