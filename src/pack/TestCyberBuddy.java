package pack;
import static org.junit.Assert.*;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;


public class TestCyberBuddy {

    private CyberBuddy buddy = new CyberBuddy();

    private String message = "{\"message\":{\"message\":\"Hi\",\"chatBotID\":155117," +
            "\"timestamp\":1542136352}," +
            "\"user\":{\"firstName\":\"Ann\",\"gender\":\"m\",\"externalID\":123}}";

    @org.junit.Test
    public void testMakeJSON()
    {
        JSONObject messageAsJson = buddy.makeJSON("Hi",
                123L, Sex.MALE, Sex.FEMALE, "Ann");
        JSONObject messageParameters = messageAsJson.getJSONObject("message");
        JSONObject userParameters = messageAsJson.getJSONObject("user");
        assertEquals("Hi", messageParameters.get("message"));
        assertEquals(155117, messageParameters.get("chatBotID"));
        assertEquals("Ann", userParameters.get("firstName"));
        assertEquals("m", userParameters.get("gender"));
        assertEquals(123L, userParameters.get("externalID"));

    }

    @org.junit.Test
    public void testGetHashHmac()
    {
        assertEquals(
                "666fad603f2d79ec3e9b0e8b763829d1bf140ff5",
                buddy.getHashHmac(new JSONObject(message)));
    }

    @org.junit.Test
    public void testGetURL() throws Exception
    {
            URL url = new URL(buddy.getURL("Hi", 123L, Sex.MALE, Sex.FEMALE,
                    "Dmitriy-Ivanovskii"));
            assertEquals("https", url.getProtocol());
            assertEquals("www.personalityforge.com", url.getHost());
            assertEquals("/api/chat/", url.getPath());
            String expectedMessageUrl = URLEncoder.encode(message,
                    "utf-8");
            String query = url.getQuery();
            String actualMessageUrl = query.substring(query.indexOf("message") + 8,
                    query.indexOf("timestamp"));
            assertEquals(expectedMessageUrl.substring(0, expectedMessageUrl.indexOf("timestamp")),
                    actualMessageUrl);
    }
}
