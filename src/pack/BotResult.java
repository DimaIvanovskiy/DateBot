package pack;

import java.util.ArrayList;

public class BotResult
{
    private StringBuilder text = new StringBuilder();

    private ArrayList<Long> chatIds = new ArrayList<>();

    private String[] answers =  new String[0];

    BotResult(String text,Long ... chatIds)
    {
        this.text.append(text);
        for (Long chatId : chatIds)
        this.chatIds.add(chatId);
    }

    public BotResult(QuestionAndAnswers questionAnswers)
    {
        addQuestionAndAnswers(questionAnswers);
    }

    void addQuestionAndAnswers(QuestionAndAnswers questionAnswers)
    {
        this.text.append(questionAnswers.question);
        this.answers = questionAnswers.answers;
    }

    BotResult(){}

    void addText(String text)
    {
        this.text.append(text);
    }

    void addChatId(Long chatId)
    {
        this.chatIds.add(chatId);
    }

    String getText()
    {
        return text.toString();
    }

    ArrayList<Long> getChatIds()
    {
        return this.chatIds;
    }

   String[] getAnswers()
    {
        return this.answers;
    }
}
