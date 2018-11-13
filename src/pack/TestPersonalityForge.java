package pack;
import static org.junit.Assert.*;

public class TestPersonalityForge {

    CyberBuddy buddy = new CyberBuddy();

    @org.junit.Test(timeout = 1000)
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
}

