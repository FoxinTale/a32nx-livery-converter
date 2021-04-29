import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FindLiveries {
	static ArrayList<File> simObjects = new ArrayList<>();
	static ArrayList<File> installedLiveries = new ArrayList<>();

	static ArrayList<File> newLiveries = new ArrayList<>();
	static ArrayList<File> newSimObjects = new ArrayList<>();


	public static void getInstalledLiveries(){
		ArrayList<String> allAddons = new ArrayList<>();
		ArrayList<String> simObjectFolderNames = new ArrayList<>();
		ArrayList<String> oldSimObjects = new ArrayList<>();

		ArrayList<Integer> slashCounts = new ArrayList<>();

		ArrayList<File> addonFiles = new ArrayList<>();
		ArrayList<File> aircraftConfigs = new ArrayList<>();
		ArrayList<File> validAircraft = new ArrayList<>();

		StringBuilder nameSB = new StringBuilder();
		StringBuilder simObjectSB = new StringBuilder();

		File aircraft;
		String[] contents = new String[0];

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
				//simObjects.add(aircraft);
				validAircraft.add(aircraft);
			}
		}

		//int size = simObjects.size();


		for(int c = 0; c < validAircraft.size(); c++){
			contents = validAircraft.get(c).list();
			simObjectFolderNames.add(contents[0]);
			simObjects.add(new File(validAircraft.get(c).getAbsolutePath() + "\\" + contents[0]));
		}


		for(int d = 0; d < simObjects.size(); d++){
			aircraftConfigs.add(new File(simObjects.get(d).getAbsolutePath() + "\\aircraft.cfg"));
		}


		//System.out.println(simObjects.size());
		installedLiveries = checkIfOldLivery(aircraftConfigs);
		simObjects.clear();
		//System.out.println(simObjects.size());

		installedLiveries = trimList(installedLiveries);
		newLiveries = trimList(newLiveries);

		makeNewPaths(installedLiveries, newLiveries);

		String tempObject;
		// printFileList(newLiveries);
		for(int e = 0; e < installedLiveries.size(); e++){
			nameSB.append(newLiveries.get(e));
			simObjectSB.append(installedLiveries.get(e));
			//contents = simObjects.get(e).list();
			//simObjectFolderNames.add(contents[0]);
			//System.out.println(simObjectSB.substring(simObjectSB.lastIndexOf("\\"), simObjectSB.length()));
			//tempObject = simObjectSB.substring(simObjectSB.lastIndexOf("\\"), simObjectSB.length());
			//	tempObject = simObjectSB.substring(simObjectSB.lastIndexOf("Airplanes"), simObjectSB.indexOf("aircraft.cfg"));
			//simObjectSB.delete(0, simObjectSB.length());
			//simObjectSB.append(tempObject);
			//oldSimObjects.add(simObjectSB.substring(simObjectSB.indexOf("\\") + 1, simObjectSB.lastIndexOf("\\")).strip());
			//nameSB.substring(0, nameSB.indexOf())
			//System.out.println(installedLiveries);
			//nameSB.substring(0, nameSB.indexOf("Airplanes") + 10) + oldSimObjects.get(e);

			//installedLiveries.set(e, new File(nameSB.substring(0, nameSB.indexOf("SimObjects"))));
			simObjects.add(new File(newLiveries.get(e).getAbsolutePath() + "\\SimObjects\\AirPlanes\\"));
			nameSB.delete(0, nameSB.length());
			simObjectSB.delete(0, simObjectSB.length());
		}

		simObjectFolderNames.clear();

		for(int f = 0; f < simObjects.size(); f++){
			System.out.println(Arrays.toString(Arrays.stream(simObjects.get(f).list()).toArray()));
			//simObjectFolderNames.add(contents[0]);
			//simObjects.set(f, new File(simObjects.get(f).getAbsolutePath() + "\\" +  contents[0]));
		}


		//printFileList(simObjects);
		//makeNewPaths(installedLiveries, newLiveries);
		//newSimObjects = makeNewPaths(simObjects, newSimObjects);

		//newLiveries.clear();
		//makeNewPaths(installedLiveries, newLiveries);

		//ConvertLiveries.copyLiveries();
		System.out.println("Old Simobjects List: ");
	//	printFileList(simObjects);
		//printFileList(newSimObjects);
		//ConvertLiveries.copyLiveries();
	}


	public static ArrayList<File> trimList(ArrayList<File> list){
		StringBuilder listSB = new StringBuilder();
		for(int i = 0; i < list.size(); i++){
			listSB.append(list.get(i).getAbsolutePath());
			list.set(i, new File(listSB.substring(0, listSB.indexOf("SimObjects"))));
			listSB.delete(0, listSB.length());
		}
		return list;
	}


	public static ArrayList<File> makeNewPaths(ArrayList<File> oldFiles, ArrayList<File> newFiles){
		String path;

		for(int i = 0; i < oldFiles.size(); i++){
			path = oldFiles.get(i).getAbsolutePath();
			if(path.contains("Asobo")){
				newFiles.add(new File(path.replaceAll("Asobo", "FlyByWire")));
			}
			else if(path.contains("a320")){
				newFiles.add(new File(path.replaceAll("a320", "a32nx")));
			}
			else if(path.contains("A20N")) {
				newFiles.add(new File(path.replaceAll("A20N", "A20NX")));
			} else {
				newFiles.add(new File(path));
			}
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


	// Takes an empty arraylist of files  and populates it with files whose paths come from the arraylist of strings.
	public static ArrayList<File> addFilestoList(ArrayList<File> fileList, ArrayList<String> filePaths){
		for(int i = 0; i < filePaths.size(); i++){
			fileList.add(new File(filePaths.get(i)));
		}
		return fileList;
	}
}
