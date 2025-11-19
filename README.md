# 📚 RMIDistributedQuiz - Distributed Score Monitoring System

A distributed student scoring system built with **Java RMI (Remote Method Invocation)**, featuring real-time score tracking, grading, and callback notifications.

## 📋 System Overview

This project demonstrates a classic Client-Server distributed system using **Java RMI**, where the Server manages the quiz logic and scores, and the Client interacts remotely while simultaneously receiving real-time updates via the **RMI Callback** mechanism.

## 🏗️ System Architecture

```
┌─────────────┐    RMI Client Stub    ┌─────────────┐
│    Java     │ ───────────────────► │    Java     │
│    Client   │ ◄─────────────────── │    Server   │
│ (Student)  │   RMI Callback        │  (Quiz Engine) │
└─────────────┘                      └─────────────┘
     │                                      │
     │ Remote Method Calls                   │ Score Management, Leaderboard
     │ (requestQuestion, submitAnswer)      │ Tracking, Callback Scheduling
```

-----

## 🚀 Features

### 🔄 Real-time Communication & Callbacks

  * **RMI Remote Invocation:** Clients invoke methods on the remote `QuizServiceImp` object.
  * **Two-Way Callback:** The server maintains a list of remote client objects and pushes the updated Leaderboard to all subscribers automatically.
      * **Immediate Update:** Sent upon every correct answer.
      * **Periodic Update:** Sent every **30 seconds** regardless of user activity.
  * **Score Tracking:** Tracks individual scores for each registered student.

### 📊 Core Components

| Component | Language | Role | Protocol |
|-----------|----------|------|----------|
| Client | Java | Student interaction, remote method invocation | RMI |
| Server | Java | Quiz logic, score management, Leaderboard generation | RMI Server |
| Registry | JVM | Central naming service for the RMI Server | RMI Registry (Port 1099) |

-----

## 🛠️ Technology Stack

  * **Java RMI (Remote Method Invocation):** For building distributed applications in Java.
  * **Java Sockets:** Underlying mechanism for RMI communication.
  * **Java `java.util.concurrent`:** Used for scheduling the periodic Leaderboard updates.

-----

## 📥 Installation & Setup

### Prerequisites

  * **Java 17+** (JDK must be installed to run RMI tools like `rmiregistry`)
  * **Git**

### 1\. Clone the Repository

```bash
git clone https://github.com/HasanSalamee/RMIDistributedQuiz.git
cd RMIDistributedQuiz
```


### 2\. Compile the Project

Assuming your project uses the package structure `org.ds.HWRMICallbackDemo` and places compiled files in `bin`:

```bash
mkdir bin 
javac -d bin org/ds/HWRMICallbackDemo/shared/*.java org/ds/HWRMICallbackDemo/server/*.java org/ds/HWRMICallbackDemo/client/*.java
```

-----

## 🎯 Quick Start (Testing the System)

You must use **three separate terminal windows** to run the system components in the correct order.

### Step 1: Start the RMI Registry

The Registry serves as the central directory for RMI objects.


### Step 2: Start the Quiz Server

The Server creates and binds the `QuizServiceImp` object to the Registry.

**Terminal 2:**

```bash
java -classpath bin org.ds.HWRMICallbackDemo.server.QuizServer
```

**Expected Output (Server):**

```
✔ RMI Registry created on port 1099.
Added 3 Quiz questions.
✔ Leaderboard update schedule started.
🚀 RMI Quiz Server ready on name: QuizService
```

### Step 3: Start the Client(s)

Start one or more client instances.

**Terminal 3 (and any subsequent terminals):**

```bash
java -classpath bin org.ds.HWRMICallbackDemo.client.Client
```

**Client Interaction:**

1.  **Enter Name:** The client prompts you for your name (e.g., `Ahmad`).
2.  **Registration:** The client registers itself for the Callback service.
3.  **Start Interaction:** Use the available commands:

| Command | Action | Example |
| :---: | :---: | :---: |
| `Question` | Requests a new question. | `Question` |
| `Answer <Text>` | Submits the answer to the latest question. | `Answer 11` |
| `Leaderboard` | Requests the current ranking immediately. | `Leaderboard` |
| `Exit` | Closes the client connection. | `Exit` |

-----

## 🔄 Data Flow Details

### 1\. Remote Invocation (Client to Server)

  * **Client** calls `requestQuestion()` (asking for data).
  * **Server** responds with the random question text and stores the correct answer in `studentLastQuestion`.

### 2\. Score Update and Callback (Server to Clients)

  * **Client** calls `submitAnswer()` with the provided answer.
  * **Server** validates the answer and updates the `studentScores` map.
  * If the answer is correct, the **Server** iterates through the `clientCallbacks` map and calls `callbackObject.updateLeaderboard()` on **all connected clients**.

### 3\. Periodic Callback

  * The `ScheduledExecutorService` on the **Server** calls `sendLeaderboardUpdateToAllClients()` every 30 seconds to refresh the ranking display across all clients automatically.

-----

## 🔧 Configuration

### Network Settings

The system uses default RMI settings:

  * **RMI Registry Port:** 1099
  * **Server Host:** `localhost` (Default setting, can be updated in `Client.java` if running across different physical machines).

-----

## 🐛 Troubleshooting

### Common Issues

1.  **`Connection Refused` or `java.rmi.ConnectException`**:
      * Ensure `rmiregistry` is running first (Terminal 1).
      * Ensure the `QuizServer` is running (Terminal 2) and bound its object successfully.
2.  **`java.rmi.NotBoundException: QuizService`**:
      * The Server failed to bind its object. Check the server console for errors.
3.  **No Callback Updates**:
      * Verify the client successfully printed the registration message (`-> Registering client: ...`).
      * Ensure 30 seconds have passed for the periodic update, or submit a correct answer for an immediate update.

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

  * HasanSalami - Initial work and RMI system design

-----

**⭐ If this RMI project helped you understand distributed systems, please star the repository\!**
