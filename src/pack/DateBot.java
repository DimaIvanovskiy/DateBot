package pack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class DateBot
{
    private HashSet<Long> abledUsers = new HashSet<>();

    private final Object lock = new Object();

    private ConcurrentHashMap<Long, BotAttributes> botAttributes = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Long, BotAttributes> getBotAttributes() {
        return botAttributes;
    }

    private BotResult processCommands(Long chatId, String text)
    {
        BotResult result = new BotResult("", chatId);
        BotAttributes attributes = botAttributes.get(chatId);
        State botState = attributes.getState();
        switch (text)
        {

            case "/start":
                if (botState == State.STARTED)
                {
                    result.addText(greetingInformation);
                    attributes.setState(State.MAKING_QUESTIONARY);
                    result.addQuestionAndAnswers(attributes.getQuestionary().AskQuestion());
                }
                break;
            case "/help":
                result.addText(commandsInformation);
                break;
            case "/able":
                if (botState == State.CONNECTED || abledUsers.contains(chatId))
                    return result;
                abledUsers.add(chatId);
                result.addText(ableReply);
                break;
            case "/disable":
                if (!abledUsers.contains(chatId))
                    return result;
                abledUsers.remove(chatId);
                result.addText(disableReply);
                break;
            case "/connect":
                synchronized (lock)
                {
                    if (botState == State.CONNECTED)
                        return result;
                    Long suitable = FindSuitable(chatId);
                    if (suitable == null)
                        result.addText(noSuitableQuestionayReply);
                    else {
                        abledUsers.remove(chatId);
                        abledUsers.remove(suitable);
                        attributes.setConnection(suitable);
                        botAttributes.get(suitable).setConnection(chatId);
                        attributes.setState(State.CONNECTED);
                        botAttributes.get(suitable).setState(State.CONNECTED);
                        result.addChatId(suitable);
                        result.addText(connectionReply);
                    }
                }
                break;
            case "/disconnect":
                if (botState != State.CONNECTED)
                    return result;
                Long connection = attributes.getConnection();
                attributes.setState(State.NORMAL);
                botAttributes.get(connection).setState(State.NORMAL);
                botAttributes.get(connection).setConnection(connection);
                attributes.setConnection(chatId);
                result.addChatId(connection);
                result.addText(disconnectionReply);
                break;
            case "/change":
                if (botState == State.CONNECTED)
                    return result;
                attributes.setState(State.MAKING_QUESTIONARY);
                attributes.setQuestionary(new Questionary());
                result.addQuestionAndAnswers(attributes.getQuestionary().AskQuestion());
                break;
            default:
                return null;
        }
        return result;
    }


    private Long FindSuitable(Long chatId)
    {
        if (abledUsers.isEmpty())
            return null;
        Questionary curUser = botAttributes.get(chatId).getQuestionary();
        ArrayList<Long> suitable = new ArrayList<>();
        for (Long id : abledUsers)
        {

            if ( !id.equals(chatId) && curUser.isSuitable(botAttributes.get(id).
                    getQuestionary()))
                suitable.add(id);
        }
        if (suitable.isEmpty())
            return null;
        Random random = new Random();
        return suitable.get(random.nextInt(suitable.size()));
    }

    public BotResult processMessage(Long chatId, String text)
    {
        if (!botAttributes.containsKey(chatId))
            botAttributes.putIfAbsent(chatId, new BotAttributes(State.STARTED,
                    new Questionary(), chatId));
        BotResult result = new BotResult("", chatId);
        if (text == null && botAttributes.get(chatId).getState() == State.CONNECTED)
        {
            result.addChatId(botAttributes.get(chatId).getConnection());
        }
        else
        {
            switch (botAttributes.get(chatId).getState()) {
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
            botAttributes.get(chatId).setState(State.NORMAL);
            result.addText(finishingQuestionaryReply);
        }
        else
            result.addQuestionAndAnswers(questionary.AskQuestion());
    }


    final static String wrongAnswerReply = "There is no such answer\n";

    final static String finishingQuestionaryReply = "Your questionary is finished";

    final static String ableReply = "Now some strangers can connect to you";

    final static String disableReply = "Now no one can write you";

    final static String noSuitableQuestionayReply = "Sorry, but for now there are no suitable people in our base.";

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
