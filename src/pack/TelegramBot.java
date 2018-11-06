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
import java.util.Arrays;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot
{
    private static String botName = "DateSearcherBot";

    private static String botToken =  System.getenv("BOT_TOKEN");

    private final ArrayList<String> normalCommands =  new ArrayList<>(Arrays.asList("/help", "/able", "/disable",
            "/change", "/connect"));

    private final ArrayList<String> connectionCommands =  new ArrayList<>(Arrays.asList("/help",
            "/disconnect"));

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
        switch (dateBot.getBotAttributes().get(chatId).getBotState())
        {
            case CONNECTED:
                setRowButtons(s, connectionCommands);
                break;
            case NORMAL:
                setRowButtons(s, normalCommands);
                break;
            case TALKING_WITH_BOT:
                setRowButtons(s, connectionCommands);
                break;
            case MAKING_QUESTIONARY:
                setInline(s, result.getAnswers());
                break;
            case ASKED_ABOUT_BOT:
                setInline(s, result.getAnswers());
                break;
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

    private void setInline(SendMessage sendMessage, String[] answers)
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

    private String getContentInfo(Message msg)
    {
        String pattern = "\n[Message contained {0}.But bot does not support it]";
        StringBuilder result = new StringBuilder();
        if (msg.hasAnimation())
            result.append(MessageFormat.format(pattern,"animation"));
        if (msg.hasDocument())
            result.append(MessageFormat.format(pattern,"document"));
        if (msg.hasContact())
            result.append(MessageFormat.format(pattern,"contact"));
        if (msg.hasInvoice())
            result.append(MessageFormat.format(pattern,"invoice"));
        if (msg.hasLocation())
            result.append(MessageFormat.format(pattern,"location"));
        if (msg.hasPassportData())
            result.append(MessageFormat.format(pattern,"passport data"));
        if (msg.hasPhoto())
            result.append(MessageFormat.format(pattern,"photo"));
        if (msg.hasSticker())
            result.append(MessageFormat.format(pattern,"sticker"));
        if (msg.hasSuccessfulPayment())
            result.append(MessageFormat.format(pattern,"Successful Payment"));
        return result.toString();
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        Message msg;
        String text;
        Long chatId;
        if (update.hasMessage())
        {
            msg = update.getMessage();
            text = msg.getText();
            chatId = msg.getChatId();
        }
        else
        {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            text = callbackQuery.getData();
            msg = callbackQuery.getMessage();
            chatId = msg.getChatId();
        }

        BotResult result = dateBot.processMessage(chatId, text);
        if (result != null)
        {
            if (dateBot.getBotAttributes().get(chatId).getBotState() == BotState.CONNECTED)
                result.addText(getContentInfo(msg));
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
