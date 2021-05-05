import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
/*
    To do list, in no particular order:
    - Create new symlinks if the livery was simlinked initially.
    - More code documentation.
    - Begin outputting things to the user via GUI so they know what is happening.
    - Add buttons to the GUI for scanning for liveries, and converting.
    - Look for already converted liveries, and its unconverted counterparts, and ignore them.

*/

// I try to follow the rule of "Have each class do one job, and make it do it well".
// As well as writing self documenting code as best I can.
public class Main {
    // This creates and assigns a new printstream for redirecting the console output.
    private static PrintStream standardOut;

    static ArrayList<File> liveries = new ArrayList<>();
    // Good old main.
    public static void main(String[] args) throws IOException {

        //redirectOutput();
        // GUI.makeGUI();
        GetPlatform.whichPlatform();
    }


    // This is how it outputs to the text area in the GUI via standard System.out calls.
    public static void redirectOutput(){
        PrintStream printStream = new PrintStream(new CustomOutputStream(GUI.consoleOutput));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

    }


    // Make an error stand out a bit more.
    public static void printErr(String s){
        System.out.println("-------------------------");
        System.out.println("ERROR:");
        System.out.println(" " + s);
    }

    //Custom print function to add a single space in front.
    // This is so it doesn't look too close to the edge, and cluttered / cramped.
    public static void print(String s){
        System.out.println("  " + s);
    }
}
