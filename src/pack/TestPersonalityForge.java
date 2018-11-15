package pack;
import org.json.JSONObject;

import static org.junit.Assert.*;

public class TestPersonalityForge {

    CyberBuddy buddy = new CyberBuddy();

    @org.junit.Test(timeout = 5000)
    public void testTimeResponse() throws Exception
    {
        String response = buddy.getMessage("Hi", 1L,
                Sex.FEMALE, Sex.MALE, "Ivanovskii");
        assertTrue(response.contains("Hi"));
    }

    @org.junit.Test
    public void testUserInformationInResponse() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject userInformation = responseAsJson.getJSONObject("user");
        assertEquals("Anya", userInformation.get("firstName").toString());
        assertEquals("f", userInformation.get("gender").toString());
        assertEquals("7L", userInformation.get("externalID").toString());
    }

    @org.junit.Test
    public void testSuccessRequest() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertEquals(1, messageInformation.get("success"));
        assertEquals("", messageInformation.get("errorMessage"));
    }

    @org.junit.Test
    public void testMessageInformationInResponse() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertTrue(messageInformation.get("message").toString().length() != 0);
        assertTrue(messageInformation.get("mood").toString().length() != 0);
    }
}

