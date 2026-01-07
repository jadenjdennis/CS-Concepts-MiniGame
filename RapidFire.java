import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class RapidFire {
    public static void setRapid() {
        int lives = 3;
        int streak = 0;
        startMode(myProgram.questions, lives, streak);
    }
    
    public static void startMode(ArrayList<Question> questions, int lives, int streak) {
        
        Scanner input = new Scanner(System.in);
        
        boolean keepGoing = true;
        int iteration;
        boolean shouldAsk = false;
        
        //Instantly asks 1 guaranteed question before they can leave if they want
        do {
            iteration = (int) (Math.random() * (questions.size() - 8)) + 8;
            System.out.println(questions.size());
            System.out.println(iteration); //to test
            System.out.println(questions.get(iteration).getQuestion());
            
            //Prints out options, and then they will pick the index val 
            //of what they think is the right answer (0 - 3)
            
            for (int i = 0; i < 4; i++) {
                System.out.println(questions.get(iteration).getOptions()[i + 1]);
            }
            
            System.out.print("Select an answer choice (0 - 3): ");
            int choice = input.nextInt();
            
            if (choice == questions.get(iteration).getCorrectAnswerIndex()) {
                streak++;
            }
            
            else {
                lives--;
                streak = 0;
            }
            
            input.nextLine();
        
            //If you have a life left you can play again otherwise it goes home
            if (lives != 0 && choice == questions.get(iteration).getCorrectAnswerIndex()) {
                System.out.println("Congrats, you got the answer right!!!");
                System.out.println("Your streak is now " + streak + ", and you have " + lives + " lives left!");
                
                System.out.print("Would you like to keep playing? (Y/N): ");
                String theDecision = input.nextLine();
                
                if (theDecision.equals("N")) {
                    keepGoing = false;
                }
                
            }
            
            else if (lives != 0 && !(choice == questions.get(iteration).getCorrectAnswerIndex())) {
                System.out.println("Gng how could you get it wrong (sob emoji) \n I would side eye you but I can't through the screen (eye roll emoji)");
                System.out.println("Your streak is now " + streak + ", and you have " + lives + " lives left!");
                
                System.out.print("Would you like to keep playing? (Y/N): ");
                //LINE 67 WOWOWOW 6767676767 
                String theDecision = input.nextLine();
                
                if (theDecision.equals("N")) {
                    keepGoing = false;
                }
                
            }
            
            else {
                keepGoing = false;
            }
            
        }
        
        while (keepGoing);
    }
}
