/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.eduvo.uniconnectchat.servlets;

import com.eduvo.uniconnectchat.db.UserDB;
import java.io.IOException;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/*
 * @author alika
 * DV.2022.F6T2N2
 */

@WebServlet(name = "RegistrationServlet", urlPatterns = {"/register"})
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDB.testConnection();

        String userName = request.getParameter("username");
        String userPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");

        // Validate
        if (userName == null || userPassword == null || confirmPassword == null ||
            userName.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/register.html?error=Missing+fields");
            return;
        }

        //password validation
        if (!userPassword.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/register.html?error=Passwords+do+not+match");
            return;
        }

        // Hash 
        String hashedPassword = hashPassword(userPassword);

        try (Connection connection = UserDB.getConnection()) {
            if (connection == null || connection.isClosed()) {
                response.sendRedirect(request.getContextPath() + "/register.html?error=Database+connection+failed");
                return;
            }

            // Check username existence
            String checkSql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
            try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
                checkPs.setString(1, userName);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    response.sendRedirect(request.getContextPath() + "/register.html?error=Username+already+exists");
                    return;
                }
            }

            // Insert 
            String sql = "INSERT INTO users (user_name, user_password) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userName);
                ps.setString(2, hashedPassword);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    response.sendRedirect(request.getContextPath() + "/login.html?success=Account+created");
                } else {
                    response.sendRedirect(request.getContextPath() + "/register.html?error=Could+not+register");
                }
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate") || e.getErrorCode() == 1062) {
                response.sendRedirect(request.getContextPath() + "/register.html?error=Username+already+exists");
            } else {
                response.sendRedirect(request.getContextPath() + "/register.html?error=Could+not+register");
            }
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
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}

