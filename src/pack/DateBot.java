package pack;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class DateBot
{

    private ConcurrentHashMap<Long, BotAttributes> botAttributes = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, BotAttributes> getBotAttributes()
    {
        return botAttributes;
    }

    private CyberBuddy cyberBuddy = new CyberBuddy();
    private MoneySubBot moneyBot = new MoneySubBot(botAttributes);

    private Set<Long> abledUsers = Sets.newConcurrentHashSet();


    private BotResult processCommands(Long chatId, String text)
    {
        BotResult result = new BotResult("", chatId);
        BotAttributes attributes = botAttributes.get(chatId);
        synchronized (attributes.lock)
        {
            BotState botState = attributes.getBotState();
            switch (text) {

                case "/start":
                    if (botState == BotState.STARTED)
                    {
                        result.addText(greetingInformation);
                        attributes.setBotState(BotState.MAKING_QUESTIONARY);
                        result.addQuestionAndAnswers(attributes.getQuestionary().AskQuestion());
                    }
                    break;
                case "/help":
                    result.addText(commandsInformation);
                    break;
                case "/able":
                    result = enableConnection(chatId, botState);
                    break;
                case "/disable":
                    result = disableConnection(chatId);
                    break;
                case "/money":
                    result.addText(MoneySubBot.showMoney(attributes));
                    break;
                case "/play":
                    attributes.setBotState(BotState.PLAYING);
                    moneyBot.startSession(result, chatId);
                    break;
                case "/connect":
                    Long suitableId = ConnectionManager.tryConnect(chatId, botAttributes, abledUsers);
                    if (suitableId == null)
                    {
                        result.addText(DateBot.noSuitableQuestionaryReply);
                        result.addQuestionAndAnswers(new QuestionAndAnswers("Would you like to talk to " +
                                "our conversation bot?", "yes", "no"));
                        attributes.setBotState(BotState.ASKED_ABOUT_BOT);
                    }
                    else
                    {
                        result.addChatId(suitableId);
                        result.addText(DateBot.connectionReply);
                    }

                    break;
                case "/disconnect":
                    if (botState == BotState.TALKING_WITH_BOT)
                    {
                        result.addText(DateBot.botDisconnectionReply);
                        attributes.setBotState(BotState.NORMAL);
                        return result;
                    }
                    if (botState == BotState.CONNECTED)
                    {
                        Long connection = ConnectionManager.disconnect(chatId, botAttributes);
                        result.addChatId(connection);
                        result.addText(DateBot.disconnectionReply);
                    }
                    break;
                case "/change":
                    if (botState == BotState.CONNECTED)
                        return result;
                    attributes.setBotState(BotState.MAKING_QUESTIONARY);
                    attributes.setQuestionary(new Questionary());
                    result.addQuestionAndAnswers(attributes.getQuestionary().AskQuestion());
                    break;
                default:
                    return null;
            }
            return result;
        }
    }


    BotResult processMessage(Long chatId, String text)
    {
        if (!botAttributes.containsKey(chatId))
            botAttributes.putIfAbsent(chatId, new BotAttributes(BotState.STARTED,
                    new Questionary(), chatId));
        BotResult result = new BotResult("", chatId);
        if (text == null && botAttributes.get(chatId).getBotState() == BotState.CONNECTED)
        {
            result.addChatId(botAttributes.get(chatId).getConnection());
        }
        else
        {
            switch (botAttributes.get(chatId).getBotState()) 
            {
                case MAKING_QUESTIONARY:
                    makeQuestionary(result, chatId, text);
                    break;
                case NORMAL:
                    result = processCommands(chatId, text);
                    break;
                case PLAYING:
                    var is_finished = moneyBot.processMessage(result, chatId, text);
                    if (is_finished)
                        botAttributes.get(chatId).setBotState(BotState.NORMAL);
                    break;
                case CONNECTED:
                    result = processCommands(chatId, text);
                    if (result == null)
                    {
                        result = new BotResult();
                        result.addChatId(botAttributes.get(chatId).getConnection());
                        result.addText(text);
                    }
                    break;
                case STARTED:
                    result = processCommands(chatId, text);
                    break;
                case TALKING_WITH_BOT:
                    result = processCommands(chatId, text);
                    if (result == null)
                    {
                        result = new BotResult("", chatId);
                        Questionary questionary =  botAttributes.get(chatId).getQuestionary();
                        try
                        {
                            result.addText(cyberBuddy.getMessage(text, chatId, questionary.userSex,
                                    questionary.coupleSex, botAttributes.get(chatId).getUserName()));
                        }
                        catch (Exception e)
                        {
                            result.addText(botError);
                        }
                    }
                    break;
                case ASKED_ABOUT_BOT:
                    switch(text)
                    {
                        case "1":
                            result.addText(nameQuestion);
                            botAttributes.get(chatId).setBotState(BotState.ASKED_NAME);
                            break;
                        case "2":
                            result.addText(waitForSuitable);
                            botAttributes.get(chatId).setBotState(BotState.NORMAL);
                            break;
                    }
                    break;
                case ASKED_NAME:
                    botAttributes.get(chatId).setUserName(text);
                    result.addText(botConnectionReply);
                    botAttributes.get(chatId).setBotState(BotState.TALKING_WITH_BOT);
                    break;
            }

        }
        addButtons(result, botAttributes.get(chatId).getBotState());
        return result;
    }

    BotResult enableConnection(Long chatId, BotState botState)
    {
        BotResult result = new BotResult("", chatId);
        if (botState == BotState.CONNECTED || abledUsers.contains(chatId))
            return result;
        abledUsers.add(chatId);
        result.addText(DateBot.ableReply);
        return result;
    }

    BotResult disableConnection(Long chatId)
    {
        BotResult result = new BotResult("", chatId);
        if (!abledUsers.contains(chatId))
            return result;
        abledUsers.remove(chatId);
        result.addText(DateBot.disableReply);
        return result;
    }

    private void addButtons(BotResult result, BotState botState)
    {
        switch (botState)
        {
            case CONNECTED:
                result.addCurrentCommands(connectionCommands);
            break;
            case TALKING_WITH_BOT:
                result.addCurrentCommands(connectionCommands);
                break;
            case NORMAL:
                result.addCurrentCommands(normalCommands);
            break;
        }
    }

    private void makeQuestionary(BotResult result, Long chatId, String text)
    {
        Questionary questionary = botAttributes.get(chatId).getQuestionary();
        int questionNumber = questionary.getNumber();
        switch (questionNumber)
        {
            case 1:
                Sex userSex = Sex.get(text);
                if (userSex != null && userSex != Sex.MALE_OR_FEMALE)
                    questionary.userSex = userSex;
                else
                {
                    result.addText(wrongAnswerReply);
                    return;
                }
                break;
            case 2:
                Sex coupleSex = Sex.get(text);
                if (coupleSex != null)
                    questionary.coupleSex = coupleSex;
                else
                {
                    result.addText(wrongAnswerReply);
                    return;
                }
                break;
        }
        if (questionary.isLastQuestion())
        {
            botAttributes.get(chatId).setBotState(BotState.NORMAL);
            result.addText(finishingQuestionaryReply);
        }
        else
            result.addQuestionAndAnswers(questionary.AskQuestion());
    }


    private final ArrayList<String> normalCommands =  new ArrayList<>(Arrays.asList("/help", "/able", "/disable",
            "/change", "/connect", "/money"));

    private final ArrayList<String> connectionCommands =  new ArrayList<>(Arrays.asList("/help",
            "/disconnect"));

    final static String botConnectionReply = "You have been connected to our bot for conversation";

    final static String botError = "Sorry, but there was some mistake in work of our bot";

    final static String nameQuestion = "What is your name?";

    final static String waitForSuitable = "Ok, now you can wait for a suitable person to appear";

    final static String botDisconnectionReply = "You've been disconnected from a conversation with the bot";

    final static String wrongAnswerReply = "There is no such answer\n";

    final static String finishingQuestionaryReply = "Your questionary is finished";

    final static String ableReply = "Now some strangers can connect to you";

    final static String disableReply = "Now no one can write you";

    final static String noSuitableQuestionaryReply = "Sorry, but for now there are no suitable people in our base.";

    final static String connectionReply = "You've been connected to some stranger. If you write something to me, it " +
            "will be sent to him.";

    final static String disconnectionReply = "You've been disconnected from a conversation with a stranger";

    final static String greetingInformation = "Hello, I am the greatest DateBot. I can help you to find " +
            "someone interesting to talk to. Lets start with a little questionary at the beginning. I will ask you " +
            "some questions about you and a couple you want to find. Please write only the numbers of the answers.\n";

    final static String commandsInformation = "Here are commands that are available for you to use:\n" +
            "'/help'-use it if you want to read about my functions\n" +
            "'/able'-use it if you want to able someone to write you\n" +
            "'/disable'-use it if you want to disable anyone to write you\n" +
            "'/money'-check your balance\n" +
            "'/connect'-use it if you want to find someone for the conversation\n" +
            "'/disconnect'-use it if you want stop the conversation\n" +
            "'/change'-use it if you want to rewrite your questionary\n";
}

