import java.io.PrintStream;

public class Main {

    private static PrintStream standardOut; // This sets the outputs.

    public static void main(String[] args){
        //redirectOutput();
        // GUI.makeGUI();
       //  mockup();
        GetPlatform.whichPlatform();
    }

    public static void redirectOutput(){
        PrintStream printStream = new PrintStream(new CustomOutputStream(GUI.consoleOutput));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

    }

    public static void mockup(){
        print("Delta livery found");
        print("Easyjet livery found");
        print("Conflicting livery -  Whizzair found.");

    }

    public static void printErr(String s){
        System.out.println("-------------------------");
        System.out.println("ERROR:");
        System.out.println(" " + s);
    }

    public static void print(String s){
        System.out.println("  " + s);
    }
}
