/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.eduvo.uniconnectchat.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * @author alika
 * DV.2022.F6T2N2
 */



@WebServlet(name = "ChatPageServlet", urlPatterns = {"/ChatPageServlet"})
public class ChatPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("user_name") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String conversationId = request.getParameter("conversation");
        if (conversationId == null) {
            response.sendRedirect("ConversationsServlet");
            return;
        }

        request.setAttribute("conversationId", conversationId);
        request.setAttribute("username", session.getAttribute("user_name"));
        request.getRequestDispatcher("/chat.jsp").forward(request, response);
    }
}

