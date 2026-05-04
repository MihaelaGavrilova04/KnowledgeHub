# KnowledgeHub API Plan - Essential

**Base URL:** /api/v1  
**Format:** application/json

---

## 1. Content Upload & Publishing
*Covers: Content Upload, Organization, and Knowledge Contribution*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | /content | Качване на документ (книга, статия, изследване) |
| POST | /content/{id}/categories | Организиране на документ в категория (рафт) |

## 2. Search & Discovery
*Covers: Search and Discovery System, Recommendation Engine*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | /content | Търсене и филтриране (keyword, type, language) |
| GET | /content/suggestions | Препоръчано съдържание (Recommendation engine) |
| GET | /categories | Списък с категории за навигация |

## 3. Reading Lists & Bookmarks
*Covers: User Reading Lists and Bookmarks*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | /reading-lists | Създаване на персонална колекция |
| POST | /reading-lists/{id}/items | Добавяне на материал към списък за по-късно |
| POST | /bookmarks | Маркиране на конкретна страница (отметка) |

## 4. Collaborative Annotation & Discussion
*Covers: Collaborative Annotation and Discussion*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | /annotations | Създаване на анотация или подчертаване в текста |
| POST | /comments | Публикуване на коментар за дискусия |
| GET | /content/{id}/interactions | Извличане на всички бележки и коментари за документ |

## 5. Auth
*Required for personalized features*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | /auth/register | Регистрация |
| POST | /auth/login | Вход |
