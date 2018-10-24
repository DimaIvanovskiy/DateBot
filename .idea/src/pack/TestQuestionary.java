package pack;

import static org.junit.Assert.*;

public class TestQuestionary {

    Questionary questionary = new Questionary();

    @org.junit.Test
    public void testAskQuestion()
    {
        String result = questionary.AskQuestion();
        assertEquals(questionary.questions[0], result);
    }

    @org.junit.Test
    public void testGetNumber()
    {
        assertTrue(questionary.getNumber() == 0);
    }

    @org.junit.Test
    public void testIsLastQuestion()
    {
        String result = "";
        for (int i = 0; i < questionary.questions.length; i++)
            result = questionary.AskQuestion();
        assertTrue(questionary.isLastQuestion() == true);
    }

    @org.junit.Test
    public void testIsSuitable()
    {
        Questionary coupleQuestionary = new Questionary();
        Sex userSex = Sex.get(1);
        Sex coupleSex = Sex.get(2);
        questionary.userSex = userSex;
        questionary.coupleSex = coupleSex;
        coupleQuestionary.userSex = coupleSex;
        coupleQuestionary.coupleSex = userSex;
        assertTrue(questionary.isSuitable(coupleQuestionary));
    }

    @org.junit.Test
    public void testIsNotSuitable()
    {
        Questionary notCoupleQuestionary = new Questionary();
        Sex userSex = Sex.get(1);
        Sex coupleSex = Sex.get(2);
        questionary.userSex = userSex;
        questionary.coupleSex = coupleSex;
        notCoupleQuestionary.userSex = userSex;
        notCoupleQuestionary.coupleSex = userSex;
        assertFalse(questionary.isSuitable(notCoupleQuestionary));
    }
}