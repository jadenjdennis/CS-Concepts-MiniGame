import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class Review {
    private static boolean cont;
    private static int choice;
    private static Scanner input = new Scanner(System.in);
    private static ArrayList<Question> questions = myProgram.questions;
    
    public static void setReview(ArrayList<Question> questions) {
            
        System.out.println("Thank you for visiting Review. (Either that or you died in RapidFire and admitted defeat.)");
        System.out.println("Which subtopic would you like to review? \n");
        System.out.println("[0] Creating Variables");
        System.out.println("[1] Snake Case");
        System.out.println("[2] Changing a Variable's Value");
        System.out.println("[3] Return Home\n");
        int subtopic = input.nextInt();
        
        
        if (subtopic == 0) {
            System.out.println("In Python, declaring a variable follows this simple structure: variable_name = variable value. A variable's value is whatever is stored in that variable, whether that's a string, number, float, boolean, list, etc. For example:");
            System.out.println("\nfriendly_greeting = \"Hi there!\"");
            System.out.println("brainrotted = True");
            System.out.println("num1 = 67\n");
            System.out.println("Remember, variables are declared using a single equals sign (=)! The double equals sign (==) is used in the conditional statements to compare values, as in \"if num_lives == 0.\"\n");
            
            askQuestion(0);
            lessonComplete(false);
            
            System.out.println("Python pays attention to indentation; you'll see why when you start defining functions and using loops. You need to make sure not to have spaces and tabs in weird places. The following would throw an error:\n");
            System.out.println("friendly_greeting = \"Hi there!\"");
            System.out.println("    brainrotted = True");
            System.out.println("num1 = 67");
            System.out.println("\nIn Python, assigning a value to a name creates the variable at the same time. Other languages, however, have different naming conventions and rules. For example, Java only pays attention to semicolons, not indentation, and you have to declare the type of the variable before setting it equal to a value. The following is one way you could declare the same variables as in the previous example in Java:\n");
            System.out.println("String friendlyGreeting = \"Hi there!\";");
            System.out.println("int num1 = 67;");
            System.out.println("boolean brainrotted;");
            System.out.println("brainrotted = true");
            System.out.println("\nDon't let this confuse you! Again, the previous code snippet is in Java, *not* Python. They are different programming languages. Hopefully, this shows you why Python is often considered one of the more intuitive languages to learn.");
            
            askQuestion(1);
            lessonComplete(false);
            
            System.out.println("One more thing: note that you can't make a variable with the exact same name as Python's 35-39 keywords. These words have special meanings in code and can confuse the computer if used where it doesn't expect them. In fact, you are not allowed to even start variable names with keywords that are supposed to begin a code structure, like \"if\", \"else\", \"while\", etc.");
            
            askQuestion(2);
            lessonComplete(true);
            
        } else if (subtopic == 1) {
            System.out.println("In Python, it is conventional when naming variables to use \"snake case,\" which replaces any spaces with underscores in a variable's name. For example, principal_name and biology_test_average_score correctly use snake case.");
            
            askQuestion(3);
            lessonComplete(false);
            
            System.out.println("Actually, Python only allows the uppercase letters (A-Z), lowercase letters (a-z), numbers, and underscores to be used in variable names. Variable names also cannot start with numbers. For example, Number_1 is a valid variable name, but 123password is not.");
            
            askQuestion(4);
            lessonComplete(false);
            
            System.out.println("Variable names are *case-sensitive*, meaning Python would consider the variables Age and age to be completely different.");
            
            askQuestion(5);
            lessonComplete(true);
            
        //6 777777
        } else if (subtopic == 2) {
            System.out.println("Sometimes, after making a decision, we have the sudden urge to change our choice. In programming, if we assign a value to a variable but need to re-assign a different value to that variable, it just looks like re-declaring it:\n");
            System.out.println("fav_Player = \"LeBron\"");
            System.out.println("fav_Player = \"Cunningham\"\n");
            
            askQuestion(6);
            lessonComplete(false);
            
            System.out.println("Sounds simple enough, right? But what if we have two variables but then want to swap their values? A common technique is to create a third variable as a placeholder so we can have somewhere to store one variable's value while we change them both. Here's an example:\n");
            System.out.println("val_1 = 6	# this hashtag indicates that a comment follows, meaning that the computer skips over this message and doesn't run it");
            System.out.println("val_2 = 7");
            System.out.println("val_3 = val_1	# now val_3 = 6");
            System.out.println("val_1 = val_2	# now val_1 = 7");
            System.out.println("val_2 = val_3	# now val_2 = 6");
            System.out.println("\nIf we had tried to do this swap using only two variables, we would have lost one value during the swap. (Actually, there is a way to do so, but you should learn what tuples are first.) If you don't believe us, try it yourself! Make sure to trace your code (write down ON PAPER the values of each variable and what they change to) so that you don't lose track of what's going on. Sounds crazy, right? Trust me, it's a must-know tip for programming as your programs get more complex.\n");
            
            askQuestion(7);
            lessonComplete(true);

        } else if (subtopic == 3) {
            System.out.println("\n");
            myProgram.main(null);
        } else {
            System.out.println("Please lock in and enter the number of the topic you're choosing.\n");
            setReview(questions);
        }
    }
    

    // this asks the question and provides feedback only once the student gets it right
    public static void askQuestion(int questionID) {
        String[] feedback = new String[8];
        feedback[0] = "Aur yass!! Remember that strings are always enclosed in either double quotes (\"\") or single quotes (\'\'), and that a single equals sign (=) is used to declare variables.";
        feedback[1] = "Correct! Remember that in Python, booleans are capitalized (True and False, not true and false as in Java). Also, variables have one type; in order for a string to be correctly stored, the third answer choice would need to be changed from \"The War of \" 1812 to \"The War of \" + str(1812).";
        feedback[2] = "Alley-oop! You recognized that \"while\" is a keyword that cannot stand alone as a variable name or even form the beginning of one, since it triggers a control structure. The computer gets confused when it reads \"while\" and some conditional like \"age < 30\" doesn't follow.";
        feedback[3] = "Nice! You've correctly identified that track_athlete heart_rate has a space in it, which is not allowed in snake case. The correct naming would be track_athlete_heart_rate. The rest are all valid; a variable name doesn't *have* to have an underscore, and it's legal (but unusual) to start with one.";
        feedback[4] = "All right! Remember, variable names can't start with numbers, but they can include them. $, /, and () are special characters that cannot be used in variable names.";
        feedback[5] = "Good job! Underscores count as characters, so the only duplicate variable in the list is ignite.";
        feedback[6] = "That's right! In Python, variables are dynamically typed; that means they can be re-assigned values of different types than their initial values; for instance, a variable that was originally a float could become a list and then a boolean. (That example is pretty convoluted, though.) This is used often to do calculations on numbers that the user inputs, since the input() function returns a string that needs to be converted into an int or float. As for the other answer choices, using mood2 creates an entirely different variable, and again, we only use the double equals sign (==) for conditionals, or to check whether two things are equal.";
        feedback[7] = "You got it! This technique is just a way to swap variable values and doesn't depend at all on what you name them.";
        
        cont = false;
        
        System.out.println(questions.get(questionID).getQuestion());
            for (int i = 0; i < 4; i++) {
                System.out.println(questions.get(questionID).getOptions()[i + 1]);
            }
            
            while (!cont) {
                System.out.print("Select an answer choice (0 - 3): ");
                int choice = input.nextInt();
                input.nextLine();
            
                if (choice == questions.get(questionID).getCorrectAnswerIndex()) {
                    System.out.println(feedback[questionID]);
                    System.out.println();
                    cont = true;
                } else {
                    System.out.println("Not quite. Try again! Make sure you're only typing the index (0, 1, 2, or 3) of the answer choice you're selecting.");
                }
            }
    }
    
    // if there is still more to cover, this lets the user decide whether they want to return to the Review Home
    public static void lessonComplete(boolean finished) {
        if (!finished) {
            System.out.print("Enter 4 to return to the Review Home, or press Enter to continue: ");
            String leaveOrContinue = input.nextLine();
            if (leaveOrContinue.equals("4")) {
                System.out.println("\n\n");
                setReview(questions);
            
            } else {
                System.out.println();
            }
        } else {
            System.out.print("That's it for this subtopic! Enter any character or press Enter to return to the Review Home: ");
            String goHome = input.nextLine();
            System.out.println("\n\n");
            setReview(questions);
        }
    }
    
}
