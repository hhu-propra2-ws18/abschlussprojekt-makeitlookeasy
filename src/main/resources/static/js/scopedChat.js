'use strict';

function initializeChat(subscriptionUrl, withSession, addUserUrl, messageUrl) {
    var endpoint = '/ws';

    var messageForm = null;
    var messageInput = null;
    var messageReceiver = null;
    var messageArea = null;

    var stompClient = null;
    var sessionId = null;

    var messageMap = {
        JOIN: " joined!",
        LEAVE: " left!"
    };

    var preceeedingMessage = {};

    function hash(s){
        return s.split("").reduce(function(a,b){a=((a<<5)-a)+b.charCodeAt(0);return a&a},0);              
    }

    function appendScopedChat() {
        var chatElement = document.querySelector('#chat-page').cloneNode(true);
        console.log(chatElement);

        chatElement.id = hash(subscriptionUrl);
        chatElement.classList.remove('chat-hidden');
        document.body.appendChild(chatElement);

        messageForm = chatElement.querySelector('.messageForm');
        messageInput = chatElement.querySelector('.message');
        messageReceiver = chatElement.querySelector('.receiver');
        messageArea = chatElement.querySelector('.messageArea');
    }

    function connect () {
        var socket = new SockJS(endpoint);
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnect, onError);
    }

    function onConnect(frame) {
        retrieveSessionId(frame);
        subscribe();
    }

    function subscribe () {
        stompClient.subscribe(
            withSession ? subscriptionUrl+"-user"+sessionId : subscriptionUrl,
            onMessageReceived
        );
        addUserUrl && stompClient.send(addUserUrl, {}, JSON.stringify({type: 'JOIN'}));
    }

    function retrieveSessionId(frame) {
        console.log(stompClient.ws._transport.url)
        var url = stompClient.ws._transport.url
            .replace("ws://localhost:8080"+endpoint+"/",  "")
            .replace("/websocket", "")
            .replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);
        sessionId = url;
    }

    function onError(error) {
        messageArea.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
        messageArea.style.color = 'red';
    }


    function sendMessage() {
        var messageContent = messageInput.value.trim();
        if(messageContent && stompClient) {
            console.log("sending");
            var chatMessage = {
                content: messageContent,
                type: 'CHAT',
                receiver: messageReceiver.value,
                timestamp: Date.now(),
                sender: 'self'
            };
            stompClient.send(messageUrl, {}, JSON.stringify(chatMessage));
            onMessageReceived(chatMessage);
            messageInput.value = '';
        }
    }


    function onMessageReceived(payload) {
        var message = payload.body ? JSON.parse(payload.body) : payload;

        var messageElement = document.createElement('li');
        messageElement.classList.add('message');

        if(message.type === 'JOIN' || message.type === 'LEAVE') {
            messageElement.classList.add('event');
            message.content = message.sender+ messageMap[message.type];
        } else if(preceeedingMessage.sender !== message.sender || message.timestamp < Date.now() - 60000) {
            messageElement.appendChild(buildSenderTag(message));
            messageElement.classList.add('divided');
        }

        var textElement = document.createElement('p');
        textElement.appendChild(document.createTextNode(message.content));

        messageElement.appendChild(formatMessage(message));

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;

        preceeedingMessage = message;
    }

    function formatMessage(message) {
        var textElement = document.createElement('p');
        var destructedMessage = message.content.split('\n');
        destructedMessage.forEach(function (value, index) {
            textElement.appendChild(document.createTextNode(value));
            if(index < destructedMessage.length) {textElement.appendChild(document.createElement('br'))};
        });
        return textElement;
    }

    function buildSenderTag(message) {
        var senderTag = document.createElement('span');
        senderTag.classList.add("message-title");
        senderTag.appendChild(
            document.createTextNode(
                message.sender +
                ' ' +
                (new Date(parseInt(message.timestamp))).toLocaleTimeString("de-DE")
            )
        );
        return senderTag;
    }

    function registerEvents() {
        messageForm.addEventListener('submit', sendMessage, true);
        messageInput.addEventListener(
            'keypress',
            function (evt) { evt.code === "Enter" && !evt.shiftKey && sendMessage(); },
            true
        );
    }

    appendScopedChat();
    connect();
    registerEvents();
}

// initializeChat("/user/queue/specific-user", true, null, "/chat.privateMessage");