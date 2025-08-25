package com.eduvo.uniconnectchat.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * @author alika
 * DV.2022.F6T2N2
 */

@WebServlet(name = "ChatServlet", urlPatterns = {"/ChatServlet"})
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //get 
        String conversationId = request.getParameter("conversationId");
        if (conversationId == null) conversationId = "defaultConversation";

        String username = request.getParameter("username");
        if (username == null) username = "Guest";

       
        response.sendRedirect("chat.html?conversationId=" + conversationId + "&username=" + username);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Chat servlet redirecting to chat.html";
    }
}
