package pack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static pack.MoneySubBotStates.NORMAL;
import static pack.MoneySubBotStates.PLAYING;
import static pack.MoneySubBotStates.QUITTING;

public class MoneySubBot {
    private ConcurrentHashMap<Long, BotAttributes> botAttributes;
    private ConcurrentHashMap<Long, MoneySubBotStates> botStates = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, String> rpsStates = new ConcurrentHashMap<>();
    private Random rand = new Random();

    public MoneySubBot(ConcurrentHashMap<Long, BotAttributes> botAttributes)
    {
        this.botAttributes = botAttributes;
    }

    void startSession(BotResult result, Long chatId)
    {
        if (!botStates.containsKey(chatId)) {
            botStates.putIfAbsent(chatId, MoneySubBotStates.NORMAL);
            result.addText(getIntroduction());
        }
        startRps(result, chatId);
    }

    void startRps(BotResult result, Long chatId)
    {
        result.addQuestionAndAnswers(rpsOptions);
        rpsStates.put(chatId, rpsWarriors[rand.nextInt(rpsWarriors.length)]);
        botStates.put(chatId, PLAYING);
    }

    boolean processGeneralCommands(BotResult result, Long chatId, String text)
    {
        switch (text) {
            case "/help":
                result.addText(helpMessage);
                return true;
            case "/quit":
                result.addText(getGoodbye());
                botStates.put(chatId, QUITTING);
                return false;
        }
        return false;
    }

    boolean processMessage(BotResult result, Long chatId, String text)
    {
        if (processGeneralCommands(result, chatId, text))
            return false;
        switch (botStates.get(chatId))
        {
            case NORMAL:
                startRps(result, chatId);
                break;
            case PLAYING:
                return processRps(result, chatId, text);
            case QUITTING:
                return true;
        }
        return false;
    }

    boolean processRps(BotResult result, Long chatId, String text)
    {
        if (!Arrays.asList(rpsWarriors).contains(text))
        {
            result.addText(unknownMessage);
            return false;
        }

        var botRps = rpsStates.get(chatId);
        result.addText(String.format("%s VS %s", text, botRps));

        if (botRps.equals(text)) {
            result.addText("Draw!");
        }
        else if (rpsWins.get(text).contains(botRps)){
            var wonMoney = rand.nextInt(winValue) + winValue;
            result.addText(String.format(winMessage, wonMoney));
            botAttributes.get(chatId).addMoney(wonMoney);
        }
        else {
            var lostMoney = rand.nextInt(loseValue) + loseValue;
            result.addText(String.format(loseMessage, lostMoney));
            botAttributes.get(chatId).substractMoney(lostMoney);
        }
        return true;

    }

    static String getIntroduction()
    {
        return introductionMessage;
    }

    static String getGoodbye()
    {
        return goodbyeMessage;
    }

    static String showMoney(BotAttributes attribute)
    {
        return moneyCheck + attribute.getMoney();
    }

    private final static String[] rpsWarriors = new String[] {"Rock", "Paper", "Scissors"};
    private final static QuestionAndAnswers rpsOptions = new QuestionAndAnswers("Choose your fighter", rpsWarriors);
    private final static HashMap<String, ArrayList<String>> rpsWins = new HashMap<>() {{
        put("Rock", new ArrayList<>() {{ add("Scissors");}});
        put("Paper", new ArrayList<>() {{ add("Rock");}});
        put("Scissors", new ArrayList<>() {{ add("Paper");}});
    }};
    private final static int winValue = 50;
    private final static int loseValue = 50;
    private final static String winMessage = "You win! +%d to your balance";
    private final static String loseMessage = "You lost) -%d to your balance";


    private final static String introductionMessage = "Seems like you've given up trying to impress " +
            "your potential partner with charisma alone. It's finally time to get some cash, " +
            "let the whole world know that Love is not the only thing " +
            "you can fill your pockets with!";
    private final static String goodbyeMessage = "Goodbye!";
    private final static String unknownMessage = "Maybe you should try getting some /help?";
    final static String helpMessage = "Available commands:\n" +
            "'/help' - get this message\n"+
            "'/play' - play rock-paper-scissors" +
            "'/quit' - quit from money making (why would you want that though?)";
    private final static String moneyCheck = "Current balance:\n";
}

