package pack;


public class Program
{

    public static void main(String[] args)
    {
        Database database = new Database();
        Long chatId1 = 2147483648L;
        Long chatId2 = 1147483648L;

        Questionary questionary = new Questionary();
        questionary.userSex = Sex.MALE;
        BotAttribute botAttribute = new BotAttribute(BotState.CONNECTED, questionary, chatId2);
        botAttribute.setMoney(100);
        botAttribute.setMoneySubBotState(MoneySubBotState.NORMAL);
        botAttribute.setRpsState("rock");

        database.setBotAttribute(botAttribute, chatId1);
        BotAttribute botAttribute1 = database.getBotAttrubute(chatId1);
        boolean contains = database.botAttributesContains(11L);
    }
}
