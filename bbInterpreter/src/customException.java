public class customException extends  Exception
{
    public customException(String exceptionMessage)
    {
        super(exceptionMessage + "\nPlease check your code to ensure all statements are delimited with a ;");
    }
}
