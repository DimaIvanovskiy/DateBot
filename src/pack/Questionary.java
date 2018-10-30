package pack;

public class Questionary
{
    public Sex userSex;

    public Sex coupleSex;

    private int questionNumber = 0;

    public int getNumber()
    {
        return questionNumber;
    }

    private boolean isLastQuestion = false;

    public boolean isLastQuestion()
    {
        return isLastQuestion;
    }

    public QuestionAndAnswers AskQuestion()
    {
        if (questionNumber == questions.length)
            return null;
        QuestionAndAnswers result = questions[questionNumber];
        questionNumber++;
        if (questionNumber == questions.length)
            isLastQuestion = true;
        return result;
    }

    public boolean isSuitable(Questionary questionary)
    {
        Sex coupleSex2 = questionary.coupleSex;
        Sex userSex2 = questionary.userSex;
        return (userSex == coupleSex2 || coupleSex2 == Sex.MALE_OR_FEMALE) &&
                (userSex2 == coupleSex || coupleSex == Sex.MALE_OR_FEMALE);
    }

    final QuestionAndAnswers[] questions =
            {       new QuestionAndAnswers("What is your biological sex?", "Male", "Female"),
                    new QuestionAndAnswers("What is the sex of your dream couple?",
                            "Only male", "Only female", "Male or female")
            };
}
