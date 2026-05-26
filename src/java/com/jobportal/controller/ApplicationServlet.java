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
        } else if ("checkStatus".equals(action)) {
            handleStatusCheck(request, response);
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
        String experience = trim(request.getParameter("experience"));
        String[] skillsArray = request.getParameterValues("skills");
        String coverLetter = trim(request.getParameter("coverLetter"));

        if (name.isEmpty() || email.isEmpty() || jobRole.isEmpty() || experience.isEmpty() || coverLetter.isEmpty()) {
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

        if (coverLetter.length() < 15) {
            redirectWithError(response, "apply.jsp", "Cover letter should be at least 15 characters.");
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
                request.setAttribute("experience", experience);
                request.setAttribute("coverLetter", coverLetter);
                request.setAttribute("applicationCount", DBConnection.getApplicationCount());
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
            request.setAttribute("applicationCount", DBConnection.getApplicationCount());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Database error: " + ex.getMessage());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        }
    }

    private void handleStatusCheck(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String statusEmail = trim(request.getParameter("statusEmail"));
        String statusApplicationId = trim(request.getParameter("statusApplicationId"));

        if (statusEmail.isEmpty() && statusApplicationId.isEmpty()) {
            redirectWithError(response, "status.jsp", "Enter email or application ID to check status.");
            return;
        }

        Integer applicationId = null;
        if (!statusApplicationId.isEmpty()) {
            try {
                applicationId = Integer.valueOf(statusApplicationId);
            } catch (NumberFormatException ex) {
                redirectWithError(response, "status.jsp", "Application ID must be a valid number.");
                return;
            }
        }

        try {
            List<Map<String, String>> statusResults = DBConnection.getApplicationStatus(statusEmail, applicationId);
            request.setAttribute("statusResults", statusResults);
            request.setAttribute("statusEmail", statusEmail);
            request.setAttribute("statusApplicationId", statusApplicationId);
            request.getRequestDispatcher("status.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Database error: " + ex.getMessage());
            request.getRequestDispatcher("status.jsp").forward(request, response);
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
