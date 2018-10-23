package pack;

import java.lang.reflect.Array;

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

    public String AskQuestion()
    {
        String result = questions[questionNumber];
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

    private final String[] questions =
            {
                    "What is your biological sex?\n" +
                            "1)Male\n" +
                            "2)Female",

                    "What is the sex of your dream couple?\n" +
                            "1)Only male\n" +
                            "2)Only female\n" +
                            "3)Male or female"
            };
}
