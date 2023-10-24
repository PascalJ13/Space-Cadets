public class customDelimiterException extends Exception
{
    public customDelimiterException(String exceptionMessage)
    {
        super(exceptionMessage + "\nPlease check your code to ensure all statements are delimited with a ;");
    }
}