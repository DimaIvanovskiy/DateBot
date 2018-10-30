package pack;

public class QuestionAndAnswers
{
    public final String question;

    public final String[] answers;

    public QuestionAndAnswers(String question, String...answers)
    {
        this.question = question;
        this.answers = answers;
    }

}
