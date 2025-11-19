package org.ds.HWRMICallbackDemo.client;

import org.ds.HWRMICallbackDemo.shared.IClientCallback;
import org.ds.HWRMICallbackDemo.shared.IQuizService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String studentName = "";
        Scanner scanner = new Scanner(System.in);
        try {
            // Locate RMI Registry on the default port (1099)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            // Look up the remote object
            IQuizService quizService = (IQuizService) registry.lookup("QuizService");

            System.out.print("Enter your name: ");
            studentName = scanner.nextLine();

            // Create and register the Callback object
            IClientCallback callbackImpl = new ClientCallbackImp();
            quizService.registerForCallback(studentName, callbackImpl);

            System.out.println("\n--- Welcome, " + studentName + ", to the RMI Quiz ---");
            System.out.println("You can now send commands and will receive Leaderboard updates (Callback).");
            System.out.println("Available commands: Question | Answer <Text> | Leaderboard | Exit");
            System.out.println("--------------------------------------------------\n");

            // 5. Main interaction loop
            while (true) {
                System.out.print("> Enter your command (Question/Answer/Leaderboard/Exit): ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("Exit")) {
                    System.out.println("Goodbye. Closing client connection.");
                    break;
                }

                // Request a question
                if (input.equalsIgnoreCase("Question")) {
                    String questionText = quizService.requestQuestion(studentName);
                    System.out.println("<< [Server]: " + questionText);

                    // Submit the answer
                } else if (input.toLowerCase().startsWith("answer ")) {
                    String answer = input.substring("answer ".length()).trim();
                    String feedback = quizService.submitAnswer(studentName, answer);
                    System.out.println("<< [Server]: " + feedback);

                    // Request the current leaderboard directly
                } else if (input.equalsIgnoreCase("Leaderboard")) {
                    String leaderboard = quizService.getLeaderboard();
                    System.out.println("<< [Server]: \n" + leaderboard);

                } else {
                    System.out.println(" Unrecognized command.");
                }
            }

        } catch (RemoteException e) {
            System.err.println("Remote communication error: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            System.err.println("RMI Service not found: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}