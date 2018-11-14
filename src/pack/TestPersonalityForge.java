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
    public void testFormatResponse() throws Exception
    {
        String response = buddy.getMessage("Hi", 17L,
            Sex.FEMALE, Sex.MALE, "Anya");
        assertTrue(response.matches("a-zA-z"));
        assertFalse(response.matches("[{/\"?*+.]"));
    }

    @org.junit.Test
    public void testJsonFormat() throws Exception
    {
        JSONObject response = buddy.getMessageJson("Hi", 17L,
                Sex.FEMALE, Sex.MALE, "Anya");
        System.out.println(response.toString());

    }
}

