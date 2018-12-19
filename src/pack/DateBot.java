package pack;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class DateBot
{
    Database database = new Database();
    Random random= new Random();

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
            case "/answerQuestions":
                addSuitableQuestion(attribute,result, chatId);
                break;
            case "/lookAtAnswers":
                addAnswer(attribute, result);
                break;
            case "/changeInterestQuestions":
                attribute.setBotState(BotState.WRITING_INTEREST_QUESTIONS);
                result.addText(interestQuestionsInfo);
                break;
            default:
                return null;
        }
        database.setBotAttribute(attribute, chatId);
        return result;
    }

    private void addAnswer(BotAttribute attribute, BotResult result)
    {

        ArrayList<Answer> answers = attribute.getAnswers();
        if (answers.size()==0)
        {
            result.addText("Sorry, but there is no answers for your questions");
            attribute.setBotState(BotState.NORMAL);
            return;
        }
        Answer answer = answers.get(0);
        result.addText(MessageFormat.format("Your question was: {0}\n The answer is {1}",
                answer.question, answer.answer));
        attribute.setBotState(BotState.LOOKING_AT_ANSWERS);
    }

    private void addSuitableQuestion(BotAttribute attribute, BotResult result, Long chatId)
    {
        ArrayList<Long> suitableIds = database.getSuitableIds(attribute, chatId);
        if (suitableIds.isEmpty())
        {
            attribute.setBotState(BotState.NORMAL);
            result.addText(emptySuitableIds);
        }
        else
        {
            Long id = suitableIds.get(random.nextInt(suitableIds.size()));
            ArrayList<String> questions = database.getBotAttribute(id).getInterestQuestions();
            String question = questions.get(random.nextInt(questions.size()));
            result.addText(question);
            attribute.setBotState(BotState.ANSWERING_QUESTIONS);
            attribute.addAnsweredQuestionId(id);
            attribute.setCurrentQuestion(question);
        }
    }

    private BotResult processAnswering(Long chatId, String text, BotAttribute attribute, BotResult result)
    {
        switch (text)
        {
            case "/finish":
                attribute.setBotState(BotState.NORMAL);
                result.addText(finishingAnswering);
                break;
            case "/help":
                result.addText(commandsInformation);
                break;
            case "/money":
                result.addText(moneyInformation);
                break;
            default:
                ArrayList<Long> answered = attribute.getAnsweredQuestionIds();
                Long id = answered.get(answered.size()-1);
                BotAttribute pair = database.getBotAttribute(id);
                pair.addAnswer(new Answer(attribute.getCurrentQuestion(), text, chatId));
                database.setBotAttribute(pair, id);
                addSuitableQuestion(attribute, result, chatId);
                break;
        }
        return result;
    }


    private void processLooking(Long chatId, String text, BotAttribute attribute, BotResult result)
    {
        Answer answer = attribute.getAnswers().remove(0);
        switch (text)
        {
            case "/finish":
                attribute.setBotState(BotState.NORMAL);
                result.addText(finishingAnswering);
                break;
            case "/help":
                result.addText(commandsInformation);
                break;
            case "/money":
                result.addText(commandsInformation);
                break;
            case "/like":
                BotAttribute pair = database.getBotAttribute(answer.id);
                pair.addLiked(chatId);
                database.setBotAttribute(pair, answer.id);
                if (attribute.getLiked().contains(answer.id))
                {

                    result.addText(MessageFormat.format("It is the match!!!\nUsername1:{0} & Username2:{1}",
                            attribute.getUserName(), pair.getUserName()));
                }
                addAnswer(attribute, result);
                break;
            case "/dislike":
                addAnswer(attribute, result);
                break;
        }
    }

    BotResult processMessage(Long chatId, String text, String userName)
    {
        if (!database.botAttributesContains(chatId))
            database.createBotAttribute(new BotAttribute(BotState.STARTED,
                    new Questionary(), userName), chatId);
        BotResult result = new BotResult("", chatId);
        BotAttribute botAttribute = database.getBotAttribute(chatId);

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
                updateInterestQuestions(result, botAttribute, text);
                break;
            case ANSWERING_QUESTIONS:
                processAnswering(chatId, text, botAttribute, result);
                break;
            case LOOKING_AT_ANSWERS:
                processLooking(chatId, text, botAttribute, result);
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
            case ANSWERING_QUESTIONS:
                result.addCurrentCommands(answeringCommands);
                break;
            case LOOKING_AT_ANSWERS:
                result.addCurrentCommands(lookingCommands);
                break;
        }
    }

    private void updateInterestQuestions(BotResult result, BotAttribute botAttribute, String text)
    {
        String[] questions = text.split("\n");
        ArrayList<String> result_questions = new ArrayList<>();
        for (String question : questions)
            if (!question.equals(""))
                result_questions.add(question);
        if (result_questions.isEmpty())
        {
            result.addText(emptyQuestionts);
        }
        else
        {
            result.addText(filledQuestions);
            botAttribute.setInterestQuestions(result_questions);
            botAttribute.setBotState(BotState.NORMAL);
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
        if (questionary.getNumber() == questionary.questions.length)
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
            "/change", "/play", "/answerQuestions", "/lookAtAnswers", "/changeInterestQuestions"));

    private final ArrayList<String> answeringCommands =  new ArrayList<>(Arrays.asList("/help", "/money",
           "/finish" ));

    private final ArrayList<String> lookingCommands =  new ArrayList<>(Arrays.asList("/help", "/money",
            "/finish" , "/like", "/dislike"));

    final static int connectionCost = 60;

    final static int startMoneyCount = 100;

    final static String finishingAnswering = "You finished to answer on questions";

    final static String emptySuitableIds = "Sorry, but for now there are no questions for you to answer";

    final static String filledQuestions = "Your interest questions have been updated.";

    final static String emptyQuestionts = "Sorry, but  your questions are empty, try again.";

    final static String interestQuestionsInfo = "Please, write questions that will help you to " +
            "find suitable person for you. Each new question should start with the newline";

    final static String notEnoughMoney = "Sorry, but you do not have enough money for connection with human." +
            " Your current money count is {0} coins. Connection costs {1} coins.";

    final static String moneyInformation = "You have {0} coins. Connection costs {1} coins.";

    final static String wrongAnswerReply = "There is no such answer\n";

    final static String finishingQuestionaryReply = "Your questionary is finished\n";

    final static String greetingInformation = "Hello, I am the greatest DateBot. I can help you to find " +
            "someone interesting to talk to. Lets start with a little questionary at the beginning. I will ask you " +
            "some questions about you and a couple you want to find. Please write only the numbers of the answers.\n";

    final static String commandsInformation = "Here are commands that are available for you to use:\n" +
            "'/help'-use it if you want to read about my functions\n" +
            "'/play'-earn money for playing\n" +
            "'/money'-check your balance\n" +
            "'/change'-use it if you want to rewrite your questionary\n" +
            "'/answerQuestions'-use it if you want to answer some questions\n" +
            "'/lookAtAnswers'-use it if you want to look at answers on your questions\n";
}

