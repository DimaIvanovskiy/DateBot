package pack;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

class DateBot
{
    Database database = new Database();

    private CyberBuddy cyberBuddy = new CyberBuddy();
    private MoneySubBot moneyBot = new MoneySubBot(database);

    private BotResult processCommands(Long chatId, String text, BotAttribute attribute)
    {
        BotResult result = new BotResult("", chatId);

        BotState botState = attribute.getBotState();
        switch (text)
        {
            case "/start":
                if (botState == BotState.STARTED)
                {
                    result.addText(greetingInformation);
                    attribute.setBotState(BotState.MAKING_QUESTIONARY);
                    result.addQuestionAndAnswers(attribute.getQuestionary().AskQuestion());
                }
                break;
            case "/help":
                result.addText(commandsInformation);
                break;
            case "/money":
                result.addText(MessageFormat.format(moneyInformation, attribute.getMoney(), connectionCost));
                break;
            case "/able":
                result = enableConnection(chatId, botState);
                break;
            case "/disable":
                result = disableConnection(chatId);
                break;
            case "/play":
                attribute.setBotState(BotState.PLAYING);
                moneyBot.startSession(result, attribute);
                break;
            case "/connect":
                Long suitableId = ConnectionManager.findSuitable(chatId, database, database.getAbledUsers());
                int currentMoney = attribute.getMoney();
                if (suitableId == null || currentMoney<connectionCost)
                {
                    if (suitableId == null)
                        result.addText(DateBot.noSuitableQuestionaryReply);
                    else
                        result.addText(MessageFormat.format(DateBot.notEnoughMoney, currentMoney, connectionCost));

                    result.addQuestionAndAnswers(new QuestionAndAnswers("Would you like to talk to " +
                            "our conversation bot?", "yes", "no"));
                    attribute.setBotState(BotState.ASKED_ABOUT_BOT);
                }
                else
                {
                    int pairMoneyCount = database.getBotAttrubute(suitableId).getMoney();
                    database.removeAbledUser(chatId);
                    database.removeAbledUser(suitableId);
                    attribute.setSuitableId(suitableId);
                    result.addQuestionAndAnswers(new QuestionAndAnswers(MessageFormat.format(
                            "Would you like to talk to stranger with {0} coins?", pairMoneyCount),
                            "yes", "no"));
                    attribute.setBotState(BotState.ASKED_ABOUT_CONNECTION);
                }

                break;
            case "/disconnect":
                if (botState == BotState.TALKING_WITH_BOT)
                {
                    result.addText(DateBot.botDisconnectionReply);
                    attribute.setBotState(BotState.NORMAL);
                    return result;
                }
                if (botState == BotState.CONNECTED)
                {
                    Long connection = ConnectionManager.disconnect(chatId, database, attribute);
                    result.addChatId(connection);
                    result.addText(DateBot.disconnectionReply);
                }
                break;
            case "/change":
                if (botState == BotState.CONNECTED)
                    return result;
                attribute.setBotState(BotState.MAKING_QUESTIONARY);
                attribute.setQuestionary(new Questionary());
                result.addQuestionAndAnswers(attribute.getQuestionary().AskQuestion());
                break;
            default:
                return null;
        }
        database.setBotAttribute(attribute, chatId);
        return result;
    }


