package pack;


import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConnectionManager
{
    private Set<Long> abledUsers = Sets.newConcurrentHashSet();

    private ConcurrentHashMap<Long, BotAttributes> botAttributes;

    ConnectionManager(ConcurrentHashMap<Long, BotAttributes> botAttributes)
    {
        this.botAttributes = botAttributes;
    }

    BotResult enableConnection(Long chatId, BotState botState)
    {
        BotResult result = new BotResult("", chatId);
        if (botState == BotState.CONNECTED || abledUsers.contains(chatId))
            return result;
        abledUsers.add(chatId);
        result.addText(DateBot.ableReply);
        return result;
    }
    BotResult disableConnection(Long chatId)
    {
        BotResult result = new BotResult("", chatId);
        if (!abledUsers.contains(chatId))
            return result;
        abledUsers.remove(chatId);
        result.addText(DateBot.disableReply);
        return result;
    }

    synchronized BotResult tryConnect(Long chatId, BotAttributes attributes)
    {
        BotResult result = new BotResult("", chatId);
        Long suitable = findSuitable(chatId);
        if (suitable == null)
        {
            result.addText(DateBot.noSuitableQuestionaryReply);
            result.addQuestionAndAnswers(new QuestionAndAnswers("Would you like to talk to " +
                    "our conversation bot?", "yes", "no"));
            attributes.setBotState(BotState.ASKED_ABOUT_BOT);
        }
        else
        {
            abledUsers.remove(chatId);
            abledUsers.remove(suitable);
            attributes.setConnection(suitable);
            botAttributes.get(suitable).setConnection(chatId);
            attributes.setBotState(BotState.CONNECTED);
            botAttributes.get(suitable).setBotState(BotState.CONNECTED);
            result.addChatId(suitable);
            result.addText(DateBot.connectionReply);
        }
        return result;
    }

    synchronized BotResult disconnect(Long chatId, BotState botState, BotAttributes attributes)
    {
        BotResult result = new BotResult("", chatId);
        if (botState == BotState.TALKING_WITH_BOT)
        {
            result.addText(DateBot.botDisconnectionReply);
            return result;
        }
        if (botState != BotState.CONNECTED)
            return result;
        Long connection = attributes.getConnection();
        attributes.setBotState(BotState.NORMAL);
        botAttributes.get(connection).setBotState(BotState.NORMAL);
        botAttributes.get(connection).setConnection(connection);
        attributes.setConnection(chatId);
        result.addChatId(connection);
        result.addText(DateBot.disconnectionReply);
        return result;
    }

    private Long findSuitable(Long chatId)
    {
        if (abledUsers.isEmpty())
            return null;
        Questionary curUser = botAttributes.get(chatId).getQuestionary();
        ArrayList<Long> suitable = new ArrayList<>();
        for (Long id : abledUsers)
        {

            if ( !id.equals(chatId) && curUser.isSuitable(botAttributes.get(id).
                    getQuestionary()))
                suitable.add(id);
        }
        if (suitable.isEmpty())
            return null;
        Random random = new Random();
        return suitable.get(random.nextInt(suitable.size()));
    }
}
