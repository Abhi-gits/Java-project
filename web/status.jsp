<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Application Status - Job Application Portal</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="header.jsp" %>

    <main class="container result-page">
        <section class="card result-card">
            <div class="result-header">
                <h2>Check Application Result</h2>
                <p>Enter applicant email or application ID to see whether application is selected, rejected, or pending.</p>
            </div>

            <%
                String error = request.getParameter("error");
                if (error == null || error.trim().isEmpty()) {
                    error = (String) request.getAttribute("error");
                }
                List<Map<String, String>> statusResults = (List<Map<String, String>>) request.getAttribute("statusResults");
                String statusEmail = (String) request.getAttribute("statusEmail");
                String statusApplicationId = (String) request.getAttribute("statusApplicationId");
            %>

            <%
                if (error != null && !error.trim().isEmpty()) {
            %>
                <p class="error-message"><%= error %></p>
            <%
                }
            %>

            <form action="ApplicationServlet" method="post" class="portal-form">
                <input type="hidden" name="action" value="checkStatus">

                <label for="statusEmail">Applicant Email</label>
                <input type="email" id="statusEmail" name="statusEmail" placeholder="e.g. aarav.sharma@example.com"
                       value="<%= statusEmail == null ? "" : statusEmail %>">

                <label for="statusApplicationId">Application ID</label>
                <input type="text" id="statusApplicationId" name="statusApplicationId" placeholder="e.g. 3"
                       value="<%= statusApplicationId == null ? "" : statusApplicationId %>">

                <div class="form-actions">
                    <button type="submit">Check Result</button>
                    <button type="reset" class="secondary-btn">Reset</button>
                </div>
            </form>

            <%
                if (statusResults != null) {
                    if (statusResults.isEmpty()) {
            %>
                <p class="no-data-message">No application result found for provided details.</p>
            <%
                    } else {
            %>
                <h3>Application Results</h3>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Application ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Job Role</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Map<String, String> row : statusResults) {
                            %>
                            <tr>
                                <td><%= row.get("id") %></td>
                                <td><%= row.get("name") %></td>
                                <td><%= row.get("email") %></td>
                                <td><%= row.get("job_role") %></td>
                                <td><strong><%= row.get("status") %></strong></td>
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
        </section>
    </main>

    <%@ include file="footer.jsp" %>
</body>
</html>
