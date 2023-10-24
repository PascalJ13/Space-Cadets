import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter
{
    HashMap<String, Integer> variables = new HashMap<String, Integer>(); //Name, value
    ArrayList<String> code = new ArrayList<String>(); //Stores each statement as an element
    Stack<Integer> whileStack = new Stack<Integer>(); //Stores line num of start of while statement


    public static void main(String[] args) throws customDelimiterException {
        //Get program file
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the name of the program file: ");
        String fileName = "src/" + keyboard.nextLine() + ".txt";

        Interpreter bbInt = new Interpreter(); //Instantiate interpreter object
        bbInt.code = bbInt.statementGen(fileName); //Get code as arraylist
        bbInt.interpret(bbInt.code);
        keyboard.close();
    }

    void interpret(ArrayList<String> code)
    {
        int lineNum = 0;
        boolean skip = false; //Handles skipping code when a while finishes
        String skipType = ""; //Determines whether an if skip or while skip is in place (slightly different rules)
        int skipCount = 1; //Counts how many end statements need to be skipped
        boolean skipElse = false; //If the if statement is successful then the next else is skipped
        while (lineNum < code.size()) //Keeps going until the last command is executed
        {
            String[] words = code.get(lineNum).split("\\s+");
            if (!skip) //Coding running as usual
            {
                System.out.println("Line " + (lineNum+1) + ": " + code.get(lineNum));
                switch(words[0])
                {
                    case "clear":
                        clear(words[1]);
                        break;
                    case "incr":
                        incr(words[1]);
                        break;
                    case "decr":
                        decr(words[1]);
                        break;
                    case "while":
                        try
                        {
                            if (!wh1le(words)) //End of loop
                            {
                                skip = true; //Skips the code of the while loop
                                skipType = "while"; //Set skip type, mainly for the if logic
                                skipCount = 1; //1 end statement needs to be skipped minimum
                            }
                            else
                            {
                                whileStack.push(lineNum); //Loop begins so push to stack
                            }
                        }
                        catch (customWhileException | ArrayIndexOutOfBoundsException e) //Custom while exception
                        {
                                System.out.println(e);
                                skip = true; //Skips code as while is invalid
                                skipType = "while"; //Set skip type, mainly for the if logic
                                skipCount = 1;  //1 end statement needs to be skipped minimum
                        }
                        break;
                    case "if":
                        if (!iF(words[2], words[4], words[3], words[1])) //If not valid
                        {
                            skip = true; //Skips the code of the if statement
                            skipType = "if"; //Set skip type, to handle else logic
                            skipCount = 1; //1 end statement needs to be skipped minimum
                        }
                        else //The next else should be skipped
                        {
                            skipElse = true;
                        }
                        break;
                    case "endwhile":
                        lineNum = whileStack.pop()-1;
                        break;
                    case "else":
                        if (skipType.equals("if")) //Ensures an if statement was skipped prior to this to run
                        {
                            skipType = ""; //Reset type
                        }
                        else if (skipElse) //If statement was successful before this
                        {
                            skip = true; //Skip the else statement
                            skipType = "else";
                            skipCount = 1;
                        }
                        else
                        {
                            System.out.println("Error, an else statement requires a prior if statement to function!");
                        }
                }
                System.out.println(variables);
            }
            else //Skipping to line after next end
            {
                if (words[0].equals("while") || words[0].equals("if")) //nested if statements and while loops also skipped
                {
                    skipCount++;
                }
                else if (words[0].equals("endwhile") || words[0].equals("endif")) //Successfully skipped over an end statement
                {
                    skipCount--;
                    if (skipCount == 0)
                    {
                        skip = false; //Stops skipping
                    }
                }
                else if (words[0].equals("else") && skipType.equals("if") && skipCount == 1) //End skip if non nested and it is an if skip type
                {
                    skipCount--;
                    skip = false; //Stops skipping
                    lineNum--; //So else statement can be detected
                }
            }

            lineNum++;

        }
    }

    void clear(String varName)
    {
        variables.put(varName, 0);
    }

    void incr(String varName)
    {
        if (variables.containsKey(varName)) //Ensure variable exists
        {
            variables.put(varName, variables.get(varName) + 1);
        }
        else
        {
            System.out.println("Var not found!");
        }
    }

    void decr(String varName)
    {
        if (variables.containsKey(varName)) //Ensure variable exists
        {
            if (variables.get(varName) == 0) //Cannot get negative number
            {
                System.out.println(varName + " cannot be decremented as it will be negative!");
            }
            else //If >= 1 it can be decremented
            {
                variables.put(varName, variables.get(varName) - 1);
            }
        }
    }

    boolean wh1le(String[] words) throws customWhileException//returns true if statement holds
    {
        System.out.println("TESTT" + words.length)
        if (variables.containsKey(words[1])) //Ensures the variable exists
        {
            if (words[2].equals("not") && words[4].equals("do")) //Ensures correct keywords present
            {
                if (variables.get(words[1]) != Integer.parseInt(words[3])) //Statement holds
                {
                    return true;
                }
                else //Statement no longer holds
                {
                    return false;
                }
            }
            else
            {
                throw new customWhileException("Keywords missing for while statement!");
            }
        }
        else
        {
            throw new customWhileException("Var not found!");
        }
    }

    boolean iF(String notKeyword, String doKeyword, String val, String varName) //returns true if statement holds
    {
        if (variables.containsKey(varName)) //Ensures the variable exists
        {
            if (notKeyword.equals("not") && doKeyword.equals("do")) //Ensures correct keywords present
            {
                if (variables.get(varName) != Integer.parseInt(val)) //Statement holds
                {
                    return true;
                }
                else //Statement no longer holds
                {
                    return false;
                }
            }
            else
            {
                System.out.println("Keywords missing for if statement!");
                return false;
            }
        }
        else
        {
            System.out.println("Var not found!");
            return false;
        }
    }

    ArrayList<String> statementGen(String fileName) throws customDelimiterException//Separates the code by the ; into different elements, removes comments and unnecessary whitespace
    {
        ArrayList<String> code = new ArrayList<String>();
        try
        {
            Scanner fileReader = new Scanner(new File(fileName));
            String temp = ""; //Holds code until next ; found
            while (fileReader.hasNextLine()) //Reads through whole file
            {
                String codeLine = temp + fileReader.nextLine(); //Gets the next bit of code
                codeLine = removeComments(codeLine); //Removes comments from the code

                if (codeLine.contains(";")) //Ensures there is a finished statement on the line
                {
                    while (codeLine.contains(";")) //Separates into statements until there are no ; left in the line
                    {

                        code.add(codeLine.substring(0, codeLine.indexOf(";")).replaceFirst("^\\s*", "")); //adds the code to the arraylist, also removes indents
                        temp = "";
                        codeLine = codeLine.substring(codeLine.indexOf(";")+1, codeLine.length());
                        //System.out.println("New codeLine is: " + codeLine);
                    }
                    temp = codeLine; //Remaining code added to temp
                }
                else //Statement doesn't end on the line
                {
                    temp += codeLine; //Add that line to temp
                }
            }
            if (!temp.isBlank()) //Missing ;
            {
                throw new customDelimiterException("Missing ;"); //Throw exception for missing ;
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) //File not found
        {
            System.out.println("Error, file not found.");
        }

        return code;
    }

    String removeComments(String codeLine) //Removes comments from the code to be interpreted
    {
        if (codeLine.contains("//")) //Comment in line
        {
            String comment = codeLine.substring(codeLine.indexOf("//"));
            String line = codeLine.substring(0, codeLine.indexOf("//"));
            //System.out.println("Comment is: " + comment + " Code is: " + line);
            return line;
        }
        else
        {
            return codeLine;
        }
    }
}
