# QueryWeave 🧠✨
Search Smarter, Understand Faster.

QueryWeave is an intelligent search assistant designed to cut through the noise of the web. Instead of showing a list of links, it reads multiple sources, understands them, and delivers a clear, structured answer with key insights and follow-up questions.

This project is a full-stack application built with Java Spring Boot and enhanced with a custom AI pipeline powered by Hugging Face models.

---

## 🌟 Introduction

QueryWeave transforms traditional search into a guided learning experience.

- It fetches real-time data from the web
- Extracts meaningful content from multiple sources
- Generates a concise, human-readable summary
- Suggests what to explore next

This project also highlights strong backend engineering, API integration, and AI-assisted processing.

---

## ✨ Key Features

- 🧠 **AI-Powered Summaries**  
Get clear, structured answers instead of raw search results.

- 🔍 **Real-Time Web Search**  
Powered by Google Custom Search API for up-to-date information.

- 🌐 **Transparent Sources**  
Every answer includes the exact links used to generate it.

- ❓ **Follow-Up Questions**  
Smart, natural next questions to continue your research.

- 📖 **Search History**  
Track and revisit your past searches anytime.

- 🔒 **Secure Google Login**  
Authentication handled via Google OAuth2.

- 👤 **Account Management**  
Delete history or remove your account securely.

- ⚡ **Custom AI Pipeline (HF Space)**  
Instead of using third-party black-box APIs, QueryWeave uses a custom Hugging Face Space with open models to generate summaries and insights.

---

## 🛠️ Technology Stack

| Component | Technology |
|----------|-----------|
| Backend | Java 17, Spring Boot 3.x |
| Database | MySQL |
| Security | Spring Security (Google OAuth2) |
| Build Tool | Gradle |
| Frontend | HTML, CSS, Vanilla JavaScript, Thymeleaf |
| AI Engine | Hugging Face Inference (Qwen / Gemma models) |
| Search | Google Custom Search API |
| Scraping | Jsoup |

---

## ⚙️ How It Works

1. User submits a query  
2. Google Search API fetches relevant links  
3. Jsoup extracts content from those pages  
4. Content is sent to a Hugging Face Space  
5. AI model generates:
   - Answer
   - Key points
   - Follow-up questions  
6. Results are displayed with sources

---

## 🚀 Getting Started

### ✅ Prerequisites

- JDK 17+
- Gradle 7+
- MySQL Server

---

### 🔧 Installation

#### 1. Clone the repository

```bash
git clone https://github.com/hashan-7/QueryWeave.git
cd queryweave

```

### 📄 License

This project is licensed under the MIT License.

<p align="center">Made with 💜 by h7</p>
