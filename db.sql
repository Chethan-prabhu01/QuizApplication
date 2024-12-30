-- Create database
CREATE DATABASE IF NOT EXISTS QUIZZ;
USE QUIZZ;

-- Create table for users
CREATE TABLE uss (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL
);

-- Create table for quizzess
CREATE TABLE quizzess (
    quiz_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

-- Create table for questions
CREATE TABLE questionss (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option1 VARCHAR(255) NOT NULL,
    option2 VARCHAR(255) NOT NULL,
    option3 VARCHAR(255) NOT NULL,
    option4 VARCHAR(255) NOT NULL,
    correct_option INT NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzess(quiz_id) ON DELETE CASCADE
);

-- Create table for quiz history
CREATE TABLE quiz_historyy (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES uss(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzess(quiz_id) ON DELETE CASCADE
);