    BotResult processMessage(Long chatId, String text)
    {
        if (!database.botAttributesContains(chatId))
            database.createBotAttribute(new BotAttribute(BotState.STARTED,
                    new Questionary(), chatId), chatId);
        BotResult result = new BotResult("", chatId);
        BotAttribute botAttribute = database.getBotAttrubute(chatId);
        if (text == null && botAttribute.getBotState() == BotState.CONNECTED)
        {
            result.addChatId(botAttribute.getConnection());
        }
        else
        {
            switch (botAttribute.getBotState())
            {
                case MAKING_QUESTIONARY:
                    makeQuestionary(result, chatId, text, botAttribute);
                    break;
                case NORMAL:
                    result = processCommands(chatId, text, botAttribute);
                    break;
                case PLAYING:
                    var is_finished = moneyBot.processMessage(result, text, botAttribute);
                    if (is_finished)
                        botAttribute.setBotState(BotState.NORMAL);
                    break;
                case CONNECTED:
                    result = processCommands(chatId, text, botAttribute);
                    if (result == null)
                    {
                        result = new BotResult();
                        result.addChatId(botAttribute.getConnection());
                        result.addText(text);
                    }
                    break;
                case STARTED:
                    result = processCommands(chatId, text, botAttribute);
                    break;
                case TALKING_WITH_BOT:
                    result = processCommands(chatId, text, botAttribute);
                    if (result == null)
                    {
                        result = new BotResult("", chatId);
                        Questionary questionary =  botAttribute.getQuestionary();
                        try
                        {
                            result.addText(cyberBuddy.getMessage(text, chatId, questionary.userSex,
                                    questionary.coupleSex, botAttribute.getUserName()));
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
                            botAttribute.setBotState(BotState.ASKED_NAME);
                            break;
                        case "2":
                            result.addText(waitForSuitable);
                            botAttribute.setBotState(BotState.NORMAL);
                            break;
                    }
                    break;
                case ASKED_NAME:
                    botAttribute.setUserName(text);
                    result.addText(botConnectionReply);
                    botAttribute.setBotState(BotState.TALKING_WITH_BOT);
                    break;
                case ASKED_ABOUT_CONNECTION:
                    Long suitableId = botAttribute.getSuitableId();
                    switch (text)
                    {
                        case "1":
                            int currentMoney = botAttribute.getMoney();

                            ConnectionManager.connect(chatId, suitableId, database, botAttribute);
                            botAttribute.setMoney(currentMoney-connectionCost);
                            result.addChatId(suitableId);
                            result.addText(DateBot.connectionReply);
                            break;
                        case "2":
                            result.addText(waitForSuitable);
                            botAttribute.setBotState(BotState.NORMAL);
                            database.addAbledUser(chatId);
                            database.addAbledUser(suitableId);
                            break;
                    }
            }
        }
        if (result == null)
            result = new BotResult();
        addButtons(result, botAttribute.getBotState());
        database.setBotAttribute(botAttribute, chatId);
        return result;
    }

    BotResult enableConnection(Long chatId, BotState botState)
    {
        BotResult result = new BotResult("", chatId);
        if (botState == BotState.CONNECTED || database.abledUsersContains(chatId))
            return result;
        database.addAbledUser(chatId);
        result.addText(DateBot.ableReply);
        return result;
    }

    BotResult disableConnection(Long chatId)
    {
        BotResult result = new BotResult("", chatId);
        if (!database.abledUsersContains(chatId))
            return result;
        database.removeAbledUser(chatId);
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
            case PLAYING:
                result.addCurrentCommands(playingCommands);
            break;
        }
    }

    private void makeQuestionary(BotResult result, Long chatId, String text, BotAttribute botAttribute)
    {
        Questionary questionary = botAttribute.getQuestionary();
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
        if (questionary.getNumber()==questionary.questions.length)
        {
            botAttribute.setBotState(BotState.NORMAL);
            result.addText(finishingQuestionaryReply);
        }
        else
            result.addQuestionAndAnswers(questionary.AskQuestion());
        botAttribute.setQuestionary(questionary);
        database.setBotAttribute(botAttribute, chatId);
    }

    private final ArrayList<String> playingCommands =  new ArrayList<>(Arrays.asList("/help","/quit"));


    private final ArrayList<String> normalCommands =  new ArrayList<>(Arrays.asList("/help","/money",
            "/able", "/disable", "/change", "/connect", "/play"));

    private final ArrayList<String> connectionCommands =  new ArrayList<>(Arrays.asList("/help", "/money",
            "/disconnect"));

    public static final ArrayList<String> gameCommands =  new ArrayList<>(Arrays.asList("/help", "/quit"));

    final static int connectionCost = 60;

    final static int startMoneyCount = 100;

    final static String notEnoughMoney = "Sorry, but you do not have enough money for connection with human." +
            " Your current money count is {0} coins. Connection costs {1} coins.";

    final static String moneyInformation = "You have {0} coins. Connection costs {1} coins.";

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

