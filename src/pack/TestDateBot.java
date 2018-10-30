package pack;

import static org.junit.Assert.*;

public class TestDateBot {

    DateBot dateBot = new DateBot();
    Long chatId = 12345678L;

    @org.junit.Test
    public void testStart()
    {
        BotResult result = MethodsForTests.StartDateBot(dateBot, chatId);
        assertEquals(DateBot.greetingInformation, result.getText().substring(0,
                DateBot.greetingInformation.length()));
    }

    @org.junit.Test
    public void testFillQuestionaryAnswer()
    {
        BotResult result = MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        assertEquals(DateBot.finishingQuestionaryReply, result.getText());
    }

    @org.junit.Test
    public void testQuestionarySetSex()
    {
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        Questionary questionary = dateBot.getBotAttributes().get(chatId).getQuestionary();
        assertEquals(Sex.MALE, questionary.userSex);
        assertEquals(Sex.FEMALE, questionary.coupleSex);
    }

    @org.junit.Test
    public void testHelp()
    {
        MethodsForTests.setSexAndCouple(dateBot,chatId,Sex.MALE, Sex.FEMALE);
        BotResult result = dateBot.processMessage(chatId, "/help");
        assertEquals(DateBot.commandsInformation, result.getText());
    }


    @org.junit.Test
    public void testSetNotSex()
    {
        MethodsForTests.StartDateBot(dateBot, chatId);
        BotResult result = dateBot.processMessage(chatId, "not sex");
        assertEquals(DateBot.wrongAnswerReply, result.getText());
    }

    @org.junit.Test
    public void testSetCoupleSex()
    {
        MethodsForTests.StartDateBot(dateBot, chatId);
       MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        assertEquals(Sex.FEMALE,dateBot.getBotAttributes().get(chatId).getQuestionary().coupleSex);
    }


    @org.junit.Test
    public void testAble()
    {
        MethodsForTests.StartDateBot(dateBot, chatId);
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        BotResult result = dateBot.processMessage(chatId, "/able");
        assertEquals(DateBot.ableReply, result.getText());
    }


    @org.junit.Test
    public void testDisable()
    {
        dateBot.processMessage(chatId, "/start");
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        dateBot.processMessage(chatId, "/able");
        BotResult result = dateBot.processMessage(chatId, "/disable");
        assertEquals(DateBot.disableReply, result.getText());
    }


    @org.junit.Test
    public void testNotSuitableIds()
    {
        Long notSuitableId = 1234557L;
       MethodsForTests.getResultOfConnect(dateBot, chatId, notSuitableId,
                Sex.MALE, Sex.FEMALE, Sex.FEMALE, Sex.FEMALE);
        assertNotEquals(notSuitableId,dateBot.getBotAttributes().get(chatId).getConnection());
        assertNotEquals(chatId, dateBot.getBotAttributes().get(notSuitableId).getConnection());
    }

    @org.junit.Test
    public void testChange()
    {
        MethodsForTests.StartDateBot(dateBot, chatId);
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        dateBot.processMessage(chatId, "/change");
        dateBot.processMessage(chatId, "2");
        assertEquals(Sex.FEMALE, dateBot.getBotAttributes().get(chatId).getQuestionary().
                userSex );
    }

    @org.junit.Test
    public void testConnect()
    {
        DateBot dateBot = new DateBot();
        Long chatId1 = 12345678L;
        Long chatId2 = 23456789L;
        MethodsForTests.getResultOfConnect(dateBot, chatId1, chatId2, Sex.MALE, Sex.MALE,
                Sex.MALE, Sex.MALE);
        assertTrue(dateBot.getBotAttributes().containsKey(chatId1));
        assertTrue(dateBot.getBotAttributes().containsKey(chatId2));
        assertSame(dateBot.getBotAttributes().get(chatId1).getState(), State.CONNECTED);
        assertSame(dateBot.getBotAttributes().get(chatId2).getState(), State.CONNECTED);

        BotResult result = dateBot.processMessage(chatId1,"Hello1");
        assertEquals(result.getText(),"Hello1");
        assertTrue(result.getChatIds().contains(chatId2));

        result = dateBot.processMessage(chatId2,"Hello2");
        assertEquals(result.getText(),"Hello2");
        assertTrue(result.getChatIds().contains(chatId1));
    }
}
