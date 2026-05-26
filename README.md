# Job Application Portal

A dynamic Java web application built with JSP, Servlets, JDBC, and MySQL.  
Users can submit job applications and search submitted applications by job role.

## Tech Stack

- Java (Maven WAR project)
- JSP + Jakarta Servlets (Tomcat 10+ compatible)
- JDBC + MySQL
- HTML5 + CSS3
- Apache NetBeans
- Apache Tomcat 10+ / 11

## Features

- Apply Job form with client-side and server-side validation
- Search applications by job role
- Result page with status messages and tabular data
- Session usage (`userName`) and cookie usage (`preferredJobRole`)
- Reusable `header.jsp` and `footer.jsp`
- Modern responsive UI

## Project Structure

```text
Job Application Portal/
├── pom.xml
├── job_portal.sql
├── src/
│   └── java/
│       └── com/jobportal/
│           ├── controller/
│           │   └── ApplicationServlet.java
│           └── util/
│               └── DBConnection.java
└── web/
    ├── index.jsp
    ├── apply.jsp
    ├── search.jsp
    ├── result.jsp
    ├── about.jsp
    ├── header.jsp
    ├── footer.jsp
    ├── css/
    │   └── style.css
    └── WEB-INF/
        └── web.xml
```

## Prerequisites

- JDK 17+ (JDK 25 is also fine)
- Maven 3.8+
- MySQL 8+
- Tomcat 10+ (Jakarta namespace)
- NetBeans (latest recommended)

## Database Setup

1. Open MySQL client (MySQL Workbench / CLI).
2. Execute:
   - `job_portal.sql`
3. Confirm database/table:
   - Database: `job_portal`
   - Table: `applications`

### Database Script Files

- `job_portal.sql`: Active MySQL schema used by the running web application.
- `job_portal-database.txt`: Legacy/reference MS Access-style schema and seed script kept for documentation/migration reference only. It is not executed by this project runtime.

### Table Columns (Current)

- `id` (AUTO_INCREMENT, PK)
- `name`
- `email`
- `job_role`
- `skills`

## Configure Database Credentials

The application reads database credentials from environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

If these are not set, local development defaults are used:

- URL: `jdbc:mysql://127.0.0.1:3306/job_portal?allowPublicKeyRetrieval=true&sslMode=DISABLED&serverTimezone=UTC`
- Username: `root`
- Password: `root`

## Deploy on Render

This repository includes:

- `Dockerfile` (multi-stage Maven build + Tomcat runtime)
- `render.yaml` (Render Blueprint service definition)
- `.dockerignore` (faster/cleaner container builds)

### Steps

1. Push this repo to GitHub.
2. In Render, create a new service using **Blueprint** and select this repository.
3. Set these required environment variables in Render:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
4. Deploy.

Render provides a dynamic `PORT` automatically. The container startup script maps Tomcat to that port.

### Health Check

- Health check path is configured as `/` in `render.yaml`.

## Run in NetBeans (Recommended)

1. Open the project folder in NetBeans.
2. Let Maven download dependencies from `pom.xml`.
3. Configure Tomcat 10+/11 as the server.
4. Run the project.
5. Open:
   - `http://localhost:8080/job-application-portal/`


## Main Pages

- Home: `/index.jsp`
- Apply Job: `/apply.jsp`
- Search: `/search.jsp`
- About: `/about.jsp`
- Servlet endpoint: `/ApplicationServlet`

## Notes

- `web.xml` uses Jakarta schema (`web-app 5.0`), so project targets Tomcat 10+.
- Group member details are currently read from `web.xml` context params (not from DB).

## Troubleshooting

- **404 on app URL**: Check WAR is deployed under correct context path.
- **DB error on submit/search**: Verify MySQL is running and credentials are correct.
- **UI not updating**: Rebuild/redeploy WAR and hard refresh browser (`Cmd/Ctrl + Shift + R`).
- **Servlet import issues**: Ensure runtime is Tomcat 10+ (Jakarta), not Tomcat 9.
- **Render deploy starts but app unavailable**: Confirm the service deployed from `Dockerfile` and health check path is `/`.
- **Render DB connection error**: Verify `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` are set correctly in Render.
