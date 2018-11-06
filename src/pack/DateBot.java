package pack;

import java.util.concurrent.ConcurrentHashMap;

class DateBot
{

    private ConcurrentHashMap<Long, BotAttributes> botAttributes = new ConcurrentHashMap<>();

    private ConnectionManager connectionManager = new ConnectionManager(botAttributes);
    ConcurrentHashMap<Long, BotAttributes> getBotAttributes()
    {
        return botAttributes;
    }

    private CyberBuddy cyberBuddy = new CyberBuddy();

    private BotResult processCommands(Long chatId, String text)
    {
        BotResult result = new BotResult("", chatId);
        BotAttributes attributes = botAttributes.get(chatId);
        synchronized (attributes.lock)
        {
            BotState botState = attributes.getBotState();
            switch (text) {

                case "/start":
                    if (botState == BotState.STARTED) {
                        result.addText(greetingInformation);
                        attributes.setBotState(BotState.MAKING_QUESTIONARY);
                        result.addQuestionAndAnswers(attributes.getQuestionary().AskQuestion());
                    }
                    break;
                case "/help":
                    result.addText(commandsInformation);
                    break;
                case "/able":
                    result = connectionManager.enableConnection(chatId, botState);
                    break;
                case "/disable":
                    result = connectionManager.disableConnection(chatId);
                    break;
                case "/connect":
                    result = connectionManager.tryConnect(chatId, attributes);
                    break;
                case "/disconnect":
                    result = connectionManager.disconnect(chatId, botState, attributes);
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
            switch (botAttributes.get(chatId).getBotState()) {
                case MAKING_QUESTIONARY:
                    makeQuestionary(result, chatId, text);
                    break;
                case NORMAL:
                    result = processCommands(chatId, text);
                    break;
                case CONNECTED:
                    result = processCommands(chatId, text);
                    if (result == null) {
                        result = new BotResult();
                        result.addChatId(botAttributes.get(chatId).getConnection());
                        result.addText(text);
                    }
                    break;
                case STARTED:
                    result = processCommands(chatId, text);
                    break;
                case TALKING_WITH_BOT:
                    if (text.equals("/disconnect"))
                    {
                        result.addText("You've been disconnected from a conversation with the bot");
                        botAttributes.get(chatId).setBotState(BotState.NORMAL);
                    }
                    else
                    {
                        Questionary questionary =  botAttributes.get(chatId).getQuestionary();
                        try
                        {
                            result.addText(cyberBuddy.getMessage(text, chatId, questionary.userSex,
                                    questionary.coupleSex));
                        }
                        catch (Exception e)
                        {
                            result.addText("Sorry, but there was some mistake in work of out bot");
                        }
                    }
                    break;
                case ASKED_ABOUT_BOT:
                    if (text.toLowerCase().equals("yes"))
                    {
                        result.addText("You have been connected to our bot for conversation");
                        botAttributes.get(chatId).setBotState(BotState.TALKING_WITH_BOT);
                    }
                    break;
            }
        }
        return result;
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


    final static String wrongAnswerReply = "There is no such answer\n";

    final static String finishingQuestionaryReply = "Your questionary is finished";

    final static String ableReply = "Now some strangers can connect to you";

    final static String disableReply = "Now no one can write you";

    final static String noSuitableQuestionaryReply = "Sorry, but for now there are no suitable people in our base." +
            "Would you like to talk to our conversation bot?";

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
            "'/connect'-use it if you want to find someone for the conversation\n" +
            "'/disconnect'-use it if you want stop the conversation\n" +
            "'/change'-use it if you want to rewrite your questionary\n";
}
