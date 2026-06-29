# 🏫 Campus Complaint Tracking System

A complete, role-based desktop application built with **Java Swing** and **File I/O** to streamline complaint management in universities.

---

## 📌 Overview

Manual complaint handling in universities—paper forms, emails, and office visits—often leads to lost requests, delays, and poor transparency.

**Campus Complaint Tracking System** solves this with a centralized, automated platform that tracks complaints from submission to resolution. Built with clean OOP principles, it supports five user roles, automatic resolver assignment, priority-based deadlines, and real-time notifications.

---

## ✨ Features

- 🔹 **5 User Roles** – The system supports **Students**, **Professors**, **Staff**, **Managers**, and **Resolvers**. Each role has a dedicated dashboard with tailored permissions. Complainants can submit and track issues; Resolvers handle assigned tasks; Managers oversee the entire workflow and generate reports.

- 🔹 **5 Complaint Categories** – Complaints are classified into **Academic**, **Administrative**, **IT**, **Infrastructure**, and **Library**. Each category has a unique set of issue types and point weights, ensuring that different problems (e.g., "Fire Hazard" vs. "Book Missing") are prioritized appropriately.

- 🔹 **Role‑based Dashboards** – Every user sees a customized interface after login. Students and staff can submit complaints and view their history; Resolvers see only their active tasks; Managers access all complaints, statistics, and administrative tools—keeping the UI clean and focused.

- 🔹 **Priority & Deadline Calculation** – Each complaint is assigned a **priority score** based on `Urgency Level (1–5)` + `Issue Points (3–15)`. This score is mapped to a specific deadline (from 7 days down to **1 hour** for critical issues like fire hazards). This ensures that urgent problems are handled immediately.

- 🔹 **Auto‑Assignment of Resolvers** – When a complaint is submitted, the system automatically finds the best resolver based on **category expertise** and **current workload** (fewest pending complaints). If a resolver is flagged by the manager, they are temporarily excluded from new assignments.

- 🔹 **Sequential Status Updates** – Resolvers cannot jump directly to 100%. They must follow a strict sequence: **25% (Read)** → **50% (In Progress)** → **75% (Near Completion)** → **100% (Resolved)**. This ensures every complaint is properly reviewed and processed.

- 🔹 **Feedback System** – Once a complaint is resolved, the complainant can rate their satisfaction on a **0–100% slider**. The average feedback rating is visible to the manager in the resolver performance report, promoting accountability.

- 🔹 **File‑based Notifications** – Every status change, query, or reassignment triggers a notification saved to `notifications.txt`. Users see these alerts in a dedicated panel, keeping everyone informed without requiring an internet connection.

- 🔹 **Manager Reports & Analytics** – Managers can view detailed statistics (complaints per category, resolved vs. pending) and a **resolver performance dashboard** that shows average resolution time and average feedback for each resolver.

- 🔹 **Query & Reassign** – If a complaint is overdue, the manager can send a **query** to the resolver. The resolver receives the query, responds, and the manager can view all responses. If needed, the manager can **reassign** the complaint to another resolver (which also extends the deadline by 48 hours).

- 🔹 **Resolver Flagging** – Managers can **flag** underperforming resolvers. Flagged resolvers are automatically excluded from auto‑assignment and reassignment dropdowns until the manager unflags them—giving managers full control over workload distribution.

---

## 🛠️ Technologies Used

| Component       | Technology                     |
|----------------|--------------------------------|
| Language        | Java (JDK 11+)                |
| GUI Framework   | Java Swing                     |
| Storage         | File I/O (Text files)          |
| Date/Time API   | java.time (LocalDateTime)      |
| OOP Principles  | Encapsulation, Inheritance, Polymorphism, Abstraction |

---

## 📂 Project Structure

```
CampusComplaintSystem/
├── src/com/campuscomplaint/
│   ├── model/          (15 classes – data models + FileHandler)
│   ├── service/        (1 class  – ComplaintService)
│   ├── ui/             (15 classes – Swing GUI components)
│   ├── exception/      (4 classes – custom exceptions)
│   └── util/           (3 classes – utilities)
├── data/               (users.txt, complaints.txt, notifications.txt)
├── resources/images/   (logo.png)
├── run.bat             (Windows launcher)
└── README.md
```

