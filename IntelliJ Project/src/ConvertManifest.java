import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvertManifest {
    static String manifest;

    public static void readManifest(File livery){

        File manifestFile = new File (livery.getAbsolutePath() + "\\manifest.json");
        manifest = manifestFile.getAbsolutePath();
        String currentLine;
        ArrayList<String> manifestContents = new ArrayList<>();

        try {
            Scanner manifestReader = new Scanner(manifestFile);

            while(manifestReader.hasNext()) {
                currentLine = manifestReader.nextLine();
                manifestContents.add(currentLine);
            }

            manifestReader.close();
            manifestFile.delete();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        editManifest(manifestContents);

    }

    public static void editManifest(ArrayList<String> manifestContents){
        int titleIndex = 0;
        StringBuilder titleSB = new StringBuilder();

        for(int a = 0; a < manifestContents.size(); a++){
            if(manifestContents.get(a).contains("title")){
                titleIndex = a;
            }
        }
        titleSB.append(manifestContents.get(titleIndex));

        if(titleSB.indexOf("A32NX") >= 0){
            Main.print("Manifest file has already been converted.");
        } else{
            titleSB.delete(titleSB.lastIndexOf("\""), titleSB.length());
            titleSB.append(" (A32NX Converted)\",");
            manifestContents.set(titleIndex, titleSB.toString());
        }
        titleSB.delete(0, titleSB.length());
        writeManifestFile(manifestContents);
    }


    public static void writeManifestFile(ArrayList<String> manifestContents){
        File newManifestFile = new File(manifest);
        try {
            FileWriter manifestWriter = new FileWriter(newManifestFile);

            for(int i = 0; i < manifestContents.size(); i++) {
                manifestWriter.write(manifestContents.get(i) + "\n");
            }
            manifestWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
