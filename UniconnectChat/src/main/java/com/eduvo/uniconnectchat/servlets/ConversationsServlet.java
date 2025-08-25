/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.eduvo.uniconnectchat.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/*
 * @author alika
 * DV.2022.F6T2N2
 */

@WebServlet(name = "ConversationsServlet", urlPatterns = {"/ConversationsServlet"})
public class ConversationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_name") == null) {
            response.sendRedirect("login.html?error=unauthorized");
            return;
        }

        String username = (String) session.getAttribute("user_name");

        // Mock conversation
        List<Map<String, String>> conversations = new ArrayList<>();

        Map<String, String> conv1 = new HashMap<>();
        conv1.put("id", "1");
        conv1.put("name", "Chat Room One");
        conversations.add(conv1);

        Map<String, String> conv2 = new HashMap<>();
        conv2.put("id", "2");
        conv2.put("name", "Chat Room Two");
        conversations.add(conv2);

        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < conversations.size(); i++) {
            Map<String, String> conv = conversations.get(i);
            json.append("{")
                .append("\"id\":\"").append(conv.get("id")).append("\",")
                .append("\"name\":\"").append(conv.get("name")).append("\",")
                .append("\"preview\":\"").append(conv.get("preview")).append("\",")
                .append("\"time\":\"").append(conv.get("time")).append("\"")
                .append("}");
            if (i < conversations.size() - 1) json.append(",");
        }
        json.append("]");

        String encodedJson = URLEncoder.encode(json.toString(), "UTF-8");

       
        response.sendRedirect("conversations.html?username=" + username + "&conversations=" + encodedJson);
    }
}