---

## 🚀 How to Run

### Windows
1. Double‑click `run.bat`
2. The splash screen appears for 5 seconds, then the login screen opens.

### Manual Compilation
```bash
cd CampusComplaintSystem/src
javac -d . com/campuscomplaint/Main.java com/campuscomplaint/model/*.java com/campuscomplaint/service/*.java com/campuscomplaint/ui/*.java com/campuscomplaint/exception/*.java com/campuscomplaint/util/*.java
java com.campuscomplaint.Main
```

---

## 👤 Default Accounts

| Role         | User ID    | Password  |
|--------------|------------|-----------|
| Manager      | MGR-01     | admin123  |
| Resolver (IT)| RES-IT     | respass   |
| Resolver (Academic) | RES-ACAD | respass |
| Resolver (Administrative) | RES-ADMIN | respass |
| Resolver (Infrastructure) | RES-INFRA | respass |
| Resolver (Library) | RES-LIB   | respass   |
| Resolver (General) | RES-GEN   | respass   |
| Student      | STU-001    | pass123   |
| Professor    | PROF-001   | pass123   |
| Staff        | STF-001    | pass123   |

---

## 🖥️ Walkthrough – Screenshots

*(All screenshots are stored in `docs/images/`)*

### 1. Splash Screen
![Splash Screen](docs/images/splash-screen.JPG)

---

### 2. Login Screen
![Login Screen](docs/images/login.JPG)

---

### 3. Sign Up (Student/Professor/Staff)
![Sign Up](docs/images/signup.JPG)

---

### 4. Complainant Dashboard
![Student Dashboard](docs/images/complainant-dashboard.JPG)

---

### 5. Submit Complaint
![Submit Complaint](docs/images/submit-complaint.JPG)

---

### 6. Resolver Dashboard – Active Complaints
![Resolver Dashboard 1](docs/images/resolver-dashboard1.JPG)

---

### 7. Resolver Dashboard – Resolved Complaints
![Resolver Dashboard 2](docs/images/resolver-dashboard2.JPG)

---

### 8. Resolver – Sequential Status Update
![Resolver Status](docs/images/resolver-status.JPG)

---

### 9. Pending Queries (Resolver)
![Pending Queries](docs/images/pending-queries.JPG)

---

### 10. Resolver Responses (Manager View)
![Resolver Responses](docs/images/resolver-responses.JPG)

---

### 11. Manager Dashboard – Submitted Tab
![Manager Dashboard](docs/images/manager-dashboard.JPG)

---

### 12. Manager – Complaint Statistics
![Complaint Statistics](docs/images/complaint-statistics.JPG)

---

### 13. Manager – Resolver Performance
![Resolver Performance](docs/images/resolver-performance.JPG)

---

### 14. Feedback Dialog
![Feedback](docs/images/complaint-feedback.JPG)

---

## 🔑 Key OOP Concepts Used

| Concept            | Implementation |
|--------------------|----------------|
| **Encapsulation**  | Private fields with public getters/setters in `User`, `Complaint`, etc. |
| **Inheritance**    | `User` abstract base → `Student`, `Professor`, `Staff`, `Manager`, `Resolver`. <br> `ComplaintCategory` → 5 concrete categories. |
| **Polymorphism**   | `IComplaint` interface implemented by `Student`, `Professor`, `Staff`. |
| **Abstraction**    | Abstract classes `User` and `ComplaintCategory`. |
| **Singleton**      | `SessionManager` tracks current logged‑in user. |
| **Utility Class**  | `FileHandler` (static methods for file I/O). |

---

## 📈 Future Enhancements

- Email/SMS notifications for status updates
- MySQL database integration
- Graphical dashboards (charts and graphs)
- Mobile companion app (Android/iOS)

---

## 👨‍💻 Developers

| Name | Registration ID |
|------|----------------|
| **Moeen Ali Abdullah Awan** | FA25-BCS-058 |
| **Mohammad Ali Mughal** | FA25-BCS-059 |

**Course:** CSC241 – Object Oriented Programming   
**University:** COMSATS University Islamabad  
**Semester:** Spring 2026

---

## 📄 License

This project is for academic purposes only.

---

**© 2026 Campus Complaint Tracking System**