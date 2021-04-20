import java.io.File;
import java.nio.file.Files;

public class SymlinkHandling {

    public static boolean checkForSymlink(File f){
        if(Files.isSymbolicLink(f.toPath())){
            return true;
        }
        return false;
    }



    public static boolean createNewSymlink(File link, File target){

        return false;
    }

}
