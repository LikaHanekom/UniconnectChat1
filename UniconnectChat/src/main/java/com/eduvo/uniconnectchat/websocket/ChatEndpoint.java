/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eduvo.uniconnectchat.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

/*
 * @author alika
 * DV.2022.F6T2N2
 */

@Slf4j
@ServerEndpoint("/chat-app/chat/{conversationId}")
public class ChatEndpoint {
    
    private static final Map<String, Set<Session>> conversation = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("conversationId") String conversationId) {
        // Store conversationId
        session.getUserProperties().put("conversationId", conversationId);

        conversation.computeIfAbsent(conversationId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("WS Open: " + session.getId() + " joined conversation " + conversationId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("Message Received: " + message);
        
        
        
        // Get conversationID
        String conversationId = (String) session.getUserProperties().get("conversationId");
        if (conversationId == null) {
            conversationId = "default";
        }

        // Get all sessions that are in that particular conversation
        Set<Session> clients = conversation.getOrDefault(conversationId, Collections.emptySet());

        // Broadcast the message to all sessions in currend conversation
        for (Session client : clients) {
            if (client.isOpen() && !client.equals(session)) { 
                try {
                    client.getBasicRemote().sendText(message);
                    log.info("Sent to client: " + client.getId());
                } catch (IOException e) {
                    log.info("Failed to send " + client.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        String conversationId = (String) session.getUserProperties().get("conversationId");
        if (conversationId != null) {
            Set<Session> sessions = conversation.get(conversationId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    conversation.remove(conversationId);
                }
            }
        }
        log.info("Close WS: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("Session error for WS " + session.getId());
        throwable.printStackTrace();
    }
}
