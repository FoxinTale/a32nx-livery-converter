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


    public static void copySimObjects(File oldObjects, File newObjects){
      //  System.out.println("Old Path: " + oldObjects.getAbsolutePath());
        //System.out.println("New Path: " + newObjects.getAbsolutePath());
        try {
            FileUtils.copyDirectory(oldObjects, newObjects);
            FileUtils.deleteDirectory(oldObjects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
