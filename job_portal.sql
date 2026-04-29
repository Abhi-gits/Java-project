
DROP TABLE user_skills;
DROP TABLE applications;
DROP TABLE skills;
DROP TABLE jobs;
DROP TABLE users;


--------------------------------------------------------------


CREATE TABLE users (
    id AUTOINCREMENT PRIMARY KEY,
    name TEXT(100) NOT NULL,
    email TEXT(150) NOT NULL,
    [password] TEXT(255) NOT NULL
);

CREATE UNIQUE INDEX idx_users_email ON users(email);


CREATE TABLE jobs (
    id AUTOINCREMENT PRIMARY KEY,
    title TEXT(100) NOT NULL,
    description LONGTEXT,
    location TEXT(100),
    posted_date DATETIME DEFAULT Date()
);


CREATE TABLE skills (
    id AUTOINCREMENT PRIMARY KEY,
    skill_name TEXT(100) NOT NULL
);

CREATE UNIQUE INDEX idx_skills_name ON skills(skill_name);


CREATE TABLE user_skills (
    user_id LONG NOT NULL,
    skill_id LONG NOT NULL,
    CONSTRAINT pk_user_skills PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
);


CREATE TABLE applications (
    id AUTOINCREMENT PRIMARY KEY,
    user_id LONG NOT NULL,
    job_id LONG NOT NULL,
    applied_date DATETIME DEFAULT Now(),
    status TEXT(50) DEFAULT 'Pending',
    CONSTRAINT fk_app_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(id)
);



