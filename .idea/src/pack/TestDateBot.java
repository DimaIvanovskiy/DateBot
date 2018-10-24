package pack;

import static org.junit.Assert.*;

public class TestDateBot {

    DateBot datebot = new DateBot();
    Long chatId = new Long("12345678");
    MethodsForTests methods = new MethodsForTests();

    @org.junit.Test
    public void testStart()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        assertEquals(datebot.greetingInformation, result.getText().substring(0,
                datebot.greetingInformation.length()));
    }

    @org.junit.Test
    public void testHelp()
    {
        BotResult result = datebot.processMessage(chatId, "/help");
        assertEquals(datebot.commandsInformation, result.getText());
    }

    @org.junit.Test
    public void testSetFemaleSex()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = datebot.processMessage(chatId, "2");
        assertTrue(datebot.getQuestionaries().get(chatId).userSex == Sex.FEMALE);
    }

    @org.junit.Test
    public void testSetNotSex()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = datebot.processMessage(chatId, "not sex");
        assertEquals("There is no such answer", result.getText());
    }

    @org.junit.Test
    public void testSetCoupleSex()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = methods.setSexAndCouple(datebot, chatId, "1", "2");
        assertTrue(datebot.getQuestionaries().get(chatId).coupleSex == Sex.FEMALE);
    }

    @org.junit.Test
    public void testSetNonexistentCoupleSex()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = methods.setSexAndCouple(datebot, chatId, "1", "Nonexistent couple sex");
        assertEquals("There is no such answer", result.getText());
    }

    @org.junit.Test
    public void testAble()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = methods.setSexAndCouple(datebot, chatId, "1", "2");
        result = datebot.processMessage(chatId, "/able");
        assertEquals("Now some strangers can connect to you", result.getText());
    }

    @org.junit.Test
    public void testDisable()
    {
        BotResult result = datebot.processMessage(chatId, "/start");
        result = methods.setSexAndCouple(datebot, chatId, "1", "2");
        result = datebot.processMessage(chatId, "/able");
        result = datebot.processMessage(chatId, "/disable");
        assertEquals("Now no one can write you", result.getText());
    }


    @org.junit.Test
    public void testNotSuitableIds()
    {
        DateBot suitableBot = new DateBot();
        Long suitableId = new Long("1234557");
        BotResult resultForNotSuitable = methods.getResultOfConnect(datebot, chatId, suitableBot, suitableId,
                "1", "2", "2", "2");
        assertEquals("Sorry, but for now there are no suitable people in our base.",
                resultForNotSuitable.getText());
    }

    @org.junit.Test
    public void testChange()
    {
        BotResult result = methods.getStartMessage(datebot, chatId);
        result = methods.setSexAndCouple(datebot, chatId, "1", "2");
        result = datebot.processMessage(chatId, "/change");
        result = datebot.processMessage(chatId, "2");
        assertTrue(datebot.getQuestionaries().get(chatId).userSex == Sex.FEMALE);
    }
}
