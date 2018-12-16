package pack;


public class Program
{

    public static void main(String[] args)
    {
        Database database = new Database();
        Long chatId1 = 2147483648L;
        Long chatId2 = 3147483648L;
        BotAttribute botAttribute = new BotAttribute(BotState.CONNECTED,new Questionary(), chatId2);
        botAttribute.setMoney(100);
        database.setBotAttribute(botAttribute, chatId1);

        System.out.println(database.getAbledUsers());
    }
}
