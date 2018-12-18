package pack;

import java.util.ArrayList;

public class BotResult
{
    private StringBuilder text = new StringBuilder();

    private ArrayList<Long> chatIds = new ArrayList<>();

    private String[] answers =  new String[0];

    private ArrayList<String> commands = new ArrayList<>();

    BotResult(String text,Long ... chatIds)
    {
        this.text.append(text);
        for (Long chatId : chatIds)
            this.chatIds.add(chatId);
    }

    void addQuestionAndAnswers(QuestionAndAnswers questionAnswers)
    {
        this.text.append(questionAnswers.question);
        this.answers = questionAnswers.answers;
    }

    void addCurrentCommands(ArrayList<String> commands)
    {
        this.commands = commands;
    }

    ArrayList<String> getCurrentCommands()
    {
        return commands;
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
