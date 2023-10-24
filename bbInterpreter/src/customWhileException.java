public class customWhileException extends Exception
{
    public customWhileException(String exceptionMessage)
    {
        super(exceptionMessage + "\nPlease ensure your while statement has the correct keywords!");
    }
}