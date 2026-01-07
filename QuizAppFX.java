import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class QuizAppFX extends Application {

    // UI-wide
    private Stage primaryStage;
    private BorderPane root;
    private VBox contentBox;

    // Rapid Fire state
    private int rapidLives;
    private int rapidStreak;
    private Question rapidCurrentQuestion;
    private Label rapidQuestionLabel;
    private ScrollPane rapidQuestionScroll; // NEW: scrollable question area
    private Label rapidStatusLabel;
    private Label rapidFeedbackLabel;
    private ToggleGroup rapidAnswerGroup;
    private ArrayList<ToggleButton> rapidAnswerButtons;
    private Button rapidSubmitButton;
    private Button rapidPlayAgainButton;
    private Button rapidExitButton;

    // Review state
    private int currentSubtopicIndex;
    private int currentStepIndex;
    private Label reviewTitleLabel;
    private TextArea reviewLessonArea;
    private Label reviewQuestionLabel;
    private ToggleGroup reviewAnswerGroup;
    private ArrayList<ToggleButton> reviewAnswerButtons;
    private Button reviewSubmitButton;
    private TextArea reviewFeedbackArea; // CHANGED: scrollable explanation area
    private Button reviewContinueButton;
    private Button reviewHomeButton;

    // Review lesson step data
    private static class LessonStep {
        String lessonText;
        int questionIndex;
        boolean lastStep;

        LessonStep(String lessonText, int questionIndex, boolean lastStep) {
            this.lessonText = lessonText;
            this.questionIndex = questionIndex;
            this.lastStep = lastStep;
        }
    }

    private LessonStep[][] reviewSteps;
    private String[] reviewFeedback;  // feedback array from Review.askQuestion

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Load questions using existing logic
        myProgram.loadQuestionsFromCSV("questions.csv");

        // Prepare review metadata
        initReviewData();

        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle(
                "-fx-background-color: #F5F5F7;" + // Apple-like light grey
                "-fx-font-family: 'SF Pro Text', 'Roboto', 'Segoe UI', sans-serif;"
        );

        Label appTitle = new Label("Python Practice Companion");
        appTitle.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #111111;"
        );
        BorderPane.setAlignment(appTitle, Pos.CENTER);
        root.setTop(appTitle);

        contentBox = new VBox(16);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.setFillWidth(true); // make children stretch with window
        root.setCenter(contentBox);

        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        Button homeButton = new Button("Main Menu");
        // red "danger" button so it stands out
        styleDangerButton(homeButton);
        homeButton.setOnAction(e -> showHome());
        bottomBar.getChildren().add(homeButton);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Python Quiz (JavaFX)");
        primaryStage.show();

        showHome();
    }

    // ============ HOME SCREEN ============

    private void showHome() {
        contentBox.getChildren().clear();

        Label welcome = new Label("Welcome! Which mode would you like to do?");
        welcome.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: 500;" +
                "-fx-text-fill: #111111;"
        );

        VBox cards = new VBox(12);
        cards.setAlignment(Pos.TOP_CENTER);
        cards.setFillWidth(true);

        VBox rapidCard = createModeCard(
                "Rapid Fire",
                "Test your knowledge with tricky questions!",
                this::showRapidFire
        );

        VBox reviewCard = createModeCard(
                "Review",
                "Review the basics and fix your misconceptions!",
                this::showReviewHome
        );

        VBox exitCard = createModeCard(
                "Exit",
                "Close the application.",
                () -> primaryStage.close()
        );

        cards.getChildren().addAll(rapidCard, reviewCard, exitCard);

        contentBox.getChildren().addAll(welcome, cards);
    }

    private VBox createModeCard(String title, String subtitle, Runnable onClick) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE); // responsive
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 24px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #111111;"
        );
        Label subLabel = new Label(subtitle);
        subLabel.setWrapText(true);
        subLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #555555;"
        );

        Button openBtn = new Button("Open");
        stylePrimaryButton(openBtn);
        openBtn.setOnAction(e -> onClick.run());

        HBox bottom = new HBox(openBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        card.getChildren().addAll(titleLabel, subLabel, bottom);

        card.setOnMouseClicked(e -> onClick.run());

        return card;
    }

    // ============ RAPID FIRE MODE ============

    private void showRapidFire() {
        contentBox.getChildren().clear();
        rapidLives = 3;
        rapidStreak = 0;

        Label title = new Label("Rapid Fire");
        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #111111;"
        );

        rapidStatusLabel = new Label();
        rapidStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        rapidQuestionLabel = new Label();
        rapidQuestionLabel.setWrapText(true);
        rapidQuestionLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #111111;"
        );

        // NEW: Wrap question label in a ScrollPane with vertical scrollbar
        rapidQuestionScroll = new ScrollPane(rapidQuestionLabel);
        rapidQuestionScroll.setFitToWidth(true);
        rapidQuestionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rapidQuestionScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rapidQuestionScroll.setPrefViewportHeight(140); // reasonable height for code snippets

        VBox questionCard = new VBox(12);
        questionCard.setPadding(new Insets(16));
        questionCard.setMaxWidth(Double.MAX_VALUE);
        questionCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 24px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);"
        );
        questionCard.getChildren().add(rapidQuestionScroll);

        rapidAnswerGroup = new ToggleGroup();
        rapidAnswerButtons = new ArrayList<>();

        VBox answersBox = new VBox(8);
        answersBox.setAlignment(Pos.CENTER_LEFT);
        answersBox.setFillWidth(true);

        for (int i = 0; i < 4; i++) {
            ToggleButton btn = new ToggleButton();
            btn.setToggleGroup(rapidAnswerGroup);
            btn.setMaxWidth(Double.MAX_VALUE);
            styleChoiceButton(btn);
            rapidAnswerButtons.add(btn);
            answersBox.getChildren().add(btn);
        }

        rapidFeedbackLabel = new Label();
        rapidFeedbackLabel.setWrapText(true);
        rapidFeedbackLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #444444;");

        rapidSubmitButton = new Button("Submit Answer");
        stylePrimaryButton(rapidSubmitButton);
        rapidSubmitButton.setOnAction(e -> handleRapidSubmit());

        rapidPlayAgainButton = new Button("Keep Playing");
        stylePrimaryButton(rapidPlayAgainButton);
        rapidPlayAgainButton.setOnAction(e -> nextRapidFireQuestion());
        rapidPlayAgainButton.setVisible(false);

        rapidExitButton = new Button("Return Home");
        styleDangerButton(rapidExitButton);
        rapidExitButton.setOnAction(e -> showHome());
        rapidExitButton.setVisible(false);

        HBox actionsBox = new HBox(10, rapidSubmitButton, rapidPlayAgainButton, rapidExitButton);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        contentBox.getChildren().addAll(title, rapidStatusLabel, questionCard, answersBox, rapidFeedbackLabel, actionsBox);

        nextRapidFireQuestion();
    }

    private void nextRapidFireQuestion() {
        if (rapidLives <= 0 || myProgram.questions.size() <= 8) {
            rapidQuestionLabel.setText("Game over! You have no lives left.");
            rapidStatusLabel.setText("Final streak: " + rapidStreak);
            rapidSubmitButton.setDisable(true);
            rapidPlayAgainButton.setVisible(false);
            rapidExitButton.setVisible(true);
            return;
        }

        rapidPlayAgainButton.setVisible(false);
        rapidExitButton.setVisible(false);
        rapidSubmitButton.setDisable(false);
        rapidFeedbackLabel.setText("");

        int minIndex = 8;
        int maxIndex = myProgram.questions.size() - 1;
        int iteration = minIndex + (int) (Math.random() * (maxIndex - minIndex + 1));
        rapidCurrentQuestion = myProgram.questions.get(iteration);

        rapidStatusLabel.setText("Lives: " + rapidLives + "   |   Streak: " + rapidStreak);

        String displayQuestion = formatQuestionForGUI(rapidCurrentQuestion.getQuestion());
        rapidQuestionLabel.setText(displayQuestion);

        String[] options = rapidCurrentQuestion.getOptions();
        for (int i = 0; i < 4; i++) {
            String text = options[i + 1];
            rapidAnswerButtons.get(i).setText(text);
            rapidAnswerButtons.get(i).setSelected(false);
        }
        rapidAnswerGroup.selectToggle(null);
    }

    private void handleRapidSubmit() {
        if (rapidCurrentQuestion == null) return;

        Toggle selectedToggle = rapidAnswerGroup.getSelectedToggle();
        if (selectedToggle == null) {
            rapidFeedbackLabel.setText("Please select an answer choice (0–3) before submitting.");
            return;
        }

        int selectedIndex = rapidAnswerButtons.indexOf(selectedToggle);
        int correctIndex = rapidCurrentQuestion.getCorrectAnswerIndex();

        if (selectedIndex == correctIndex) {
            rapidStreak++;
            rapidFeedbackLabel.setText(
                    "Congrats, you got the answer right!!!\n" +
                    "Your streak is now " + rapidStreak + ", and you have " + rapidLives + " lives left!\n" +
                    "Would you like to keep playing?"
            );
        } else {
            rapidLives--;
            rapidStreak = 0;
            if (rapidLives > 0) {
                rapidFeedbackLabel.setText(
                        "Gng how could you get it wrong (sob emoji)\nI would side eye you but I can't through the screen (eye roll emoji)\n" +
                        "Your streak is now " + rapidStreak + ", and you have " + rapidLives + " lives left!\n" +
                        "Would you like to keep playing?"
                );
            } else {
                rapidFeedbackLabel.setText(
                        "Gng how could you get it wrong (sob emoji)\n" +
                        "You have 0 lives left. Game over!"
                );
            }
        }

        rapidStatusLabel.setText("Lives: " + rapidLives + "   |   Streak: " + rapidStreak);
        rapidSubmitButton.setDisable(true);

        if (rapidLives > 0) {
            rapidPlayAgainButton.setVisible(true);
        }
        rapidExitButton.setVisible(true);
    }

    // ============ REVIEW MODE ============

    private void showReviewHome() {
        contentBox.getChildren().clear();

        Label title = new Label("Review");
        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #111111;"
        );

        Label subtitle = new Label("Which subtopic would you like to review?");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        VBox options = new VBox(10);
        options.setAlignment(Pos.TOP_LEFT);
        options.setFillWidth(true);

        Button b0 = new Button("[0] Creating Variables");
        Button b1 = new Button("[1] Snake Case");
        Button b2 = new Button("[2] Changing a Variable's Value");
        Button b3 = new Button("[3] Return Home");

        stylePrimaryButton(b0);
        stylePrimaryButton(b1);
        stylePrimaryButton(b2);
        styleDangerButton(b3);

        b0.setOnAction(e -> startReviewSubtopic(0));
        b1.setOnAction(e -> startReviewSubtopic(1));
        b2.setOnAction(e -> startReviewSubtopic(2));
        b3.setOnAction(e -> showHome());

        options.getChildren().addAll(b0, b1, b2, b3);

        contentBox.getChildren().addAll(title, subtitle, options);
    }

    private void startReviewSubtopic(int subtopicIndex) {
        currentSubtopicIndex = subtopicIndex;
        currentStepIndex = 0;

        contentBox.getChildren().clear();

        String titleText;
        if (subtopicIndex == 0) titleText = "Creating Variables";
        else if (subtopicIndex == 1) titleText = "Snake Case";
        else titleText = "Changing a Variable's Value";

        reviewTitleLabel = new Label(titleText);
        reviewTitleLabel.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #111111;"
        );

        reviewLessonArea = new TextArea();
        reviewLessonArea.setEditable(false);
        reviewLessonArea.setWrapText(true);
        // BIGGER lesson area
        reviewLessonArea.setPrefRowCount(16);
        reviewLessonArea.setPrefHeight(280);
        reviewLessonArea.setMaxWidth(Double.MAX_VALUE);
        reviewLessonArea.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #222222;" +
                "-fx-control-inner-background: #FFFFFF;" +
                "-fx-background-radius: 16px;" +
                "-fx-border-radius: 16px;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-width: 1px;"
        );

        reviewQuestionLabel = new Label();
        reviewQuestionLabel.setWrapText(true);
        reviewQuestionLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #111111;");

        reviewAnswerGroup = new ToggleGroup();
        reviewAnswerButtons = new ArrayList<>();
        VBox answersBox = new VBox(8);
        answersBox.setAlignment(Pos.CENTER_LEFT);
        answersBox.setFillWidth(true);

        for (int i = 0; i < 4; i++) {
            ToggleButton btn = new ToggleButton();
            btn.setToggleGroup(reviewAnswerGroup);
            btn.setMaxWidth(Double.MAX_VALUE);
            styleChoiceButton(btn);
            reviewAnswerButtons.add(btn);
            answersBox.getChildren().add(btn);
        }

        reviewSubmitButton = new Button("Submit Answer");
        stylePrimaryButton(reviewSubmitButton);
        reviewSubmitButton.setOnAction(e -> handleReviewSubmit());

        // NEW: scrollable explanation/feedback area
        reviewFeedbackArea = new TextArea();
        reviewFeedbackArea.setEditable(false);
        reviewFeedbackArea.setWrapText(true);
        reviewFeedbackArea.setPrefRowCount(4);
        reviewFeedbackArea.setMaxWidth(Double.MAX_VALUE);
        reviewFeedbackArea.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #444444;" +
                "-fx-control-inner-background: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-radius: 12px;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-width: 1px;"
        );

        reviewContinueButton = new Button("Continue");
        stylePrimaryButton(reviewContinueButton);
        reviewContinueButton.setOnAction(e -> goToNextReviewStep());

        reviewHomeButton = new Button("Return to Review Home");
        styleDangerButton(reviewHomeButton);
        reviewHomeButton.setOnAction(e -> showReviewHome());

        HBox bottomButtons = new HBox(10, reviewSubmitButton, reviewContinueButton, reviewHomeButton);
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);

        contentBox.getChildren().addAll(
                reviewTitleLabel,
                reviewLessonArea,
                reviewQuestionLabel,
                answersBox,
                reviewFeedbackArea,
                bottomButtons
        );

        loadCurrentReviewStep();
    }

    private void loadCurrentReviewStep() {
        LessonStep step = reviewSteps[currentSubtopicIndex][currentStepIndex];

        reviewLessonArea.setText(step.lessonText);

        Question q = myProgram.questions.get(step.questionIndex);
        String displayQ = formatQuestionForGUI(q.getQuestion());
        reviewQuestionLabel.setText(displayQ);

        String[] options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            reviewAnswerButtons.get(i).setText(options[i + 1]);
            reviewAnswerButtons.get(i).setSelected(false);
        }
        reviewAnswerGroup.selectToggle(null);

        reviewFeedbackArea.setText("");
        reviewSubmitButton.setDisable(false);
        reviewContinueButton.setDisable(true);
    }

    private void handleReviewSubmit() {
        LessonStep step = reviewSteps[currentSubtopicIndex][currentStepIndex];
        int questionIndex = step.questionIndex;
        Question q = myProgram.questions.get(questionIndex);

        Toggle selectedToggle = reviewAnswerGroup.getSelectedToggle();
        if (selectedToggle == null) {
            reviewFeedbackArea.setText("Please select an answer choice (0–3) before submitting.");
            return;
        }

        int selectedIndex = reviewAnswerButtons.indexOf(selectedToggle);
        int correctIndex = q.getCorrectAnswerIndex();

        if (selectedIndex == correctIndex) {
            // Use same feedback text as console version
            reviewFeedbackArea.setText(reviewFeedback[questionIndex]);
            reviewSubmitButton.setDisable(true);
            if (step.lastStep) {
                reviewContinueButton.setText("Done – Back to Review Home");
            } else {
                reviewContinueButton.setText("Continue");
            }
            reviewContinueButton.setDisable(false);
        } else {
            reviewFeedbackArea.setText(
                    "Not quite. Try again! Make sure you're only typing the index (0, 1, 2, or 3) of the answer choice you're selecting."
            );
        }
    }

    private void goToNextReviewStep() {
        LessonStep step = reviewSteps[currentSubtopicIndex][currentStepIndex];
        if (step.lastStep) {
            showReviewHome();
        } else {
            currentStepIndex++;
            loadCurrentReviewStep();
        }
    }

    // ============ REVIEW DATA INIT (MIRRORS Review.java) ============

    private void initReviewData() {
        reviewFeedback = new String[8];
        reviewFeedback[0] = "Aur yass!! Remember that strings are always enclosed in either double quotes (\"\") or single quotes (''), and that a single equals sign (=) is used to declare variables.";
        reviewFeedback[1] = "Correct! Remember that in Python, booleans are capitalized (True and False, not true and false as in Java). Also, variables have one type; in order for a string to be correctly stored, the third answer choice would need to be changed from \"The War of \" 1812 to \"The War of \" + str(1812).";
        reviewFeedback[2] = "Alley-oop! You recognized that \"while\" is a keyword that cannot stand alone as a variable name or even form the beginning one, since it triggers a control structure. The computer gets confused when it reads \"while\" and some conditional like \"age < 30\" doesn't follow.";
        reviewFeedback[3] = "Nice! You've correctly identified that track_athlete heart_rate has a space in it, which is not allowed in snake case. The correct naming would be track_athlete_heart_rate. The rest are all valid; a variable name doesn't *have* to have an underscore, and it's legal (but unusual) to start with one.";
        reviewFeedback[4] = "All right! Remember, variable names can't start with numbers, but they can include them. $, /, and () are special characters that cannot be used in variable names.";
        reviewFeedback[5] = "Good job! Underscores count as characters, so the only duplicate variable in the list is ignite.";
        reviewFeedback[6] = "That's right! In Python, variables are dynamically typed; that means they can be re-assigned values of different types than their initial values; for instance, a variable that was originally a float could become a list and then a boolean. (That example is pretty convoluted, though.) This is used often to do calculations on numbers that the user inputs, since the input() function returns a string that needs to be converted into an int or float. As for the other answer choices, using mood2 creates an entirely different variable, and again, we only use the double equals sign (==) for conditionals, or to check whether two things are equal.";
        reviewFeedback[7] = "You got it! This technique is just a way to swap variable values and doesn't depend at all on what you name them.";

        // Subtopic 0: Creating Variables (0,1,2)
        String sub0_step0 =
                "In Python, declaring a variable follows this simple structure: variable_name = variable value. " +
                "A variable's value is whatever is stored in that variable, whether that's a string, number, float, boolean, list, etc. For example:\n\n" +
                "friendly_greeting = \"Hi there!\"\n" +
                "brainrotted = True\n" +
                "num1 = 67\n\n" +
                "Remember, variables are declared using a single equals sign (=)! The double equals sign (==) is used in " +
                "the conditional statements to compare values, as in \"if num_lives == 0.\"";

        String sub0_step1 =
                "Python pays attention to indentation; you'll see why when you start defining functions and using loops. " +
                "You need to make sure not to have spaces and tabs in weird places. The following would throw an error:\n\n" +
                "friendly_greeting = \"Hi there!\"\n" +
                "       brainrotted = True\n" +
                "num1 = 67\n\n" +
                "In Python, assigning a value to a name creates the variable at the same time. Other languages, however, " +
                "have different naming conventions and rules. For example, Java only pays attention to semicolons, not indentation, " +
                "and you have to declare the type of the variable before setting it equal to a value. The following is one way " +
                "you could declare the same variables as in the previous example in Java:\n\n" +
                "String friendlyGreeting = \"Hi there!\";\n" +
                "int num1 = 67;\n" +
                "boolean brainrotted;\n" +
                "brainrotted = true\n\n" +
                "Don't let this confuse you! Again, the previous code snippet is in Java, *not* Python. They are different " +
                "programming languages. Hopefully, this shows you why Python is often considered one of the more intuitive languages to learn.";

        String sub0_step2 =
                "One more thing: note that you can't make a variable with the exact same name as Python's 35-39 keywords. " +
                "These words have special meanings in code and can confuse the computer if used where it doesn't expect them. " +
                "In fact, you are not allowed to even start variable names with keywords that are supposed to begin a code structure, " +
                "like \"if\", \"else\", \"while\", etc.";

        LessonStep s0_0 = new LessonStep(sub0_step0, 0, false);
        LessonStep s0_1 = new LessonStep(sub0_step1, 1, false);
        LessonStep s0_2 = new LessonStep(sub0_step2, 2, true);

        // Subtopic 1: Snake Case (3,4,5)
        String sub1_step0 =
                "In Python, it is conventional when naming variables to use \"snake case,\" which replaces any spaces " +
                "with underscores in a variable's name. For example, principal_name and biology_test_average_score correctly use snake case.";

        String sub1_step1 =
                "Actually, Python only allows the uppercase letters (A-Z), lowercase letters (a-z), numbers, and underscores " +
                "to be used in variable names. Variable names also cannot start with numbers. For example, Number_1 is a valid variable name, " +
                "but 123password is not.";

        String sub1_step2 =
                "Variable names are *case-sensitive*, meaning Python would consider the variables Age and age to be completely different.";

        LessonStep s1_0 = new LessonStep(sub1_step0, 3, false);
        LessonStep s1_1 = new LessonStep(sub1_step1, 4, false);
        LessonStep s1_2 = new LessonStep(sub1_step2, 5, true);

        // Subtopic 2: Changing a Variable's Value (6,7)
        String sub2_step0 =
                "Sometimes, after making a decision, we have the sudden urge to change our choice. In programming, if we assign a value to a variable " +
                "but need to re-assign a different value to that variable, it just looks like re-declaring it:\n\n" +
                "fav_Player = \"LeBron\"\n" +
                "fav_Player = \"Cunningham\"";

        String sub2_step1 =
                "Sounds simple enough, right? But what if we have two variables but then want to swap their values? " +
                "A common technique is to create a third variable as a placeholder so we can have somewhere to store one variable's value " +
                "while we change them both. Here's an example:\n\n" +
                "val_1 = 6\t# this hashtag indicates that a comment follows, meaning that the computer skips over this message and doesn't run it\n" +
                "val_2 = 7\n" +
                "val_3 = val_1\t# now val_3 = 6\n" +
                "val_1 = val_2\t# now val_1 = 7\n" +
                "val_2 = val_3\t# now val_2 = 6\n\n" +
                "If we had tried to do this swap using only two variables, we would have lost one value during the swap. (Actually, there is a way to do so, " +
                "but you should learn what tuples are first.) If you don't believe us, try it yourself! Make sure to trace your code (write down ON PAPER " +
                "the values of each variable and what they change to) so that you don't lose track of what's going on. Sounds crazy, right? " +
                "Trust me, it's a must-know tip for programming as your programs get more complex.";

        LessonStep s2_0 = new LessonStep(sub2_step0, 6, false);
        LessonStep s2_1 = new LessonStep(sub2_step1, 7, true);

        reviewSteps = new LessonStep[3][];
        reviewSteps[0] = new LessonStep[]{s0_0, s0_1, s0_2};
        reviewSteps[1] = new LessonStep[]{s1_0, s1_1, s1_2};
        reviewSteps[2] = new LessonStep[]{s2_0, s2_1};
    }

    // ============ UTILS ============

    /**
     * Replace $%^& in question text with a newline for GUI display.
     */
    private String formatQuestionForGUI(String rawQuestion) {
        if (rawQuestion == null) return "";
        return rawQuestion.replace("$%^&", "\n");
    }

    private void stylePrimaryButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #007AFF, #1A73E8);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 9999;" +
                "-fx-padding: 8 20 8 20;" +
                "-fx-border-color: transparent;"
        );
    }

    private void styleDangerButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #FF3B30, #EA4335);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 9999;" +
                "-fx-padding: 8 20 8 20;" +
                "-fx-border-color: transparent;"
        );
    }

    private void styleChoiceButton(ToggleButton btn) {
        btn.setWrapText(true);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #111111;" +
                "-fx-background-radius: 9999;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-border-radius: 9999;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 14px;"
        );

        btn.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                btn.setStyle(
                        "-fx-background-color: #E8F0FE;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-background-radius: 9999;" +
                        "-fx-padding: 10 16 10 16;" +
                        "-fx-border-radius: 9999;" +
                        "-fx-border-color: #1A73E8;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-font-size: 14px;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                        "-fx-text-fill: #111111;" +
                        "-fx-background-radius: 9999;" +
                        "-fx-padding: 10 16 10 16;" +
                        "-fx-border-radius: 9999;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-size: 14px;"
                );
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
