package pack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class DateBot
{
    private HashSet<Long> abledUsers = new HashSet<>();

    private HashMap<Long, Questionary> questionaries = new HashMap<>();

    private HashMap<Long, State> states = new HashMap<>();

    private HashMap<Long, Long> connections = new HashMap<>();

    private BotResult processCommands(Long chatId, String text)
    {
        BotResult result = new BotResult("", chatId);
        switch (text)
        {
            case "/start":
                if (states.get(chatId) == State.STARTED)
                {
                    result.addText(greetingInformation);
                    states.put(chatId, State.MAKING_QUESTIONARY);
                    questionaries.put(chatId, new Questionary());
                    result.addText(questionaries.get(chatId).AskQuestion());
                }
                break;
            case "/help":
                result.addText(commandsInformation);
                break;
            case "/able":
                if (states.get(chatId) == State.CONNECTED || abledUsers.contains(chatId))
                    return result;
                abledUsers.add(chatId);
                result.addText("Now some strangers can connect to you");
                break;
            case "/disable":
                if (!abledUsers.contains(chatId))
                    return result;
                abledUsers.remove(chatId);
                result.addText("Now no one can wright you");
                break;
            case "/connect":
                if (states.get(chatId) == State.CONNECTED)
                    return result;
                Long suitable = FindSuitable(chatId);
                if (suitable == null)
                    result.addText("Sorry, but for now there are no suitable people in our base.");
                else
                {
                    abledUsers.remove(chatId);
                    abledUsers.remove(suitable);
                    connections.put(chatId, suitable);
                    connections.put(suitable, chatId);
                    states.put(chatId, State.CONNECTED);
                    states.put(suitable, State.CONNECTED);
                    result.addChatId(suitable);
                    result.addText( "You've been connected to some stranger. If you write something to me, it " +
                            "will be sent to him.");
                }
                break;
            case "/disconnect":
                if (states.get(chatId) != State.CONNECTED)
                    return result;
                Long connection = connections.get(chatId);
                states.put(chatId, State.NORMAL);
                states.put(connection, State.NORMAL);
                connections.remove(chatId);
                connections.remove(connection);
                result.addChatId(connection);
                result.addText("You've been disconnected from a conversation with a stranger");
                break;
            case "/change":
                if (states.get(chatId) == State.CONNECTED)
                    return result;
                states.put(chatId, State.MAKING_QUESTIONARY);
                questionaries.put(chatId, new Questionary());
                result.addText(questionaries.get(chatId).AskQuestion());
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
        Questionary curUser = questionaries.get(chatId);
        ArrayList<Long> suitable = new ArrayList<>();
        for (Long id : abledUsers)
        {

            if ( !id.equals(chatId) && curUser.isSuitable(questionaries.get(id)))
                suitable.add(id);
        }
        if (suitable.isEmpty())
            return null;
        Random random = new Random();
        return suitable.get(random.nextInt(suitable.size()));
    }

    public BotResult processMessage(Long chatId, String text)
    {
        if (!states.containsKey(chatId))
            states.put(chatId,State.STARTED);
        BotResult result = new BotResult("", chatId);
        switch(states.get(chatId))
        {
            case MAKING_QUESTIONARY:
                result.addText(makeQuestionary(chatId, text));
                break;
            case NORMAL:
                result =  processCommands(chatId, text);
                break;
            case CONNECTED:
                result = processCommands(chatId, text);
                if (result==null)
                {
                    result = new BotResult();
                    result.addChatId(connections.get(chatId));
                    result.addText(text);
                }
                break;
            case STARTED:
                result =  processCommands(chatId, text);
                break;
        }
        return result;
    }

    private String makeQuestionary(Long chatId, String text)
    {
        String result = "";
        Questionary questionary = questionaries.get(chatId);
        int questionNumber = questionary.getNumber();
        switch (questionNumber)
        {
            case 1:
                Sex userSex = Sex.get(text);
                if (userSex!=null)
                    questionary.userSex = userSex;
                else
                    return "There is no such answer";
                break;
            case 2:
                Sex coupleSex = Sex.get(text);
                if (coupleSex!=null)
                    questionary.coupleSex = coupleSex;
                else
                    return "There is no such answer";
                break;
        }
        if (questionary.isLastQuestion())
        {
            states.put(chatId, State.NORMAL);
            result = "Your questionary is finished";
        }
        else
            result = questionary.AskQuestion();
        return result;
    }


    final private static String greetingInformation = "Hello, I am the greatest DateBot. I can help you to find " +
            "someone interesting to talk to. Lets start with a little questionary at the beginning. I will ask you " +
            "some questions about you and a couple you want to find. Please write only the numbers of the answers.\n";

    final private static String commandsInformation = "Here are commands that are available for you to use:\n" +
            "'/help'-use it if you want to read about my functions\n" +
            "'/able'-use it if you want to able someone to write you\n" +
            "'/disable'-use it if you want to disable anyone to write you\n" +
            "'/connect'-use it if you want to find someone for the conversation\n" +
            "'/disconnect'-use it if you want stop the conversation\n" +
            "'/change'-use it if you want to rewrite your questionary\n";
}
