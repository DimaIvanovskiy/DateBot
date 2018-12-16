package pack;

class BotAttribute
{
    final Object lock = new Object();
    private Questionary questionary;
    private BotState botState;
    private Long connection;
    private String userName;
    private int money = DateBot.startMoneyCount;
    private Long suitableId;
    private MoneySubBotState moneySubBotState;
    private String rpsState;

    Questionary getQuestionary() { return questionary; }
    BotState getBotState() { return botState; }
    Long getConnection() { return connection; }
    String getUserName() { return userName; }
    int getMoney() { return money; }
    Long getSuitableId() { return suitableId; }
    MoneySubBotState getMoneySubBotState() { return moneySubBotState; }
    String getRpsState() { return rpsState; }

    void setRpsState(String rpsState) { this.rpsState = rpsState; }
    void setMoneySubBotState(MoneySubBotState moneySubBotState){ this.moneySubBotState = moneySubBotState; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setConnection(Long newConnection) { connection = newConnection; }
    void setUserName(String userName){ this.userName = userName; }
    void setMoney(int money){ this.money = money; }
    void setSuitableId(Long suitableId) { this.suitableId = suitableId; }
    void addMoney(int value){this.money += value;}
    void substractMoney(int value){this.money -= value;}



    BotAttribute(BotState newBotState, Questionary newQuestionary, Long newConnection)
    {
        botState = newBotState;
        questionary = newQuestionary;
        connection = newConnection;
    }




}
