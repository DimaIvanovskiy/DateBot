package pack;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;


public class TelegramBot extends TelegramLongPollingBot
{
    private static String botName = "DateSearcherBot";
    private static String botToken = "";

    private DateBot dateBot = new DateBot();

    public static void main(String[] args)
    {
        try
        {
            ApiContextInitializer.init();
            TelegramBotsApi botapi = new TelegramBotsApi();

            TelegramBot bot = new TelegramBot();
            botapi.registerBot(bot);
        }
        catch (TelegramApiException e)
        {
            e.printStackTrace();
        }
    }

    private void sendMsg(Long chatId, String text)
    {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s.setText(text);
        try
        {
            execute(s);
        }
        catch (TelegramApiException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        Message msg = update.getMessage();
        String text = msg.getText();
        Long chatId = msg.getChatId();

        BotResult result = dateBot.processMessage(chatId, text);
        if (result != null)
        {
            String answer = result.getText();
            if (!answer.isEmpty()) {
                ArrayList<Long> chatIds = result.getChatIds();
                for (Long id : chatIds)
                    sendMsg(id, answer);
            }
        }
    }

    @Override
    public String getBotUsername()
    {
        return botName;
    }

    @Override
    public String getBotToken()
    {
        return botToken;
    }
}
