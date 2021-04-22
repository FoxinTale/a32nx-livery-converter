import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FindLiveries {


	//static ArrayList<File> aircraftConfigs = new ArrayList<File>();
	//static ArrayList<File> textureConfigs = new ArrayList<File>();


	public static void getInstalledLiveries() {
		ArrayList<String> installedAddons = new ArrayList<>();
		ArrayList<String> baseLiveryPaths = new ArrayList<>();

		ArrayList<String> trueLiveryPaths = new ArrayList<>();
		ArrayList<String> newLiveryPaths = new ArrayList<>();

		ArrayList<String> newLiveryFolders = new ArrayList<>();
		ArrayList<String> oldLiveryFolders = new ArrayList<>();

		ArrayList<File> baseLiveries = new ArrayList<>();
		ArrayList<File> trueLiveries = new ArrayList<>();
		ArrayList<File> newLiveries = new ArrayList<>();

		ArrayList<File> oldSimObjects = new ArrayList<>();
		ArrayList<File> newSimObjects = new ArrayList<>();

		StringBuilder liverySB = new StringBuilder();
		StringBuilder simObjectSB = new StringBuilder();

		installedAddons = scanCommunityFolder(installedAddons);
		baseLiveryPaths = findBaseLiveries(installedAddons, baseLiveryPaths);

		addFilestoList(baseLiveries, baseLiveryPaths);

		for(int i = 0; i < baseLiveries.size(); i++){
			liverySB.append(baseLiveries.get(i).getAbsolutePath());
			oldLiveryFolders.add(liverySB.substring(liverySB.lastIndexOf("\\") + 1, liverySB.length()));
			liverySB.delete(0, liverySB.length());

			if(SymlinkHandling.checkForSymlink(baseLiveries.get(i))){
				// If liveries are symlinked.
				try {
					trueLiveryPaths.add(Files.readSymbolicLink(baseLiveries.get(i).toPath()).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// Liveries are not symlinked.
				trueLiveryPaths.add(baseLiveries.get(i).getAbsolutePath());
			}
		}

		addFilestoList(trueLiveries, trueLiveryPaths);

		for(int i = 0; i < trueLiveryPaths.size(); i++){
			if(!(liverySB.length() == 0)){
				liverySB.delete(0, liverySB.length());
			}

			if(trueLiveryPaths.get(i).contains("Asobo_A320_NEO")){
				liverySB.append(trueLiveryPaths.get(i));
				newLiveryPaths.add(liverySB.toString().replace("Asobo", "FlyByWire"));
				// This is probably the longest singular line of code I have written to date.
				newLiveryFolders.add(liverySB.substring(liverySB.lastIndexOf("\\") + 1, liverySB.length()).replace("Asobo", "FlyByWire").trim());
				liverySB.delete(0, liverySB.length());
			}
		}

		addFilestoList(newLiveries, newLiveryPaths);

		for(int i = 0; i < newLiveries.size(); i++){
			FileOps.copyLivery(trueLiveries.get(i), newLiveries.get(i));
		}

		 oldSimObjects = fillSubFolderList(newLiveryPaths, oldSimObjects, oldLiveryFolders);
		 newSimObjects = fillSubFolderList(newLiveryPaths, newSimObjects, newLiveryFolders);

		for(int i = 0; i < oldSimObjects.size(); i++){
			simObjectSB.append(oldSimObjects.get(i).getAbsolutePath());
			newSimObjects.add(new File(simObjectSB.toString().replace("Asobo", "FlyByWire")));
			simObjectSB.delete(0, simObjectSB.length());
		}

		for(int i = 0; i < oldSimObjects.size(); i++){
			File newObject;
			StringBuilder newPathSB = new StringBuilder();

			if(!oldSimObjects.get(i).exists()){
				newPathSB.append(oldSimObjects.get(i).getAbsolutePath());
				int underscoreIndex = newPathSB.lastIndexOf("_");
				newPathSB.replace(underscoreIndex, underscoreIndex + 1, "-");
				newObject = new File(newPathSB.toString());
				oldSimObjects.set(i, newObject);
			}
		}

		for(int i = 0; i < newSimObjects.size() - 1; i++){
			try {
				FileOps.copySimObjects(oldSimObjects.get(i), newSimObjects.get(i));
			} catch(IndexOutOfBoundsException ignored){

			}
		}
	}


	public static ArrayList<String> scanCommunityFolder(ArrayList<String> contents){
		File packages = new File(GetPlatform.finalInstallPath);
		File communityFolder = null;
		String[] communityContents;
		String communityPath;
		File manifestFile;

		if(packages.exists()) {
			communityFolder = new File(packages.getAbsolutePath() + "\\Community\\");
		} else {
			Main.printErr("Could not find the packages folder. Oops.");
		}

		if(communityFolder.exists()) {
			communityPath =  communityFolder.getAbsolutePath() + "\\";
			communityContents = communityFolder.list();

			for(int i = 0; i < communityContents.length; i++) {
				manifestFile = new File(communityPath + communityContents[i] + "\\manifest.json");
				if (manifestFile.exists()) {
					contents.add(communityPath + communityContents[i]);
					// Has a valid manifest.json file, and is a proper addon / mod.
				} else {
					System.out.println(communityContents[i] + " is missing a 'manifest.json' file, thus will not load properly in sim.");
				}
			}
		} else {
			Main.printErr("Could not find community folder.. How?");
		}
		return contents;
	}


	public static ArrayList<String> findBaseLiveries(ArrayList<String> allItems, ArrayList<String> foundItems){
		for(int i = 0; i < allItems.size(); i++){
			if(allItems.get(i).contains("Asobo_A320_NEO")){
				foundItems.add(allItems.get(i));
			}
		}
		return foundItems;
	}


	// Takes an empty arraylist of files  and populates it with files whose paths come from the arraylist of strings.
	public static ArrayList<File> addFilestoList(ArrayList<File> fileList, ArrayList<String> filePaths){
		for(int i = 0; i < filePaths.size(); i++){
			fileList.add(new File(filePaths.get(i)));
		}
		return fileList;
	}


	public static ArrayList<File> fillSubFolderList(ArrayList<String> mainfolders, ArrayList<File> subfolders, ArrayList<String> folderNames){
		ArrayList<File> liveryFolders = new ArrayList<>();

		for(int i = 0; i < mainfolders.size(); i++) {
			liveryFolders.add(new File( mainfolders.get(i) + "\\SimObjects\\AirPlanes\\"));
		}

		for(int i = 0; i < liveryFolders.size(); i++){
			subfolders.add(new File(liveryFolders.get(i).getAbsolutePath() + "\\" + folderNames.get(i)));
		}
		return subfolders;
	}

}
