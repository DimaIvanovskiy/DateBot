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

class Database
{
    private Firestore database;


    public Database()
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

    void setBotAttribute(BotAttribute attribute, Long chatId)
    {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("botState", attribute.getBotState().toString());
        fields.put("money", attribute.getMoney());
        fields.put("connection", attribute.getConnection());
        fields.put("moneySubBotState", attribute.getMoneySubBotState().toString());
        fields.put("rpsState", attribute.getRpsState());
        fields.put("suitableId", attribute.getSuitableId());
        fields.put("userName", attribute.getUserName());
        ApiFuture<WriteResult> future = database.collection("botAttributes")
                .document(chatId.toString())
                .update(fields);
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    public BotAttribute getBotAttrubute(Long chatId)
    {
        Map<String, Object> documentDate = getDocumentData(chatId);
        if (documentDate == null)
            return null;
        return null;
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

    void setQuestionary(Long chatId, Sex userSex, Sex coupleSex)
    {
        HashMap<String, Integer> questionary = new HashMap<>();
        questionary.put("userSex", userSex.getNumber());
        questionary.put("coupleSex", coupleSex.getNumber());
        simpleSet(chatId, questionary, "questionary");
    }

    void setBotState(Long chatId, BotState botState)
    {
        simpleSet(chatId, botState.toString(), "botState");
    }

    Object getBotState(Long chatId)
    {
        Object stringState = simpleGet(chatId, "botState");
        return BotState.valueOf((String)stringState);
    }

    void setConnection(Long chatId, Long suitableId)
    {
        simpleSet(chatId, suitableId, "connection");
    }

    Object getConnection(Long chatId)
    {
        return simpleGet(chatId, "connection");
    }


    void setUserName(Long chatId, String name)
    {
        simpleSet(chatId, name, "userName");
    }

    Object getUserName(Long chatId)
    {
        return simpleGet(chatId, "userName");
    }

    void setMoney(Long chatId, int money)
    {
        simpleSet(chatId, money, "money");
    }


    Object getMoney(Long chatId)
    {
        return simpleGet(chatId, "money");
    }

    void setSuitableId(Long chatId, Long suitableId)
    {
        simpleSet(chatId, suitableId, "suitableId");
    }

    Object getSuitableId(Long chatId)
    {
        return simpleGet(chatId, "suitableId");
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
        HashSet<Long> result = new HashSet<Long>();

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

    private void simpleSet(Long chatId, Object object, String fieldName)
    {
        HashMap<String, Object> field = new HashMap<>();
        field.put(fieldName, object);
        ApiFuture<WriteResult> future = database.collection("botAttributes")
                .document(chatId.toString())
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

    private Object simpleGet(Long chatId, String fieldName)
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
            return document.get(fieldName);
        else
            return null;
    }
}
