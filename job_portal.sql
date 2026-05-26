CREATE DATABASE IF NOT EXISTS job_portal;
USE job_portal;

DROP TABLE IF EXISTS applications;

CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    skills VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Pending'
);

INSERT INTO applications (name, email, job_role, skills, status) VALUES
('Aarav Sharma', 'aarav.sharma@example.com', 'Developer', 'Java, SQL, Web Dev', 'Selected'),
('Neha Verma', 'neha.verma@example.com', 'Designer', 'Web Dev, Python', 'Pending'),
('Rohan Mehta', 'rohan.mehta@example.com', 'Analyst', 'SQL, Python', 'Rejected'),
('Priya Nair', 'priya.nair@example.com', 'Developer', 'Java, Web Dev', 'Selected'),
('Karan Singh', 'karan.singh@example.com', 'Analyst', 'SQL, Java', 'Pending');
