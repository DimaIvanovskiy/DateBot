package pack;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CyberBuddy {
    private final String apiKey = "QDz1wyesabl0da7U";
    private final String apiSecret = "I33deXsRMKjyUKEr0VTWwpB4jeXQE1hr";

    public String getMessage(String message, Long chatId, Sex userSex, Sex coupleSex,
                             String userName) throws Exception
    {
        JSONObject messageObj = new JSONObject();
        URL url = new URL(getRequest(message, chatId, userSex, coupleSex, userName));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            messageObj = new JSONObject(inputLine);
        in.close();
        return messageObj.getJSONObject("message").get("message").toString();
    }

    private JSONObject makeJSON(String message, Long chatId, Sex userSex, Sex coupleSex,
                                String userName)
    {
        String gender = userSex == Sex.MALE ? "m" : "f";
        int chatBotId = coupleSex == Sex.MALE ? 145691 : 155117;
        HashMap messageObject = new HashMap<String, Object>(){{
            put("message", message);
            put("chatBotID", chatBotId);
            put("timestamp", System.currentTimeMillis() / 1000L);
        }};
        HashMap userObject = new HashMap<String, Object>() {{
            put("firstName", userName);
            put("gender", gender);
            put("externalID", chatId);
        }};
        HashMap userInfomration = new HashMap<>() {{
            put("message", messageObject);
            put("user", userObject);
        }};

        JSONObject messageJson = new JSONObject(userInfomration);
        return messageJson;
    }

    private String getRequest(String message, Long chatId, Sex userSex, Sex coupleSex,
                              String userName) throws Exception
    {
        JSONObject messageOb = makeJSON(message, chatId, userSex, coupleSex, userName);
        String hash = getHashHmac(messageOb);
        URIBuilder ub = new URIBuilder("https://www.personalityforge.com/api/chat/?");
        ub.addParameter("apiKey", apiKey);
        ub.addParameter("hash", hash);
        ub.addParameter("message", messageOb.toString());
        String url = ub.toString();
        return url;
    }

    private String getHashHmac(JSONObject value)
    {
        try {
            byte[] keyBytes = apiSecret.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(value.toString().getBytes());
            byte[] hexBytes = new Hex().encode(rawHmac);
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
