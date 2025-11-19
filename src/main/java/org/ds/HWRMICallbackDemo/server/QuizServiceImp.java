package org.ds.HWRMICallbackDemo.server;

import org.ds.HWRMICallbackDemo.shared.IClientCallback;
import org.ds.HWRMICallbackDemo.shared.IQuizService;
import org.ds.HWRMICallbackDemo.shared.Question;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuizServiceImp extends UnicastRemoteObject implements IQuizService {


    protected QuizServiceImp() throws RemoteException {
        super();
        initializeQuestions(); // Initialize questions on startup
        startLeaderboardUpdater();
    }

    // Data structures for Server state
    private final Map<String, IClientCallback> clientCallbacks = new ConcurrentHashMap<>();
    private final List<Question> questions = new ArrayList<>();
    private final Map<String, Integer> studentScores = new ConcurrentHashMap<>();
    private final Map<String, Question> studentLastQuestion = new ConcurrentHashMap<>();
    private final Random random = new Random();


    private void initializeQuestions() {
        questions.add(new Question("3+8", "11"));
        questions.add(new Question("7*8", "56"));
        questions.add(new Question("2-1", "1"));
        System.out.println("Added " + questions.size() + " Quiz questions."); // Translated
    }

    @Override
    public String requestQuestion(String studentName) throws RemoteException {
        studentScores.putIfAbsent(studentName, 0);

        if (questions.isEmpty()) {
            return "NO_QUESTIONS_AVAILABLE";
        }

        Question q = questions.get(random.nextInt(questions.size()));
        studentLastQuestion.put(studentName, q);


        return "Question for " + studentName + ": " + q.getText();
    }

    @Override
    public String submitAnswer(String studentName, String answer) throws RemoteException {

        Question lastQuestion = studentLastQuestion.get(studentName);
        if (lastQuestion == null) {

            return " Error: No current question pending. Send 'Question' first.";
        }

        String correctAnswer = lastQuestion.getAnswer();
        int currentScore = studentScores.get(studentName);
        String feedback;
        boolean isCorrect = answer.trim().equalsIgnoreCase(correctAnswer);

        if (isCorrect) {
            int newScore = currentScore + 1;
            studentScores.put(studentName, newScore);

            feedback = " Correct Answer! Your current score is: " + newScore;
        } else {
            feedback = " Wrong Answer. The correct answer was: **" + correctAnswer + "**.\n"
                    + "Your current score: " + currentScore;
        }

        studentLastQuestion.remove(studentName);

        if (isCorrect) {
            sendLeaderboardUpdateToAllClients();
        }

        return feedback;
    }


    @Override
    public String getLeaderboard() throws RemoteException {
        return generateLeaderboard();
    }

    @Override
        public void registerForCallback(String studentName, IClientCallback callbackObject) throws RemoteException {
            System.out.println("-> Registering client: " + studentName + " for Callback.");
            clientCallbacks.put(studentName, callbackObject);
            callbackObject.updateLeaderboard(generateLeaderboard());
        }

    public String generateLeaderboard() {
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(studentScores.entrySet());
        sortedScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));


        StringBuilder leaderboardMessage = new StringBuilder("--- Current Leaderboard ---\n");
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedScores) {
            if (count >= 3) break;
            leaderboardMessage.append((count + 1)).append(". ")
                    .append(entry.getKey()).append(": ")

                    .append(entry.getValue()).append(" points\n");
            count++;
        }
        leaderboardMessage.append("---------------------------\n"); // Adjusted line for aesthetics
        return leaderboardMessage.toString();

    }
    private void sendLeaderboardUpdateToAllClients() {
        String leaderboardData = generateLeaderboard();

        clientCallbacks.forEach((name, callback) -> {
            try {
                callback.updateLeaderboard(leaderboardData);
            } catch (RemoteException e) {
                // Translated message
                System.err.println("Callback failed for client " + name + ": " + e.getMessage());
                clientCallbacks.remove(name);
            }
        });
    }
    private void startLeaderboardUpdater() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::sendLeaderboardUpdateToAllClients,
                10, // Initial delay
                30, // Repeat every 30 seconds
                TimeUnit.SECONDS);

        System.out.println("Leaderboard update schedule started.");
    }
}