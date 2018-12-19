package pack;


import java.util.ArrayList;

public class Program
{

    public static void main(String[] args)
    {
        Database database = new Database();
        Long chatId1 = 10L;
        Questionary questionary = new Questionary();
        questionary.userSex = Sex.MALE;
        questionary.coupleSex = Sex.MALE_OR_FEMALE;
        BotAttribute botAttribute = new BotAttribute(BotState.NORMAL,questionary, "");
        botAttribute.setMoney(100);
        botAttribute.setCurrentQuestion("Who am i?");
        botAttribute.setMoneySubBotState(MoneySubBotState.NORMAL);
        botAttribute.setRpsState("rock");
        ArrayList<String> a = new ArrayList<>();
        a.add("123");
        a.add("Im sorry for everything ive done");
        botAttribute.setInterestQuestions(a);

        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Who?", "me", 1L));
        answers.add(new Answer("Where?", "there", 2L));
        botAttribute.setAnswers(answers);

        database.setBotAttribute(botAttribute, chatId1);
        BotAttribute botAttribute1 = database.getBotAttribute(chatId1);
        for (Long x: database.getSuitableIds(botAttribute1, chatId1))
        {
            System.out.println(x);
        }
    }
}
