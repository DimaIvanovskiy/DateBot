package pack;

class BotAttributes
{
    final Object lock = new Object();
    private Questionary questionary;
    private BotState botState;
    private Long connection;

    Questionary getQuestionary() { return questionary; }
    BotState getBotState() { return botState; }
    Long getConnection() { return connection; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setConnection(Long newConnection) { connection = newConnection; }

    BotAttributes(BotState newBotState, Questionary newQuestionary, Long newConnection)
    {
        botState = newBotState;
        questionary = newQuestionary;
        connection = newConnection;
    }



}
