package pack;
import org.json.JSONObject;

import static org.junit.Assert.*;
import java.util.Objects;

public class TestPersonalityForge {

    CyberBuddy buddy = new CyberBuddy();

    @org.junit.Test(timeout = 5000)
    public void testTimeResponse() throws Exception
    {
        String response = buddy.getMessage("Hi", 1L,
                Sex.FEMALE, Sex.MALE, "Ivanovskii");
        assertFalse(Objects.isNull(response));
    }

    @org.junit.Test
<<<<<<< HEAD
    public void testSuccessRequest() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        assertEquals(1, responseAsJson.get("success"));
        assertTrue(Objects.isNull(responseAsJson.get("errorMessage")));
    }

    @org.junit.Test
    public void testRequestStructure() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Test", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        assertTrue(responseAsJson.has("message"));
        assertTrue(responseAsJson.has("data"));
    }


    @org.junit.Test
    public void testFieldsInResponse() throws Exception
=======
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
>>>>>>> 66254acbee55eb537e518bcf1642d9ca12ebdf77
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
<<<<<<< HEAD
        assertTrue(messageInformation.has("message"));
        assertTrue(messageInformation.has("emotion"));
        assertTrue(messageInformation.has("chatBotID"));
        assertTrue(messageInformation.has("chatBotName"));
=======
        assertTrue(messageInformation.get("message").toString().length() != 0);
        assertTrue(messageInformation.get("mood").toString().length() != 0);
>>>>>>> 66254acbee55eb537e518bcf1642d9ca12ebdf77
    }

    @org.junit.Test
    public void testCorrectMessageInformationInResponse() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("How are you?", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertEquals("145691", messageInformation.get("chatBotID"));
        assertEquals("Jimmy Jones", messageInformation.get("chatBotName"));
    }

}

