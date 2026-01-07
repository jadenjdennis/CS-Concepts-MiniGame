import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

//For Jaden: ADD RETURN TO MYPROGRAM IN REVIEW

public class myProgram
{
    public static ArrayList<Question> questions = new ArrayList<>();
    public static boolean keepPlaying = true;
    
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        loadQuestionsFromCSV("questions.csv");
        
        while (keepPlaying) {
            System.out.println("Welcome! Which mode would you like to do? [1 - 4] \n"); 
            System.out.println("Option 1: Rapid Fire\n\t  Test your knowledge with tricky questions!");
            System.out.println("Option 2: Review\n\t  Review the basics and fix your misconceptions!");
            //System.out.println("Option 3: Application Style");
            System.out.println("Option 4: Return Home\n");
            int modeChoice = input.nextInt();
            if (modeChoice == 1) {
                RapidFire.setRapid();
            }
            
            else if (modeChoice == 2) {
                Review.setReview(questions);
            }
            
            else {
                System.out.println("THIS IS A TEST FOR NOW");
            }
            
            System.out.println("Would you like to play again? (Y/N) ");
            String theChoice = input.nextLine();
                
            if (theChoice.equals("N")) {
                keepPlaying = false;
            } 
                
        }
    }
    
     public static void loadQuestionsFromCSV(String filename) {
       try (Scanner scanner = new Scanner(new File(filename))){ 
            // Skip header line
            // Automatically assumes that the file has exactly 1 extraneous header line
            if (scanner.hasNextLine()) // if the file has another line beyond this one
                scanner.nextLine(); // read this line, don't assign it to anything, and move on

            while (scanner.hasNextLine()) {    //assumes that there is also an extraneous footer?
                String line = scanner.nextLine();
                //System.out.println(line);
                if (line.isEmpty()) continue;
                //loop thorough the line looking for the "," and 
                //store each part of the line in a variable
                //optional: you can use an array of a fixed length to store answer options
                String[] options = new String[7];
                options = line.split(",");
                //options has more than 4 things
                String question = options[0];
                String imagePath = "";
                String[] answerChoices = new String[4];
                for (int i = 0; i < 4; i++) {
                    
                    answerChoices[i] = options[i + 1];
                }
                
                //System.out.println(Integer.parseInt(options[5]));
                int correctAnswerIndex = Integer.parseInt(options[5]);
            
                   
                
            //System.out.println(Arrays.toString(answerChoices));    
            //System.out.println(answer);
                //if there is an image path, store it in a variable imagePath
                if (options.length > 6)
                    imagePath = options[6];
                
                
                //Create a variable of a Question class and store the values in it
                //optional - add the Question object to the ArrayList
                Question theQuestion = new Question(question, options, correctAnswerIndex, imagePath);
                
                //Adds the obj to the ArrayList on line 11
                questions.add(theQuestion);
            }
                
        } catch (FileNotFoundException e) {
           System.out.println("File not found: " + filename + " Error");
        }
         
     }
}
