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
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertTrue(messageInformation.has("message"));
        assertTrue(messageInformation.has("emotion"));
        assertTrue(messageInformation.has("chatBotID"));
        assertTrue(messageInformation.has("chatBotName"));
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

