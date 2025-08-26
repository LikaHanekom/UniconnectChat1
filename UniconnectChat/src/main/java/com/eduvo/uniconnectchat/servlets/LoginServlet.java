/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.eduvo.uniconnectchat.servlets;

import com.eduvo.uniconnectchat.db.UserDB;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/*
 * @author alika
 * DV.2022.F6T2N2
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("Login attempt for: " + username);

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.html?error=empty");
            return;
        }
        
        try (Connection conn = UserDB.getConnection()) {
            String sql = "SELECT user_id, user_name, user_password FROM users WHERE user_name = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("user_password");
                        String inputHash = hashPassword(password);
                        
                        if (inputHash.equals(storedHash)) {
                            HttpSession session = request.getSession();
                            session.setAttribute("user_id", rs.getInt("user_id"));
                            session.setAttribute("user_name", rs.getString("user_name"));

                            
                            response.sendRedirect("home.html?username=" + URLEncoder.encode(username, "UTF-8"));
                            return;
                        }
                    }
                    response.sendRedirect("login.html?error=invalid");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error in login: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.html?error=server");
        } catch (Exception e) {
            System.out.println("Error in login: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.html?error=server");
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash password Failed", e);
        }
    }
}
