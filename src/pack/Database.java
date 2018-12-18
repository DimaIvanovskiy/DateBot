package pack;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.toIntExact;

class Database
{
    private Firestore database;


    Database()
    {
        String path ="C:\\Users\\Дима\\Desktop\\Key.json";
        try
        {
            FileInputStream serviceAccount = new FileInputStream(path);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://datebot-9b168.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);
            database = FirestoreClient.getFirestore();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    boolean botAttributesContains(Long chatId)
    {
        return getDocumentData(chatId) != null;
    }

    void setBotAttribute(BotAttribute attribute, Long chatId)
    {
        HashMap<String, Object> fields = formFields(attribute);
        ApiFuture<WriteResult> future = database.collection("botAttributes")
                .document(chatId.toString())
                .set(fields);
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    void createBotAttribute(BotAttribute attribute, Long chatId)
    {
        HashMap<String, Object> fields = formFields(attribute);
        ApiFuture<WriteResult> future = database.collection("botAttributes")
                .document(chatId.toString())
                .create(fields);
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }
     BotAttribute getBotAttrubute(Long chatId)
    {
        Map<String, Object> documentData = getDocumentData(chatId);
        if (documentData == null)
            return null;
        Questionary questionary = formQuestionary(documentData);
        BotState botState = BotState.valueOf((String) documentData.get("botState"));

        BotAttribute botAttribute = new BotAttribute(botState, questionary);
        botAttribute.setRpsState((String)documentData.get("rpsState"));
        String strMoneySybBotState = (String)documentData.get("moneySubBotState");
        botAttribute.setMoneySubBotState(strMoneySybBotState == null ? null :
                MoneySubBotState.valueOf(strMoneySybBotState));
        botAttribute.setMoney(toIntExact((Long)documentData.get("money")));
        return botAttribute;
    }

    private Questionary formQuestionary(Map<String, Object> documentData)
    {
        Map<String, Object> mapQuestionary = (Map<String, Object>) documentData.get("questionary");
        Questionary questionary = new Questionary();
        String strUserSex = (String)mapQuestionary.get("userSex");
        questionary.userSex = strUserSex == null? null : Sex.valueOf(strUserSex);
        String strCoupleSex = (String)mapQuestionary.get("coupleSex");
        questionary.coupleSex = strCoupleSex == null? null : Sex.valueOf(strCoupleSex);
        questionary.setNumber(toIntExact((Long)mapQuestionary.get("questionNumber")));
        return questionary;
    }

    private HashMap<String, Object> formFields(BotAttribute attribute)
    {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("botState", attribute.getBotState().toString());
        fields.put("money", attribute.getMoney());

        MoneySubBotState moneySubBotState = attribute.getMoneySubBotState();
        fields.put("moneySubBotState", moneySubBotState==null? null : moneySubBotState.toString());
        fields.put("rpsState", attribute.getRpsState());

        Questionary questionary = attribute.getQuestionary();
        HashMap<String, Object> questionaryField = new HashMap<>();
        questionaryField.put("userSex", questionary.userSex == null ? null : questionary.userSex.toString());
        questionaryField.put("coupleSex", questionary.coupleSex == null ? null : questionary.coupleSex.toString());
        questionaryField.put("questionNumber", questionary.getNumber());

        fields.put("questionary", questionaryField);
        return fields;
    }

    private Map<String, Object> getDocumentData(Long chatId)
    {
        DocumentReference docRef = database.collection("botAttributes").document(chatId.toString());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try
        {
            document = future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        if (document!= null && document.exists())
            return document.getData();
        else
            return null;
    }
}
