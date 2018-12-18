package pack;

import java.util.ArrayList;

class BotAttribute
{
    private Questionary questionary;
    private BotState botState;
    private int money = DateBot.startMoneyCount;
    private MoneySubBotState moneySubBotState;
    private String rpsState;
    private ArrayList<String> interestQuestions = new ArrayList<>();

    String getRpsState() { return rpsState; }
    MoneySubBotState getMoneySubBotState() { return moneySubBotState; }
    BotState getBotState() { return botState; }
    Questionary getQuestionary() { return questionary; }
    int getMoney() { return money; }
    ArrayList<String> getInterestQuestions() { return interestQuestions; }

    void setRpsState(String rpsState) { this.rpsState = rpsState; }
    void setMoneySubBotState(MoneySubBotState moneySubBotState){ this.moneySubBotState = moneySubBotState; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setMoney(int money){ this.money = money; }
    void setInterestQuestions(ArrayList<String> interestQuestions) { this.interestQuestions = interestQuestions; }

    void addMoney(int value){this.money += value;}
    void subtractMoney(int value){this.money -= value;}


    BotAttribute(BotState newBotState, Questionary newQuestionary)
    {
        botState = newBotState;
        questionary = newQuestionary;
    }
}
