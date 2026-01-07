// Question class
public class Question {
    private String question;
    private String[] options;
    private int correctAnswerIndex;
    private String imagePath;

    public Question(String question, String[] options, int correctAnswerIndex, String imagePath) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.imagePath = imagePath;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public String getImagePath() {
        return imagePath;
    }
}
