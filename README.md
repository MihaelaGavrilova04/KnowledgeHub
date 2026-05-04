# KnowledgeHub

**KnowledgeHub** is a collaborative digital library where users upload, organize, and discover content with personalized reading lists and smart search. It enables real-time annotations, discussions, AI-powered recommendations, and user publishing—transforming reading into a living knowledge ecosystem.

---

## Business Requirements

### Basic Requirements
*   **Content Upload and Organization:** Enable users to upload books, articles, and research materials, organizing them into categories like shelves in an ever-expanding library.
*   **Search and Discovery System:** Implement advanced search and filtering tools, helping users navigate knowledge like explorers charting a path through a vast archive.
*   **User Reading Lists and Bookmarks:** Allow users to save and organize materials for later reading, creating personalized collections like curated exhibitions of ideas.

### Advanced Requirements
*   **Collaborative Annotation and Discussion:** Provide tools for highlighting, commenting, and discussing documents, transforming reading into a shared intellectual dialogue.
*   **Recommendation Engine:** Use machine learning to suggest relevant materials based on user interests, guiding learners like a knowledgeable librarian.
*   **Knowledge Contribution and Publishing:** Allow users to publish their own research or articles, cultivating a living ecosystem of knowledge where ideas continuously grow.

---

## Tech Stack

### Backend
*   **Java 21** (LTS)
*   **Spring Boot 3.5.14**
*   **Maven** (Build Tool)
*   **PostgreSQL** (Database)
*   **Spring Security** (Authentication & Authorization)
*   **Lombok & Validation**

### Frontend
*   **React**
*   **Node.js**

---

## CI/CD Pipeline
The project uses **GitHub Actions** for Continuous Integration.
*   Runs on every `push` and `pull_request`.
*   Includes **Checkstyle** for code quality.
*   Runs automated tests with **JUnit 5**.

---

##  Project Structure
```text
.
├── client/          # React Frontend
├── server/          # Spring Boot Backend
├── .github/         # CI/CD Workflows
└── .gitignore       # Root Git Ignore
