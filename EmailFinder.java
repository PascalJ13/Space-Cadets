import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.net.*;

class EmailFinder
{
    public static void main(String[] args) throws Exception
    {
        //Gets the website URL associated with the ID
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Please enter the email ID: ");
        String id = keyboard.nextLine();
        URL newUrl = new URL("https://www.ecs.soton.ac.uk/people/" + id);
        BufferedReader web = new BufferedReader(new InputStreamReader(newUrl.openStream()));  
        
        //Finds name of person associated to ID
        String nameLine = null;
        do
        {
            String tempLine = web.readLine();
            if (tempLine != null) //Ensures the String isn't null when reading length
            {
                if (tempLine.length() >= 25) //Ensures it's long enough for the substring to be valid
                {
                    if (tempLine.substring(19, 24).equals("title")) //Checks for the correct line containing the name
                    {
                        nameLine = tempLine;
                    }
                }
            }
        }
        while (nameLine == null); //Loops until nameLine set to a value (correct line found)
        String name = nameLine.substring(35, nameLine.length()-4);
        System.out.println(name);
        web.close();
        keyboard.close();
    }
}