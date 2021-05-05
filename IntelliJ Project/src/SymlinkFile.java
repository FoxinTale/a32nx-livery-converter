import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SymlinkFile {

    public static void makeSymlinkList(ArrayList<File> liveries){
        StringBuilder liveryNameSB = new StringBuilder();
        String liveryName;

        ArrayList<File> liveryTargets = new ArrayList<>();
        ArrayList<File> liveryLinks = new ArrayList<>();

       for(int a = 0; a < liveries.size(); a++){
           liveryName = liveries.get(a).getAbsolutePath();

           if(!liveryName.contains(GetPlatform.finalInstallPath)){ // Indicates a Symlink.
               liveryNameSB.append(liveryName);
               liveryName = liveryNameSB.substring(liveryNameSB.lastIndexOf("\\") + 1, liveryNameSB.length()).trim();
               liveryTargets.add(liveries.get(a));
               liveryLinks.add(new File(GetPlatform.finalInstallPath + "\\Community\\" + liveryName));
           }
           liveryNameSB.delete(0, liveryNameSB.length());
       }
      appendCommands(liveryTargets, liveryLinks);
    }


    public static void appendCommands(ArrayList<File> liveryTargets, ArrayList<File> liveryLinks){
        ArrayList<String> batchContent = new ArrayList<>();
        for(int b = 0; b < liveryTargets.size(); b++){
            batchContent.add("mklink /D \"" + liveryLinks.get(b).getAbsolutePath() + " \"  \"" + liveryTargets.get(b).getAbsolutePath() + "\"");
        }
        System.out.println();
        writeBatchFile(batchContent);
    }


    public static void writeBatchFile(ArrayList<String> batchContents){
        String currentDirectory = System.getProperty("user.dir");
        File batchFile = new File (currentDirectory + "\\MakeSymlinks.bat");

        try {
            FileWriter batchWriter = new FileWriter(batchFile);

            for(int c = 0; c < batchContents.size(); c++){
                batchWriter.write(batchContents.get(c) + "\n");
            }
            batchWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
