package pack;

import java.util.ArrayList;

public class BotResult
{
    private StringBuilder text = new StringBuilder();

    private ArrayList<Long> chatIds = new ArrayList<>();

    public BotResult(String text,Long ... chatIds)
    {
        this.text.append(text);
        for (Long chatId : chatIds)
        this.chatIds.add(chatId);
    }

    public BotResult(){}

    public void addText(String text)
    {
        this.text.append(text);
    }

    public void addChatId(Long chatId)
    {
        this.chatIds.add(chatId);
    }

    public String getText()
    {
        return text.toString();
    }

    public ArrayList<Long> getChatIds()
    {
        return this.chatIds;
    }

}
