package pack;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot
{
    private static String botName = "DateSearcherBot";

    private static String botToken = System.getenv("BOT_TOKEN");

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

    private void sendMsg(Long chatId, String text, BotResult result)
    {
        SendMessage s = new SendMessage();

        if (result.getCurrentCommands().size()!=0)
        {
            setRowButtons(s, result.getCurrentCommands());
        }
        if (result.getAnswers().length!=0)
        {
            setInlineButtons(s, result.getAnswers());
        }

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

    private void setRowButtons(SendMessage sendMessage, ArrayList<String> names)
    {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        for (int i=0;i<names.size();i++)
        {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(names.get(i)));
            keyboard.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void setInlineButtons(SendMessage sendMessage, String[] answers)
    {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        for (int i=0; i<answers.length;i++)
        {
            String answer = answers[i];
            String data = Integer.toString(i+1);

            buttons1.add(new InlineKeyboardButton().setText(answer).setCallbackData(data));
        }
        buttons.add(buttons1);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        sendMessage.setReplyMarkup(markupKeyboard);
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        Message msg;
        String text;
        Long chatId;
        String userName;
        if (update.hasMessage())
        {
            msg = update.getMessage();
            text = msg.getText();
            chatId = msg.getChatId();
            userName = msg.getFrom().getUserName();
        }
        else
        {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            text = callbackQuery.getData();
            msg = callbackQuery.getMessage();
            chatId = msg.getChatId();
            userName = msg.getFrom().getUserName();
        }

        BotResult result = dateBot.processMessage(chatId, text, userName);
        if (result != null)
        {
            String answer = result.getText();
            if (!answer.isEmpty())
            {
                ArrayList<Long> chatIds = result.getChatIds();
                for (Long id : chatIds)
                    sendMsg(id, answer, result);
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
