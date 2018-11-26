package pack;

class BotAttributes
{
    final Object lock = new Object();
    private Questionary questionary;
    private BotState botState;
    private Long connection;
    private int money;
    private String userName;

    Questionary getQuestionary() { return questionary; }
    BotState getBotState() { return botState; }
    Long getConnection() { return connection; }
    String getUserName() { return userName; }
    int getMoney() { return money; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setConnection(Long newConnection) { connection = newConnection; }
    void setUserName(String userName){this.userName = userName;}
    void addMoney(int value){this.money += value;}
    void substractMoney(int value){this.money -= value;}


    BotAttributes(BotState newBotState, Questionary newQuestionary, Long newConnection)
    {
        botState = newBotState;
        questionary = newQuestionary;
        connection = newConnection;
    }



}
