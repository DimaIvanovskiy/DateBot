package pack;


import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;


class ConnectionManager
{
    synchronized static void connect(Long chatId, Long suitableId, Database database, BotAttribute botAttribute)
    {
        BotAttribute pairAttribute = database.getBotAttrubute(suitableId);
        botAttribute.setConnection(suitableId);
        botAttribute .setBotState(BotState.CONNECTED);
        pairAttribute.setConnection(chatId);
        pairAttribute.setBotState(BotState.CONNECTED);
        database.setBotAttribute(pairAttribute, suitableId);
    }

    synchronized static Long disconnect(Long chatId, Database database, BotAttribute botAttribute)
    {
        Long connection = botAttribute.getConnection();
        botAttribute.setBotState(BotState.NORMAL);
        BotAttribute pairAttribute = database.getBotAttrubute(connection);
        pairAttribute.setBotState(BotState.NORMAL);
        pairAttribute.setConnection(connection);
        botAttribute.setConnection(chatId);
        database.setBotAttribute(pairAttribute, connection);
        return connection;
    }

    synchronized static Long findSuitable(Long chatId, Database database,
                              Set<Long> abledUsers)
    {
        if (abledUsers.isEmpty())
            return null;
        Questionary curUser = database.getBotAttrubute(chatId).getQuestionary();
        ArrayList<Long> suitable = new ArrayList<>();
        for (Long id : abledUsers)
        {

            if ( !id.equals(chatId) && curUser.isSuitable(database.getBotAttrubute(id).getQuestionary()))
                suitable.add(id);
        }
        if (suitable.isEmpty())
            return null;
        Random random = new Random();
        return suitable.get(random.nextInt(suitable.size()));
    }
}
