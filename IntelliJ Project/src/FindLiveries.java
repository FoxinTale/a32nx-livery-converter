import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class FindLiveries {


	//static ArrayList<File> aircraftConfigs = new ArrayList<File>();
	//static ArrayList<File> textureConfigs = new ArrayList<File>();

	public static void getInstalledLiveries(){
		ArrayList<String> allAddons = new ArrayList<>();
		ArrayList<String> simObjectFolderNames = new ArrayList<>();

		ArrayList<File> addonFiles = new ArrayList<>();
		ArrayList<File> simobjects = new ArrayList<>();
		ArrayList<File> aircraftConfigs = new ArrayList<>();
		ArrayList<File> installedLiveries;

		ArrayList<File> newLiveries = new ArrayList<>();


		StringBuilder nameSB = new StringBuilder();

		File aircraft;
		String[] contents;
		String path;

		allAddons = scanCommunityFolder(allAddons);
		addFilestoList(addonFiles, allAddons);

		// Handling symlinks like a boss.
		for(int a = 0; a < addonFiles.size(); a++){ //Each letter shows how many for loops I use here.
			if(SymlinkHandling.checkForSymlink(addonFiles.get(a))) {
				try {
					addonFiles.set(a, new File(Files.readSymbolicLink(addonFiles.get(a).toPath()).toString()));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		for(int b = 0; b < addonFiles.size(); b++){
			aircraft = new File(addonFiles.get(b).getAbsolutePath() + "\\SimObjects\\Airplanes\\");

			if(aircraft.exists()){ //If this folder doesn't exist, then it's not an aircraft or a livery.
				simobjects.add(aircraft);
			}
		}

		for(int c = 0; c < simobjects.size(); c++){
			contents = simobjects.get(c).list();
			simObjectFolderNames.add(contents[0]);
			simobjects.set(c, new File(simobjects.get(c).getAbsolutePath() + "\\" + contents[0]));
		}

		for(int d = 0; d < simobjects.size(); d++){
			aircraftConfigs.add(new File(simobjects.get(d).getAbsolutePath() + "\\aircraft.cfg"));
		}


		installedLiveries = checkIfOldLivery(aircraftConfigs);
		simobjects.clear();

		for(int e = 0; e < installedLiveries.size(); e++){
			nameSB.append(installedLiveries.get(e));
			installedLiveries.set(e, new File(nameSB.substring(0, nameSB.indexOf("SimObjects"))));
			simobjects.add(new File(nameSB.substring(0, nameSB.lastIndexOf("\\"))));
			nameSB.delete(0, nameSB.length());
		}

		//printFileList(simobjects);
		//System.out.println("\n");
		//printFileList(installedLiveries);
		makeNewPaths(installedLiveries, newLiveries);

	}

	public static ArrayList<File> makeNewPaths(ArrayList<File> oldFiles, ArrayList<File> newFiles){
		String path;
		for(int i = 0; i < oldFiles.size(); i++){
			path = oldFiles.get(i).getAbsolutePath();
			if(path.contains("Asobo")){
				System.out.println(path.replaceAll("Asobo", "FlyByWire"));
			}
			else if(path.contains("a320")){
				System.out.println(path.replaceAll("a320", "a32nx"));
			}
			else if(path.contains("a320neo") || path.contains("a320Neo")){
				System.out.println(path.replaceAll("a320neo", "a32nx"));
			}
			//newFiles.add(new File(path));
		}
		return newFiles;
	}

	public static ArrayList<File> checkIfOldLivery(ArrayList<File> configs){
		ArrayList<File> validLiveries = new ArrayList<>();
		Scanner configReader;
		String currentLine;
		String oldLivery = "base_container = \"..\\Asobo_A320_NEO\"";

		for(int i = 0; i < configs.size(); i++){
			try {
				configReader = new Scanner(configs.get(i));
				while(configReader.hasNext()){
					currentLine = configReader.nextLine();
					if(currentLine.equals(oldLivery)){
						validLiveries.add(configs.get(i));
					}
				}
				configReader.close();
			} catch (FileNotFoundException ignored) {
			// If the file isn't found here, then it's not an A320 livery and can be ignored.
			}
		}
		return validLiveries;
	}


	public static void printList(ArrayList<String> list){
			for(int i = 0; i < list.size(); i++){
				System.out.println(list.get(i));
			}
		}


	public static void printFileList(ArrayList<File> list){
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i).getAbsolutePath() + " || " + list.get(i).exists() );
		}
	}


	// Old, because I hate the way I did this one.
	public static void getInstalledLiveries_old() {


		ArrayList<String> trueLiveryPaths = new ArrayList<>();
		ArrayList<String> newLiveryPaths = new ArrayList<>();

		ArrayList<String> newLiveryFolders = new ArrayList<>();
		ArrayList<String> oldLiveryFolders = new ArrayList<>();


		ArrayList<File> trueLiveries = new ArrayList<>();
		ArrayList<File> newLiveries = new ArrayList<>();

		ArrayList<File> oldSimObjects = new ArrayList<>();
		ArrayList<File> newSimObjects = new ArrayList<>();

		StringBuilder liverySB = new StringBuilder();
		StringBuilder simObjectSB = new StringBuilder();
/*
		ArrayList<File> baseLiveries = new ArrayList<>();

		ArrayList<String> installedAddons = new ArrayList<>();
		ArrayList<String> baseLiveryPaths = new ArrayList<>();

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
*/

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
