import java.io.File;
import java.io.IOException;
import FileUtils.FileUtils;

public class FileOps {


    public static void copyLivery(File oldLivery, File newLivery){

        try {
            FileUtils.copyDirectory(oldLivery, newLivery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copySimObjects(File oldObject, File newObject){
        try {
            FileUtils.copyDirectory(oldObject, newObject);
            FileUtils.deleteDirectory(oldObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
