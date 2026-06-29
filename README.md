# 🏫 Campus Complaint Tracking System

A complete, role-based desktop application built with **Java Swing** and **File I/O** to streamline complaint management in universities.

---

## 📌 Overview

Manual complaint handling in universities—paper forms, emails, and office visits—often leads to lost requests, delays, and poor transparency.

**Campus Complaint Tracking System** solves this with a centralized, automated platform that tracks complaints from submission to resolution. Built with clean OOP principles, it supports five user roles, automatic resolver assignment, priority-based deadlines, and real-time notifications.

---

## ✨ Features

- ✅ **5 User Roles** – Student, Professor, Staff, Manager, Resolver
- ✅ **5 Complaint Categories** – Academic, Administrative, IT, Infrastructure, Library
- ✅ **Role‑based Dashboards** – tailored views for each user type
- ✅ **Priority & Deadline Calculation** – dynamic deadlines based on issue type and urgency
- ✅ **Auto‑Assignment** – automatically assigns resolvers based on category and workload
- ✅ **Sequential Status Updates** – 25% → 50% → 75% → 100%
- ✅ **Feedback System** – 0–100% satisfaction rating
- ✅ **File‑based Notifications** – real‑time alerts for all users
- ✅ **Manager Reports** – resolver performance, complaint statistics, and overdue tracking
- ✅ **Query & Reassign** – manager can query resolvers and reassign overdue complaints
- ✅ **Resolver Flagging** – manager can flag/unflag resolvers to control workload

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
![Splash Screen](docs/images/splash-screen.jpg)

---

### 2. Login Screen
![Login Screen](docs/images/login.jpg)

---

### 3. Sign Up (Student/Professor/Staff)
![Sign Up](docs/images/signup.jpg)

---

### 4. Complainant Dashboard
![Student Dashboard](docs/images/complainant-dashboard.jpg)

---

### 5. Submit Complaint
![Submit Complaint](docs/images/submit-complaint.jpg)

---

### 6. Resolver Dashboard – Active Complaints
![Resolver Dashboard 1](docs/images/resolver-dashboard1.jpg)

---

### 7. Resolver Dashboard – Resolved Complaints
![Resolver Dashboard 2](docs/images/resolver-dashboard2.jpg)

---

### 8. Resolver – Sequential Status Update
![Resolver Status](docs/images/resolver-status.jpg)

---

### 9. Notifications Panel
![Notifications](docs/images/notifications.jpg)

---

### 10. Pending Queries (Resolver)
![Pending Queries](docs/images/pending-queries.jpg)

---

### 11. Resolver Responses (Manager View)
![Resolver Responses](docs/images/resolver-responses.jpg)

---

### 12. Manager Dashboard – Submitted Tab
![Manager Dashboard](docs/images/manager-dashboard.jpg)

---

### 13. Manager – Complaint Statistics
![Complaint Statistics](docs/images/complaint-statistics.jpg)

---

### 14. Manager – Resolver Performance
![Resolver Performance](docs/images/resolver-performance.jpg)

---

### 15. Feedback Dialog
![Feedback](docs/images/complaint-feedback.jpg)

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