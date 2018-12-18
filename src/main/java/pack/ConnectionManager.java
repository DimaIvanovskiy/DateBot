package pack;


import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;


class ConnectionManager
{
    synchronized static void connect(Long chatId, Long suitableId, Map<Long, BotAttribute> botAttributes)
    {
        BotAttribute attributes = botAttributes.get(chatId);
        BotAttribute pair = botAttributes.get(suitableId);
        attributes.setConnection(suitableId);
        attributes.setBotState(BotState.CONNECTED);
        pair.setConnection(chatId);
        pair.setBotState(BotState.CONNECTED);
    }

    synchronized static Long disconnect(Long chatId, Map<Long, BotAttribute> botAttributes)
    {
        BotAttribute attributes = botAttributes.get(chatId);
        Long connection = attributes.getConnection();
        attributes.setBotState(BotState.NORMAL);
        botAttributes.get(connection).setBotState(BotState.NORMAL);
        botAttributes.get(connection).setConnection(connection);
        attributes.setConnection(chatId);
        return connection;
    }

    synchronized static Long findSuitable(Long chatId, Map<Long, BotAttribute> botAttributes,
                              Set<Long> abledUsers)
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
