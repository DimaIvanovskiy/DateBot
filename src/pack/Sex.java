package pack;

public enum Sex
{
    MALE(1),
    FEMALE(2),
    MALE_OR_FEMALE(3);

    private final int number;

    private Sex(int number)
    {
        this.number = number;
    }

    public static Sex get(int number)
    {
        for (Sex sex : Sex.values())
            if (sex.number == number)
                return sex;
        return null;
    }

    public int getNumber()
    {
        return number;
    }

    public static Sex get(String str)
    {
        int number = 0;
        try
        {
            number = Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        for (Sex sex : Sex.values())
            if (sex.number == number)
                return sex;
        return null;
    }
}
