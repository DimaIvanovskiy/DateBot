package pack;

import java.util.ArrayList;
import java.util.HashSet;

class BotAttribute
{
    private Questionary questionary;
    private BotState botState;
    private int money = DateBot.startMoneyCount;
    private MoneySubBotState moneySubBotState;
    private String rpsState;
    private ArrayList<String> interestQuestions = new ArrayList<>();
    private ArrayList<Long> answeredQuestionIds = new ArrayList();
    private ArrayList<Answer> answers  = new ArrayList<>();
    private String currentQuestion;
    private ArrayList<Long> liked = new ArrayList<>();
    private String userName;

    String getRpsState() { return rpsState; }
    MoneySubBotState getMoneySubBotState() { return moneySubBotState; }
    BotState getBotState() { return botState; }
    Questionary getQuestionary() { return questionary; }
    int getMoney() { return money; }
    ArrayList<String> getInterestQuestions() { return interestQuestions; }
    ArrayList<Long> getAnsweredQuestionIds() { return answeredQuestionIds; }
    ArrayList<Answer> getAnswers() { return answers; }
    String getCurrentQuestion() {return  currentQuestion;}
    ArrayList<Long> getLiked() { return  liked; }
    String getUserName() { return userName; }

    void setRpsState(String rpsState) { this.rpsState = rpsState; }
    void setMoneySubBotState(MoneySubBotState moneySubBotState){ this.moneySubBotState = moneySubBotState; }
    void setBotState(BotState newBotState) { botState = newBotState; }
    void setQuestionary(Questionary newQuestionary) { questionary = newQuestionary; }
    void setMoney(int money){ this.money = money; }
    void setInterestQuestions(ArrayList<String> interestQuestions) { this.interestQuestions = interestQuestions; }
    void setAnsweredQuestionIds(ArrayList<Long> ids) { this.answeredQuestionIds = ids; }
    void setAnswers(ArrayList<Answer> answers) { this.answers = answers; }
    void setCurrentQuestion(String question) { this.currentQuestion = question; }
    void setLiked(ArrayList<Long> liked) {this.liked = liked; }
    void setUserName(String userName) {this.userName = userName; }

    void addLiked(Long id) {this.liked.add(id);}
    void addAnswer(Answer answer) { this.answers.add(answer); }
    void addAnsweredQuestionId(long chatId) { this.answeredQuestionIds.add(chatId); }
    void addMoney(int value){this.money += value;}
    void subtractMoney(int value){this.money -= value;}


    BotAttribute(BotState newBotState, Questionary newQuestionary, String userName)
    {
        botState = newBotState;
        questionary = newQuestionary;
        this.userName = userName;
    }
}
