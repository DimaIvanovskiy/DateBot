package pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static pack.MoneySubBotState.NORMAL;
import static pack.MoneySubBotState.PLAYING;
import static pack.MoneySubBotState.QUITTING;

public class MoneySubBot {
    private Database database;
    private Random rand = new Random();

    public MoneySubBot( Database database)
    {
        this.database = database;
    }

    void startSession(BotResult result, BotAttribute botAttribute)
    {
        if (botAttribute.getMoneySubBotState()==null)
        {
            botAttribute.setMoneySubBotState(NORMAL);
            result.addText(getIntroduction());
        }
        startRps(result, botAttribute);
    }

    void startRps(BotResult result, BotAttribute botAttribute)
    {
        result.addQuestionAndAnswers(rpsOptions);
        botAttribute.setRpsState(rpsWarriors[rand.nextInt(rpsWarriors.length)]);
        botAttribute.setMoneySubBotState(PLAYING);
    }

    boolean processGeneralCommands(BotResult result, String text, BotAttribute botAttribute)
    {
        switch (text) {
            case "/help":
                result.addText(helpMessage);
                return true;
            case "/quit":
                result.addText(getGoodbye());
                botAttribute.setMoneySubBotState(QUITTING);
                return false;
        }
        return false;
    }

    boolean processMessage(BotResult result, String text, BotAttribute botAttribute)
    {
        if (processGeneralCommands(result, text, botAttribute))
            return false;
        switch (botAttribute.getMoneySubBotState())
        {
            case NORMAL:
                startRps(result, botAttribute);
                break;
            case PLAYING:
                return processRps(result, text, botAttribute);
            case QUITTING:
                return true;
        }
        return false;
    }

    boolean processRps(BotResult result, String text, BotAttribute botAttribute)
    {
        var warrior = numberToRps.getOrDefault(text, "0");
        if (!Arrays.asList(rpsWarriors).contains(warrior))
        {
            result.addText(unknownMessage);
            return false;
        }

        var botRps = botAttribute.getRpsState();
        result.addText(String.format("%s VS %s\n", warrior, botRps));

        if (botRps.equals(warrior)) {
            result.addText("Draw!");
        }
        else if (rpsWins.get(warrior).contains(botRps)){
            var wonMoney = rand.nextInt(winValue) + winValue;
            result.addText(String.format(winMessage, wonMoney));
            botAttribute.addMoney(wonMoney);
        }
        else {
            var lostMoney = rand.nextInt(loseValue) + loseValue;
            result.addText(String.format(loseMessage, lostMoney));
            botAttribute.substractMoney(lostMoney);
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

    static String showMoney(BotAttribute attribute)
    {
        return moneyCheck + attribute.getMoney();
    }

    private final static String[] rpsWarriors = new String[] {"Rock", "Paper", "Scissors"};
    private final static HashMap<String, String> numberToRps = new HashMap<>() {{
       put("1", "Rock");
       put("2", "Paper");
       put("3", "Scissors");
    }};
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
            "you can fill your pockets with!\n";
    private final static String goodbyeMessage = "Goodbye!\n";
    private final static String unknownMessage = "Maybe you should try getting some /help?";
    final static String helpMessage = "Available commands:\n" +
            "'/help' - get this message\n"+
            "'/play' - play rock-paper-scissors" +
            "'/quit' - quit from money making (why would you want that though?)";
    private final static String moneyCheck = "Current balance:\n";
}

