package pack;
import org.json.JSONObject;
import org.junit.Before;

import static org.junit.Assert.*;

public class TestPersonalityForge {

    CyberBuddy buddy;

    @Before
    public void init()
    {
        buddy = new CyberBuddy();
    }


    @org.junit.Test(timeout = 5000)
    public void testTimeResponse() throws Exception
    {
        buddy.getMessage("Hi", 1L,
                Sex.FEMALE, Sex.MALE, "Ivanovskii");
    }

    @org.junit.Test
    public void testSuccessRequest() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("Hi", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        assertEquals(1, responseAsJson.get("success"));

        assertEquals("",responseAsJson.get("errorMessage") );
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
    public void testCorrectBotOnMaleSexCouple() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("How are you?", 7L, Sex.FEMALE, Sex.MALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertEquals(145691, messageInformation.get("chatBotID"));
        assertEquals("Jimmy Jones", messageInformation.get("chatBotName"));
    }

    @org.junit.Test
    public void testCorrectBotOnFemaleSexCouple() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("How are you?", 7L, Sex.FEMALE, Sex.FEMALE,
                "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertEquals(155117, messageInformation.get("chatBotID"));
        assertEquals("Melody Tulip", messageInformation.get("chatBotName"));
    }

    @org.junit.Test
    public void testCorrectBotOnMaleOrFemaleSexCouple() throws Exception
    {
        JSONObject responseAsJson = buddy.getMessageJson("How are you?", 7L, Sex.FEMALE,
                Sex.MALE_OR_FEMALE, "Anya");
        JSONObject messageInformation = responseAsJson.getJSONObject("message");
        assertEquals(155117, messageInformation.get("chatBotID"));
        assertEquals("Melody Tulip", messageInformation.get("chatBotName"));
    }
}