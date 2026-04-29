<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Result - Job Application Portal</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="header.jsp" %>

    <main class="container result-page">
        <section class="card result-card">
            <div class="result-header">
                <h2>Operation Result</h2>
                <p>Check submission status or review search output.</p>
            </div>

            <%
                String message = (String) request.getAttribute("message");
                String error = (String) request.getAttribute("error");
                Integer skillsCount = (Integer) request.getAttribute("skillsCount");
                String experience = (String) request.getAttribute("experience");
                String coverLetter = (String) request.getAttribute("coverLetter");
                String searchRole = (String) request.getAttribute("searchRole");
                List<Map<String, String>> applications = (List<Map<String, String>>) request.getAttribute("applications");
                boolean hasApplications = applications != null;
                boolean hasFeedback = (message != null && !message.trim().isEmpty())
                        || (error != null && !error.trim().isEmpty());
                boolean isEmptyState = !hasFeedback && !hasApplications;
            %>

            <%
                if (error != null && !error.trim().isEmpty()) {
            %>
                <p class="error-message"><%= error %></p>
            <%
                } else if (message != null && !message.trim().isEmpty()) {
            %>
                <p class="success-message"><%= message %></p>
                <div class="result-summary">
                    <%
                        if (skillsCount != null) {
                    %>
                        <span class="summary-pill">Selected skills: <strong><%= skillsCount %></strong></span>
                    <%
                        }
                        if (experience != null && !experience.isEmpty()) {
                    %>
                        <span class="summary-pill">Experience: <strong><%= experience %></strong></span>
                    <%
                        }
                        if (coverLetter != null && !coverLetter.isEmpty()) {
                    %>
                        <span class="summary-pill">Cover letter: <strong>Submitted</strong></span>
                    <%
                        }
                    %>
                </div>
            <%
                }
            %>

            <%
                if (applications != null) {
                    if (applications.isEmpty()) {
            %>
                                            <p class="no-data-message">No results found for the selected role.</p>
            <%
                    } else {
            %>
                                            <h3>Search Results for: <%= searchRole %></h3>
                                            <div class="table-wrapper">
                                                <table>
                                                    <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Name</th>
                                                            <th>Email</th>
                                                            <th>Job Role</th>
                                                            <th>Skills</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <%
                                                            for (Map<String, String> row : applications) {
                                                        %>
                                                                <tr>
                                                                    <td><%= row.get("id") %></td>
                                                                    <td><%= row.get("name") %></td>
                                                                    <td><%= row.get("email") %></td>
                                                                    <td><%= row.get("job_role") %></td>
                                                                    <td><%= row.get("skills") %></td>
                                                                </tr>
                                                        <%
                                                            }
                                                        %>
                                                    </tbody>
                                                </table>
                                            </div>
            <%
                    }
                }
            %>

            <%
                if (isEmptyState) {
            %>
                <div class="result-empty">
                    <p>No result to display yet. Submit a new application or run a role search.</p>
                </div>
            <%
                }
            %>

            <div class="result-actions">
                <a href="apply.jsp" class="cta-btn primary-link">Go to Apply Page</a>
                <a href="search.jsp" class="cta-btn secondary-link">Go to Search Page</a>
            </div>
        </section>
    </main>

    <%@ include file="footer.jsp" %>
</body>
</html>
