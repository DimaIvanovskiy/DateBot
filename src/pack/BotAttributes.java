package pack;

class BotAttributes
{
    final Object lock = new Object();
    private Questionary questionary;
    private BotState botState;
    private Long connection;
    private String userName;

    Questionary getQuestionary() { return questionary; }
    BotState getBotState() { return botState; }
    Long getConnection() { return connection; }
    String getUserName() { return userName; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setConnection(Long newConnection) { connection = newConnection; }
    void setUserName(String userName){this.userName = userName;}


    BotAttributes(BotState newBotState, Questionary newQuestionary, Long newConnection)
    {
        botState = newBotState;
        questionary = newQuestionary;
        connection = newConnection;
    }



}
