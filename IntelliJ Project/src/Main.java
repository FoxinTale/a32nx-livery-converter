import java.io.IOException;
import java.io.PrintStream;


// I try to follow the rule of "Have each class do one job, and make it do it well".
// As well as writing self documenting code as best I can.
public class Main {
    // This creates and assigns a new printstream for redirecting the console output.
    private static PrintStream standardOut;

    // Good old main.
    public static void main(String[] args) throws IOException {
        //redirectOutput();
        // GUI.makeGUI();
       //  mockup();
        GetPlatform.whichPlatform();
    }

    // This is how it outputs to the text area in the GUI via standard System.out calls.
    public static void redirectOutput(){
        PrintStream printStream = new PrintStream(new CustomOutputStream(GUI.consoleOutput));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

    }

    //temporary. For testing purposes only.
    public static void mockup(){
        print("Delta livery found");
        print("Easyjet livery found");
        print("Conflicting livery -  Whizzair found.");

    }

    // Make an error stand out a bit more.
    public static void printErr(String s){
        System.out.println("-------------------------");
        System.out.println("ERROR:");
        System.out.println(" " + s);
    }

    //Custom print function to add a single space in front.
    // This is so it doesn't look too close to the edge, and cluttered/ cramped.
    public static void print(String s){
        System.out.println("  " + s);
    }
}
