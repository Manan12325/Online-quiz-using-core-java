import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AdvancedQuizApp {
    private static int score = 0;
    private static final int TIME_LIMIT_PER_QUESTION = 30; // seconds
    private static final String[] CATEGORIES = {"Science", "Literature", "History", "Mixed"};
    
    private static class Question {
        String category;
        String question;
        String[] options;
        int correctAnswer;
        int difficulty; // 1-3 (1=easiest, 3=hardest)

        public Question(String category, String question, String[] options, 
                       int correctAnswer, int difficulty) {
            this.category = category;
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.difficulty = difficulty;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        
        // Initialize questions
        Question[] questions = initializeQuestions();
        
        // Display welcome screen
        displayWelcomeBanner();
        
        // Category selection
        int selectedCategory = selectCategory(scanner);
        
        // Start quiz
        startQuiz(scanner, questions, selectedCategory);
        
        // Display results
        displayFinalResults(questions.length);
        
        scanner.close();
    }

    private static Question[] initializeQuestions() {
        return new Question[] {
            new Question("Science", "What is the chemical symbol for gold?", 
                        new String[]{"1. Ag", "2. Fe", "3. Au", "4. Cu"}, 2, 2),
            new Question("Science", "Which planet is known as the Red Planet?", 
                        new String[]{"1. Venus", "2. Mars", "3. Jupiter", "4. Saturn"}, 1, 1),
            new Question("Literature", "Who wrote 'Romeo and Juliet'?", 
                        new String[]{"1. Charles Dickens", "2. William Shakespeare", 
                        "3. Jane Austen", "4. Mark Twain"}, 1, 2),
            new Question("History", "In which year did World War II end?", 
                        new String[]{"1. 1945", "2. 1939", "3. 1950", "4. 1941"}, 0, 3),
            new Question("Mixed", "What is the largest mammal in the world?", 
                        new String[]{"1. African Elephant", "2. Blue Whale", 
                        "3. Giraffe", "4. Hippopotamus"}, 1, 2)
        };
    }

    private static void displayWelcomeBanner() throws InterruptedException {
        System.out.println("\n" + ColorCodes.ANSI_CYAN);
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║      WELCOME TO QUIZ APP     ║");
        System.out.println("╚══════════════════════════════╝" + ColorCodes.ANSI_RESET);
        TimeUnit.SECONDS.sleep(1);
    }

    private static int selectCategory(Scanner scanner) {
        System.out.println("\n" + ColorCodes.ANSI_YELLOW + "Select a category:");
        for(int i = 0; i < CATEGORIES.length; i++) {
            System.out.println((i+1) + ". " + CATEGORIES[i]);
        }
        System.out.print(ColorCodes.ANSI_RESET + "Enter choice (1-" + CATEGORIES.length + "): ");
        return scanner.nextInt() - 1;
    }

    private static void startQuiz(Scanner scanner, Question[] questions, int category) 
            throws InterruptedException {
        System.out.println("\nStarting Quiz...\n");
        TimeUnit.SECONDS.sleep(1);

        for(int i = 0; i < questions.length; i++) {
            if(category != 3 && !questions[i].category.equals(CATEGORIES[category])) 
                continue;

            displayQuestion(questions[i], i+1);
            long startTime = System.currentTimeMillis();
            int userAnswer = getValidAnswer(scanner, questions[i].options.length);
            long timeTaken = (System.currentTimeMillis() - startTime)/1000;

            processAnswer(questions[i], userAnswer, (int)timeTaken);
            displayProgressBar(i+1, questions.length);
        }
    }

    private static void displayQuestion(Question question, int qNumber) {
        System.out.println(ColorCodes.ANSI_PURPLE + "\nQuestion " + qNumber + " (" + question.category + ")");
        System.out.println("[" + "★".repeat(question.difficulty) + "]");
        System.out.println(ColorCodes.ANSI_WHITE + question.question);
        for(String option : question.options) {
            System.out.println("  " + option);
        }
        System.out.print(ColorCodes.ANSI_RESET);
    }

    private static int getValidAnswer(Scanner scanner, int optionsCount) {
        int answer;
        while(true) {
            System.out.print("Your answer (1-" + optionsCount + "): ");
            if(scanner.hasNextInt()) {
                answer = scanner.nextInt();
                if(answer >= 1 && answer <= optionsCount) {
                    return answer - 1;
                }
            } else {
                scanner.next(); // Clear invalid input
            }
            System.out.println(ColorCodes.ANSI_RED + "Invalid input! Please enter between 1-" + optionsCount + ColorCodes.ANSI_RESET);
        }
    }

    private static void processAnswer(Question question, int userAnswer, int timeTaken) {
        String result;
        if(userAnswer == question.correctAnswer) {
            int points = calculatePoints(question.difficulty, timeTaken);
            score += points;
            result = ColorCodes.ANSI_GREEN + "✓ Correct! (" + points + " points)";
        } else {
            result = ColorCodes.ANSI_RED + "✗ Incorrect! Correct answer: " + 
                    (question.correctAnswer + 1);
        }
        System.out.println(result + ColorCodes.ANSI_RESET);
        System.out.println("Time taken: " + timeTaken + "s\n");
    }

    private static int calculatePoints(int difficulty, int timeTaken) {
        int basePoints = difficulty * 10;
        int timeBonus = Math.max(0, TIME_LIMIT_PER_QUESTION - timeTaken) * 2;
        return basePoints + timeBonus;
    }

    private static void displayProgressBar(int current, int total) {
        int progress = (current * 100) / total;
        System.out.print(ColorCodes.ANSI_CYAN + "Progress: [");
        for(int i = 0; i < 50; i++) {
            System.out.print(i < (progress/2) ? "█" : " ");
        }
        System.out.println("] " + progress + "%" + ColorCodes.ANSI_RESET);
    }

    private static void displayFinalResults(int totalQuestions) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        System.out.println(ColorCodes.ANSI_YELLOW + "\n══════════════════════════════");
        System.out.println("         RESULTS          ");
        System.out.println("══════════════════════════════");

        System.out.println("\nTotal Questions: " + totalQuestions);
        System.out.println("Final Score: " + score + " points");
        System.out.printf("Average: %.1f points/question\n", (double)score/totalQuestions);

        System.out.println("\nThanks for playing!" + ColorCodes.ANSI_RESET);
    }

    private static class ColorCodes {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_WHITE = "\u001B[37m";
    }
}