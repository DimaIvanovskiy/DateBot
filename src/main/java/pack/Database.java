package pack;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.toIntExact;

class Database
{
    private Firestore database;

    Database()
    {
        try
        {
            var decodedCredentials = new ByteArrayInputStream(Base64.getDecoder().decode(System.getenv("FIREBASE_BASE64_ENCODED_KEY")));
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(decodedCredentials))
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
        addChatId(chatId);
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

    BotAttribute getBotAttribute(Long chatId)
    {
        Map<String, Object> documentData = getDocumentData(chatId);
        if (documentData == null)
            return null;
        Questionary questionary = formQuestionary(documentData);
        BotState botState = BotState.valueOf((String) documentData.get("botState"));
        String userName = (String) documentData.get("userName");

        BotAttribute botAttribute = new BotAttribute(botState, questionary, userName);
        botAttribute.setRpsState((String)documentData.get("rpsState"));
        String strMoneySybBotState = (String)documentData.get("moneySubBotState");
        botAttribute.setMoneySubBotState(strMoneySybBotState == null ? null :
                MoneySubBotState.valueOf(strMoneySybBotState));
        botAttribute.setMoney(toIntExact((Long)documentData.get("money")));
        botAttribute.setCurrentQuestion((String)documentData.get("currentQuestion"));
        botAttribute.setInterestQuestions((ArrayList<String>) documentData.get("interestQuestions"));
        botAttribute.setAnsweredQuestionIds((ArrayList<Long>)documentData.get("answeredQuestionIds"));
        botAttribute.setAnswers(formAnswers(documentData));
        botAttribute.setLiked((ArrayList<Long>)documentData.get("liked"));
        return botAttribute;
    }

    private void addChatId(Long chatId)
    {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("chatIds", FieldValue.arrayUnion(chatId));
        ApiFuture<WriteResult> future = database.collection("botAttributes")
                .document("information")
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

    ArrayList<Long> getSuitableIds(BotAttribute botAttribute, Long chatId)
    {
        ArrayList<Long> suitableIds = new ArrayList<>();
        ArrayList<Long> answeredQuestionsIds = botAttribute.getAnsweredQuestionIds();
        for (Query query: getSuitableQueries(botAttribute.getQuestionary()))
        {
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            try
            {
                for (DocumentSnapshot document : querySnapshot.get().getDocuments())
                {
                    Long id = Long.parseLong(document.getId());
                    if (!answeredQuestionsIds.contains(id) && !id.equals(chatId))
                        suitableIds.add(id);
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        return suitableIds;
    }

    private ArrayList<Query> getSuitableQueries(Questionary questionary)
    {
        ArrayList<Query> suitableQueries = new ArrayList<>();
        CollectionReference attributes = database.collection("botAttributes");
        Query query1 = attributes.whereEqualTo("questionary.coupleSex", questionary.userSex);
        Query query2 = attributes.whereEqualTo("questionary.coupleSex", Sex.MALE_OR_FEMALE);
        if (questionary.coupleSex == Sex.MALE_OR_FEMALE)
        {
            suitableQueries.add(query1);
            suitableQueries.add(query2);
        }
        else
        {
            suitableQueries.add(query1.whereEqualTo("questionary.userSex", questionary.coupleSex));
            suitableQueries.add(query2.whereEqualTo("questionary.userSex", questionary.coupleSex));
        }
        return suitableQueries;
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

    private ArrayList<Answer> formAnswers(Map<String, Object> documentData)
    {
        Map<String, Object> mapAnswers = (HashMap<String, Object>) documentData.get("answers");
        ArrayList<Answer> answers = new ArrayList<>();
        for (String id : mapAnswers.keySet()) {
            Map<String, String> fields = (HashMap<String, String>) mapAnswers.get(id);
            String question = fields.get("question");
            String answer = fields.get("answer");
            answers.add(new Answer(question, answer, Long.parseLong(id)));
        }
        return answers;
    }

    private HashMap<String, Object> formFields(BotAttribute attribute)
    {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("botState", attribute.getBotState().toString());
        fields.put("money", attribute.getMoney());

        MoneySubBotState moneySubBotState = attribute.getMoneySubBotState();
        fields.put("moneySubBotState", moneySubBotState==null? null : moneySubBotState.toString());
        fields.put("rpsState", attribute.getRpsState());
        fields.put("interestQuestions", attribute.getInterestQuestions());
        fields.put("answeredQuestionIds", new ArrayList<>(attribute.getAnsweredQuestionIds()));
        fields.put("currentQuestion", attribute.getCurrentQuestion());
        fields.put("liked", attribute.getLiked());
        fields.put("userName", attribute.getUserName());

        Questionary questionary = attribute.getQuestionary();
        HashMap<String, Object> questionaryField = new HashMap<>();
        questionaryField.put("userSex", questionary.userSex == null ? null : questionary.userSex.toString());
        questionaryField.put("coupleSex", questionary.coupleSex == null ? null : questionary.coupleSex.toString());
        questionaryField.put("questionNumber", questionary.getNumber());

        ArrayList<Answer> answers = attribute.getAnswers();
        HashMap<String, HashMap<String, String>> answersField = new HashMap<>();
        for (Answer answer : answers)
        {
            HashMap<String, String> answerMap = new HashMap<>();
            answerMap.put("question", answer.question);
            answerMap.put("answer", answer.answer);
            answersField.put(answer.id.toString(), answerMap);
        }

        fields.put("answers", answersField);
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
