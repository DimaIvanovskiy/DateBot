package pack;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
            case "/play":
                attribute.setBotState(BotState.PLAYING);
                moneyBot.startSession(result, attribute);
                break;
            case "/change":
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
                    new Questionary()), chatId);
        BotResult result = new BotResult("", chatId);
        BotAttribute botAttribute = database.getBotAttrubute(chatId);

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
            case STARTED:
                result = processCommands(chatId, text, botAttribute);
                break;
            case WRITING_INTEREST_QUESTIONS:
                String[] questions = text.split("/n");
                break;
        }


        if (result == null)
            result = new BotResult();
        addButtons(result, botAttribute.getBotState());
        database.setBotAttribute(botAttribute, chatId);
        return result;
    }

    private void addButtons(BotResult result, BotState botState)
    {
        switch (botState)
        {
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
            result.addText(finishingQuestionaryReply);
            if (botAttribute.getInterestQuestions().size()==0)
            {
                botAttribute.setBotState(BotState.WRITING_INTEREST_QUESTIONS);
                result.addText(interestQuestionsInfo);
            }
            else
                botAttribute.setBotState(BotState.NORMAL);
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

    final static int connectionCost = 60;

    final static int startMoneyCount = 100;

    final static String interestQuestionsInfo = "Please, write questions that will help you to " +
            "find suitable person for you. Each new question should start with the newline";

    final static String notEnoughMoney = "Sorry, but you do not have enough money for connection with human." +
            " Your current money count is {0} coins. Connection costs {1} coins.";

    final static String moneyInformation = "You have {0} coins. Connection costs {1} coins.";

    final static String wrongAnswerReply = "There is no such answer\n";

    final static String finishingQuestionaryReply = "Your questionary is finished";

    final static String greetingInformation = "Hello, I am the greatest DateBot. I can help you to find " +
            "someone interesting to talk to. Lets start with a little questionary at the beginning. I will ask you " +
            "some questions about you and a couple you want to find. Please write only the numbers of the answers.\n";

    final static String commandsInformation = "Here are commands that are available for you to use:\n" +
            "'/help'-use it if you want to read about my functions\n" +
            "'/able'-use it if you want to able someone to write you\n" +
            "'/disable'-use it if you want to disable anyone to write you\n" +
            "'/play'-earn money for playing\n" +
            "'/money'-check your balance\n" +
            "'/connect'-use it if you want to find someone for the conversation\n" +
            "'/disconnect'-use it if you want stop the conversation\n" +
            "'/change'-use it if you want to rewrite your questionary\n";
}

