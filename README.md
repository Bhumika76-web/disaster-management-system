# 🚨 Disaster Management & Alert System

## Project Overview
A comprehensive disaster management system built with Java Swing and MySQL that enables real-time coordination between citizens, responders, and administrators during emergencies.

## Features
- **Real-time Disaster Alerts**: Live disaster map visualization
- **Role-based Access**: Admin, Responder, and Citizen dashboards
- **Help Request Management**: Citizens request help, responders respond
- **Rescue Operations**: Track and manage rescue tasks
- **Risk Assessment**: Analytics on disaster patterns and citizen engagement
- **Notification System**: Track alert acknowledgment rates

## Technology Stack
- **Frontend**: Java Swing
- **Backend**: Java
- **Database**: MySQL
- **Design Pattern**: DAO (Data Access Object)
- **IDE**: IntelliJ IDEA / Eclipse

## Project Structure
src/
├── ui/
│   ├── LoginRegisterPanel.java
│   ├── AdminDashboard.java
│   ├── CitizenDashboard.java
│   ├── ResponderDashboard.java
│   ├── RescueOperationsPage.java
│   ├── RiskAssessmentPage.java
│   └── AlertsPage.java
├── models/
│   ├── User.java
│   ├── Disaster.java
│   ├── DisasterReport.java
│   ├── HelpRequest.java
│   ├── ResponderTask.java
│   └── NotificationAcknowledgment.java
├── dao/
│   ├── UserDAO.java
│   ├── DisasterDAO.java
│   ├── DisasterReportDAO.java
│   ├── HelpRequestDAO.java
│   ├── ResponderTaskDAO.java
│   └── NotificationAcknowledgmentDAO.java
└── database/
└── DatabaseConnection.java
