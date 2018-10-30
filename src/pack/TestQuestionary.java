package pack;

import static org.junit.Assert.*;

public class TestQuestionary
{

    Questionary questionary = new Questionary();

    @org.junit.Test
    public void testAskQuestion()
    {
        QuestionAndAnswers result = questionary.AskQuestion();
        assertEquals(questionary.questions[0], result);
    }

    @org.junit.Test
    public void testAskAllQuestion()
    {
        for(var i=0;i<questionary.questions.length; i++)
        {
            assertEquals(questionary.questions[i], questionary.AskQuestion());
        }
    }

    @org.junit.Test
    public void testGetNumber()
    {
        assertEquals(0, questionary.getNumber());
    }

    @org.junit.Test
    public void testIsLastQuestion()
    {
        for (int i = 0; i < questionary.questions.length; i++)
            questionary.AskQuestion();
        assertTrue(questionary.isLastQuestion());
    }

    @org.junit.Test
    public void testAskQuestionReturnsNull()
    {
        QuestionAndAnswers result = questionary.AskQuestion();
        for (int i = 0; i < questionary.questions.length ; i++)
            result = questionary.AskQuestion();
        assertNull(result);
    }

    @org.junit.Test
    public void testIsSuitableOnHeterosexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.MALE, Sex.FEMALE,
                Sex.FEMALE, Sex.MALE);
        assertTrue(result);
    }

    @org.junit.Test
    public void testIsSuitableOnHomosexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.MALE, Sex.MALE,
                Sex.MALE, Sex.MALE);
        assertTrue(result);
    }

    @org.junit.Test
    public void testIsSuitableOnBisexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.FEMALE, Sex.MALE_OR_FEMALE,
                Sex.FEMALE, Sex.FEMALE);
        assertTrue(result);
    }

    @org.junit.Test
    public void testIsNotSuitableOnHeterosexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.FEMALE, Sex.MALE,
                Sex.FEMALE, Sex.MALE);
        assertFalse(result);
    }

    @org.junit.Test
    public void testIsNotSuitableOnHomosexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.FEMALE, Sex.FEMALE,
                Sex.MALE, Sex.MALE);
        assertFalse(result);
    }

    @org.junit.Test
    public void testIsNotSuitableOnBisexualCouple()
    {
        boolean result = MethodsForTests.questionariesAreSuitable(Sex.FEMALE, Sex.MALE_OR_FEMALE,
                Sex.MALE, Sex.MALE);
        assertFalse(result);
    }
}