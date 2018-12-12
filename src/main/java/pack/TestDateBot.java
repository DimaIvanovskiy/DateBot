package pack;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.MessageFormat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestDateBot {

    DateBot dateBot;
    Long chatId = 12345678L;

    @Before
    public void init() {
        dateBot = new DateBot();
        MockitoAnnotations.initMocks(this);
    }

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
                userSex);
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
        assertSame(dateBot.getBotAttributes().get(chatId1).getBotState(), BotState.CONNECTED);
        assertSame(dateBot.getBotAttributes().get(chatId2).getBotState(), BotState.CONNECTED);

        BotResult result = dateBot.processMessage(chatId1,"Hello1");
        assertEquals(result.getText(),"Hello1");
        assertTrue(result.getChatIds().contains(chatId2));

        result = dateBot.processMessage(chatId2,"Hello2");
        assertEquals(result.getText(),"Hello2");
        assertTrue(result.getChatIds().contains(chatId1));
    }

    @org.junit.Test
    public void testConnectionSpendsConnecterMoney()
    {
        DateBot dateBot = new DateBot();
        Long chatId1 = 12345678L;
        Long chatId2 = 23456789L;
        MethodsForTests.getResultOfConnect(dateBot, chatId1, chatId2, Sex.MALE, Sex.MALE,
                Sex.MALE, Sex.MALE);
        int expectedConnecterMoney = DateBot.startMoneyCount - DateBot.connectionCost;
        int currentConnecterMoney =  dateBot.getBotAttributes().get(chatId2).getMoney();
        int currentConnectedMoney = dateBot.getBotAttributes().get(chatId1).getMoney();
        assertEquals(expectedConnecterMoney, currentConnecterMoney);
        assertEquals(DateBot.startMoneyCount, currentConnectedMoney);
    }

    @org.junit.Test
    public void testUnableToConnectIfNotEnoughMoney()
    {
        DateBot dateBot = new DateBot();
        Long chatId1 = 12345678L;
        Long chatId2 = 23456789L;
        MethodsForTests.getResultOfConnect(dateBot, chatId1, chatId2, Sex.MALE, Sex.MALE,
                Sex.MALE, Sex.MALE);
        dateBot.processMessage(chatId2, "/disconnect");
        dateBot.processMessage(chatId1, "/able");
        int currentMoneyCount = dateBot.getBotAttributes().get(chatId2).getMoney();
        BotResult result = dateBot.processMessage(chatId2, "/connect");
        assertTrue(result.getText().startsWith(MessageFormat.format(DateBot.notEnoughMoney, currentMoneyCount,
                DateBot.connectionCost)));
    }

    @org.junit.Test
    public void testBotAsksQuestionAfterFindingSuitable()
    {
        Long chatId1 = 12345678L;
        Long chatId2 = 23456789L;
        MethodsForTests.StartDateBot(dateBot, chatId);
        MethodsForTests.setSexAndCouple(dateBot, chatId1, Sex.MALE, Sex.MALE);

        MethodsForTests.StartDateBot(dateBot, chatId2);
        MethodsForTests.setSexAndCouple(dateBot,  chatId2, Sex.MALE, Sex.MALE);

        dateBot.processMessage(chatId2, "/able");
        BotResult result = dateBot.processMessage(chatId1,"/connect");
        int pairMoneyCount = dateBot.getBotAttributes().get(chatId2).getMoney();
        String expected = MessageFormat.format("Would you like to talk to stranger with {0} coins?",
                pairMoneyCount);
        assertEquals(expected, result.getText());
    }

    @org.junit.Test
    public void testRefuseToConnectAfterFingingSuitable()
    {
        Long chatId1 = 12345678L;
        Long chatId2 = 23456789L;
        MethodsForTests.StartDateBot(dateBot, chatId);
        MethodsForTests.setSexAndCouple(dateBot, chatId1, Sex.MALE, Sex.MALE);

        MethodsForTests.StartDateBot(dateBot, chatId2);
        MethodsForTests.setSexAndCouple(dateBot,  chatId2, Sex.MALE, Sex.MALE);

        dateBot.processMessage(chatId2, "/able");
        dateBot.processMessage(chatId1,"/connect");

        BotResult result = dateBot.processMessage(chatId1,"2");

        assertEquals(BotState.NORMAL, dateBot.getBotAttributes().get(chatId1).getBotState());
        assertEquals(BotState.NORMAL, dateBot.getBotAttributes().get(chatId2).getBotState());
    }

    @org.junit.Test
    public void testNotConnectCyberBuddy()
    {
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        dateBot.processMessage(chatId, "/connect");
        assertEquals(BotState.ASKED_ABOUT_BOT, dateBot.getBotAttributes().get(chatId).
                getBotState());
        BotResult result = dateBot.processMessage(chatId, "2");
        assertEquals(DateBot.waitForSuitable,
                result.getText());
        assertEquals(BotState.NORMAL, dateBot.getBotAttributes().get(chatId).getBotState());
    }

    @org.junit.Test
    public void testConnectCyberBuddy()
    {
        MethodsForTests.setSexAndCouple(dateBot, chatId, Sex.MALE, Sex.FEMALE);
        dateBot.processMessage(chatId, "/connect");
        assertEquals(BotState.ASKED_ABOUT_BOT, dateBot.getBotAttributes().get(chatId).
                getBotState());
        BotResult result = dateBot.processMessage(chatId, "1");
        assertEquals(DateBot.nameQuestion, result.getText());
        assertEquals(BotState.ASKED_NAME, dateBot.getBotAttributes().get(chatId).getBotState());
        dateBot.processMessage(chatId, "Alice");
        assertEquals(BotState.TALKING_WITH_BOT, dateBot.getBotAttributes().get(chatId).getBotState());
    }


    @Mock
    CyberBuddy cyberBuddy;

    @InjectMocks
    DateBot mockedDateBot;

    @org.junit.Test
    public void testBuddyMakeResponse() throws Exception
    {
        MethodsForTests.setSexAndCouple(mockedDateBot, chatId, Sex.MALE, Sex.FEMALE);
        MethodsForTests.startConversationCyberBuddy(mockedDateBot, chatId);
        when(cyberBuddy.getMessage("Hi", chatId, Sex.MALE, Sex.FEMALE, "Alice")).
                thenReturn("Hello");
        assertEquals("Hello", mockedDateBot.processMessage(chatId, "Hi").getText());
    }

    @org.junit.Test()
    public void testBuddyThrowException() throws Exception
    {
        MethodsForTests.setSexAndCouple(mockedDateBot, chatId, Sex.MALE, Sex.FEMALE);
        MethodsForTests.startConversationCyberBuddy(mockedDateBot, chatId);
        when(cyberBuddy.getMessage("Hi", chatId, Sex.MALE, Sex.FEMALE, "Alice"))
                .thenThrow(new Exception());
        assertEquals(DateBot.botError,
                mockedDateBot.processMessage(chatId, "Hi").getText());
    }
}
