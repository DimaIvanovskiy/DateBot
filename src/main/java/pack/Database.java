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
        Long connection = (Long) documentData.get("connection");

        BotAttribute botAttribute = new BotAttribute(botState, questionary, connection);
        botAttribute.setRpsState((String)documentData.get("rpsState"));
        String strMoneySybBotState = (String)documentData.get("moneySubBotState");
        botAttribute.setMoneySubBotState(strMoneySybBotState == null ? null :
                MoneySubBotState.valueOf(strMoneySybBotState));
        botAttribute.setMoney(toIntExact((Long)documentData.get("money")));
        botAttribute.setSuitableId((Long) documentData.get("suitableId"));
        botAttribute.setUserName((String) documentData.get("userName"));
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
        fields.put("connection", attribute.getConnection());

        MoneySubBotState moneySubBotState = attribute.getMoneySubBotState();
        fields.put("moneySubBotState", moneySubBotState==null? null : moneySubBotState.toString());
        fields.put("rpsState", attribute.getRpsState());
        fields.put("suitableId", attribute.getSuitableId());
        fields.put("userName", attribute.getUserName());

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

    void addAbledUser(Long chatId)
    {
        HashMap<String, Object> field = new HashMap<>();

        field.put("abledUsers." + chatId, true);
        ApiFuture<WriteResult> future = database.collection("abledUsers")
                .document("abledUsers")
                .update(field);
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    void removeAbledUser(Long chatId)
    {
        HashMap<String, Object> field = new HashMap<>();

        field.put("abledUsers." + chatId, FieldValue.delete());
        ApiFuture<WriteResult> future = database.collection("abledUsers")
                .document("abledUsers")
                .update(field);
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    Boolean abledUsersContains(Long chatId)
    {
        DocumentReference docRef = database.collection("abledUsers").document("abledUsers");
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
            return document.get("abledUsers." + chatId) != null;
        return false;
    }

    Set<Long> getAbledUsers()
    {
        HashSet<Long> result = new HashSet<>();

        HashMap<String, Boolean> mappedUsers = getMappedAbledUsers();
        if (mappedUsers == null)
            return null;
        for (String key: mappedUsers.keySet())
        {
            result.add(Long.parseLong(key));
        }
        return result;
    }

    private HashMap<String, Boolean> getMappedAbledUsers()
    {
        DocumentReference docRef = database.collection("abledUsers").document("abledUsers");
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
            return (HashMap<String, Boolean>)document.get("abledUsers");
        else
            return null;
    }

}
