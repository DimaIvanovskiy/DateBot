package pack;


public class MethodsForTests {

    public BotResult setSexAndCouple(DateBot datebot, Long chatId, String userSex, String coupleSex)
    {
        BotResult result = datebot.processMessage(chatId, "/start");
        result = datebot.processMessage(chatId, userSex);
        result = datebot.processMessage(chatId, coupleSex);
        return result;
    }

    public BotResult getResultOfConnect(DateBot datebot, Long chatId, DateBot suitableBot, Long suitableId,
                                        String userSex, String coupleSex, String secondUserSex,
                                        String secondCoupleSex)
    {
        BotResult result = getStartMessage(datebot, chatId);
        result = setSexAndCouple(datebot, chatId, userSex, coupleSex);
        result = datebot.processMessage(chatId, "/able");
        BotResult resultForSuitable = getStartMessage(suitableBot, suitableId);
        resultForSuitable = setSexAndCouple(suitableBot, suitableId, secondUserSex, secondCoupleSex);
        resultForSuitable = suitableBot.processMessage(suitableId, "/able");
        resultForSuitable = suitableBot.processMessage(suitableId, "/connect");
        return resultForSuitable;
    }

    public BotResult getStartMessage(DateBot datebot, Long chatId)
    {
        BotResult result = datebot.processMessage(chatId, "/start");
        return result;
    }
}
