package com.jobportal.controller;

import com.jobportal.util.DBConnection;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ApplicationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("apply".equals(action)) {
            handleApplication(request, response);
        } else if ("search".equals(action)) {
            handleSearch(request, response);
        } else {
            request.setAttribute("error", "Invalid action.");
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }

    private void handleApplication(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String jobRole = trim(request.getParameter("jobRole"));
        String[] skillsArray = request.getParameterValues("skills");

        if (name.isEmpty() || email.isEmpty() || jobRole.isEmpty()) {
            redirectWithError(response, "apply.jsp", "All required fields must be filled.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectWithError(response, "apply.jsp", "Please enter a valid email address.");
            return;
        }

        if (skillsArray == null || skillsArray.length == 0) {
            redirectWithError(response, "apply.jsp", "Please select at least one skill.");
            return;
        }

        // Small calculation required by assignment: count selected skills.
        String skills = "";
        int skillsCount = 0;
        if (skillsArray != null && skillsArray.length > 0) {
            skillsCount = skillsArray.length;
            skills = Arrays.stream(skillsArray).collect(Collectors.joining(", "));
        }

        // Persist applicant name for the current browser session.
        HttpSession session = request.getSession();
        session.setAttribute("userName", name);

        // Keep job-role preference in a cookie for a week.
        Cookie roleCookie = new Cookie("preferredJobRole", jobRole);
        roleCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(roleCookie);

        try {
            boolean inserted = DBConnection.insertApplication(
                    name, email, jobRole, skills
            );

            if (inserted) {
                request.setAttribute("message", "Application submitted successfully.");
                request.setAttribute("skillsCount", skillsCount);
            } else {
                request.setAttribute("error", "Unable to save application. Please try again.");
            }
            request.getRequestDispatcher("result.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Database error: " + ex.getMessage());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String searchRole = trim(request.getParameter("searchRole"));
        if (searchRole.isEmpty()) {
            redirectWithError(response, "search.jsp", "Please select a job role to search.");
            return;
        }

        try {
            List<Map<String, String>> applications = DBConnection.searchApplications(searchRole);
            request.setAttribute("applications", applications);
            request.setAttribute("searchRole", searchRole);
            request.setAttribute("message", "Search completed.");
            request.getRequestDispatcher("result.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Database error: " + ex.getMessage());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }

    private void redirectWithError(HttpServletResponse response, String page, String error)
            throws IOException {
        String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8.name());
        response.sendRedirect(page + "?error=" + encodedError);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
