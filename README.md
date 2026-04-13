Synapse Search 🧠✨
Search Smarter, Not Harder. Your Intelligent Gateway to the Web's Knowledge.
Welcome to Synapse Search! This isn't just another search engine. It's a smart research assistant I built to cut through the noise of the internet. Instead of giving you a list of links to dig through, Synapse Search reads them for you and provides a clear, comprehensive summary powered by Google's Gemini AI.

This project is a key part of my personal portfolio, showcasing a full-stack application built with Java and Spring Boot.

✨ Key Features
🧠 AI-Powered Summaries: Get intelligent, easy-to-understand answers instead of just links.

🔍 Real-Time Web Search: All answers are based on the most current information available on the web, thanks to the Google Search API.

💡 Smart Suggestions: As you type, get instant suggestions to complete your thoughts, just like in Google.

🌐 Show Your Sources: See the exact web pages the AI used to create its summary, ensuring transparency and trust.

❓ Follow-Up Questions: Discover new avenues of inquiry with AI-suggested related questions.

🗣️ Multi-Language Support: Translate any AI-generated summary into Sinhala or Tamil with a single click.

🔒 Secure Google Login: Your data is safe with secure authentication via Google OAuth2.

📖 Search History: Keep track of your past searches and revisit them anytime.

👤 Full Account Management: Clear your history or delete your account with ease.

🛠️ Technology Stack
Component

Technology Used

Backend

Java 17, Spring Boot 3.x

Database

MySQL

Security

Spring Security (Google OAuth2)

Build Tool

Gradle

Frontend

HTML, CSS, Vanilla JavaScript, Thymeleaf

AI & Search

Google Gemini API, Google Custom Search API

Key Deps

spring-data-jpa, spring-security, mysql-connector-j, jsoup

🚀 Getting Started
To get a local copy up and running, follow these simple steps.

Prerequisites
JDK 17 or later

Gradle 7.0 or later

MySQL Server (e.g., via XAMPP, WAMP, or standalone installation)

Installation & Setup
Clone the repository:

git clone https://github.com/your-username/ai-search-app.git
cd ai-search-app

Database Setup:

Ensure your MySQL server is running.

Open a MySQL client (like phpMyAdmin, MySQL Workbench, or the command line).

Create a new, empty database. You can name it ai_search.

CREATE DATABASE ai_search;

Configure application.properties:

⚠️ Important: This file is intentionally excluded from the repository via .gitignore to protect your secret keys. You must create it manually.

In the project, navigate to src/main/resources/.

Create a new file named application.properties.

Copy the content below into your new file and update the placeholder values for your local environment.

# Server Port
server.port=8080

# Google OAuth2 Login Configuration
# Replace with your own credentials from Google Cloud Console
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID_HERE
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET_HERE
spring.security.oauth2.client.registration.google.scope=openid,profile,email

# External API Keys
# Replace with your own keys from Google Cloud & AI Studio
google.search.api.key=YOUR_GOOGLE_API_KEY_HERE
google.search.engine.id=YOUR_SEARCH_ENGINE_ID_HERE
gemini.api.key=YOUR_GEMINI_API_KEY_HERE

# Database Connection (MySQL)
# Replace 'your_mysql_password' with your MySQL root password (or leave blank if none).
spring.datasource.url=jdbc:mysql://localhost:3306/ai_search?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password

# JPA and Hibernate Properties
# 'update' will automatically create and update tables based on your @Entity classes.
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true

Build and Run the Application:

Open a terminal in the project's root directory.

Run the application using the Gradle wrapper:

./gradlew bootRun

The application will start, and the necessary database tables will be created automatically. You can access it at http://localhost:8080.

A Project by h7.