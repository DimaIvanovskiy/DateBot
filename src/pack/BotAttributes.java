package pack;

public class BotAttributes {
    private Questionary questionary;
    private State state;
    private Long connection;

    public Questionary getQuestionary() { return questionary; }
    public State getState() { return state; }
    public Long getConnection() { return connection; }
    public void setState(State newState) { state = newState; }
    public void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    public void setConnection(Long newConnection) { connection = newConnection; }

    BotAttributes(State newState, Questionary newQuestionary, Long newConnection)
    {
        state = newState;
        questionary = newQuestionary;
        connection = newConnection;
    }



}
