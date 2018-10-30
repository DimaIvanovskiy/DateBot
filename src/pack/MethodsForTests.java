package pack;


public class MethodsForTests {

    static BotResult setSexAndCouple(DateBot datebot, Long chatId, Sex userSex, Sex coupleSex)
    {
        StartDateBot(datebot, chatId);
        datebot.processMessage(chatId, Integer.toString(userSex.getNumber()));
        BotResult result = datebot.processMessage(chatId, Integer.toString(coupleSex.getNumber()));
        return result;
    }

    static BotResult getResultOfConnect(DateBot dateBot, Long chatId, Long suitableId,
                                        Sex userSex, Sex coupleSex, Sex secondUserSex,
                                        Sex secondCoupleSex)
    {
        StartDateBot(dateBot, chatId);
        setSexAndCouple(dateBot, chatId, userSex, coupleSex);

        StartDateBot(dateBot, suitableId);
        setSexAndCouple(dateBot, suitableId, secondUserSex, secondCoupleSex);

        dateBot.processMessage(chatId, "/able");

        BotResult result = dateBot.processMessage(suitableId,"/connect");
        return result;

    }

    static boolean questionariesAreSuitable(Sex userSex, Sex coupleSex,
                                                Sex secondUserSex, Sex secondCoupleSex)
    {
        Questionary questionary1 = new Questionary();
        questionary1.coupleSex = coupleSex;
        questionary1.userSex = userSex;
        Questionary questionary2 = new Questionary();
        questionary2.coupleSex = secondCoupleSex;
        questionary2.userSex = secondUserSex;
        return questionary1.isSuitable(questionary2);
    }

    static BotResult StartDateBot(DateBot datebot, Long chatId)
    {
        BotResult result = datebot.processMessage(chatId, "/start");
        return result;
    }
}
