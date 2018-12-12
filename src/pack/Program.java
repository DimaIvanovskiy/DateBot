package pack;


public class Program
{

    public static void main(String[] args)
    {
        Database database = new Database();
        Long chatId = 2147483648L;
        database.setQuestionary(chatId, Sex.MALE, Sex.MALE);
        database.setBotState(chatId, BotState.PLAYING);
        database.setMoney(chatId, 34);
        database.addAbledUser(chatId);
        database.addAbledUser(111L);
        System.out.println(database.getAbledUsers());
    }
}
