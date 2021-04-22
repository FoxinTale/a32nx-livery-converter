import FileUtils.FileOps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FindLiveries {


	static ArrayList<File> aircraftConfigs = new ArrayList<File>();
	static ArrayList<File> textureConfigs = new ArrayList<File>();


	public static void getInstalledLiveries() {
		ArrayList<String> installedAddons = new ArrayList<>();
		ArrayList<String> baseLiveryPaths = new ArrayList<>();
		ArrayList<String> trueLiveryPaths = new ArrayList<>();
		ArrayList<String> newLiveryPaths = new ArrayList<>();
		ArrayList<String> newLiveryFolders = new ArrayList<>();

		ArrayList<File> baseLiveries = new ArrayList<>();
		ArrayList<File> newLiveries = new ArrayList<>();
		ArrayList<File> oldSimObjects = new ArrayList<>();
		ArrayList<File> newSimObjects = new ArrayList<>();

		String liveryFolderContents[] = new String[0];

		File oldSimObjectFolder = null;
		File newSimObjectFolder;
		File liveryObject = null;

		StringBuilder liverySB = new StringBuilder();
		StringBuilder simObjectSB = new StringBuilder();

		installedAddons = scanCommunityFolder(installedAddons);
		baseLiveryPaths = findBaseLiveries(installedAddons, baseLiveryPaths);

		addFilestoList(baseLiveries, baseLiveryPaths);

		for(int i = 0; i < baseLiveries.size(); i++){
			if(SymlinkHandling.checkForSymlink(baseLiveries.get(i))){
				try {
					trueLiveryPaths.add(Files.readSymbolicLink(baseLiveries.get(i).toPath()).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				trueLiveryPaths.add(baseLiveries.get(i).getAbsolutePath());
			}
		}

		for(int i = 0; i < trueLiveryPaths.size(); i++){
			if(trueLiveryPaths.get(i).contains("Asobo_A320_NEO")){
				liverySB.append(trueLiveryPaths.get(i));
				newLiveryPaths.add(liverySB.toString().replace("Asobo", "FlyByWire"));
				// This is probably the longest singular line of code I have written to date.
				newLiveryFolders.add(liverySB.substring(liverySB.lastIndexOf("\\") + 1, liverySB.length()).replace("Asobo", "FlyByWire").trim());
				liverySB.delete(0, liverySB.length());
			}
		}

		addFilestoList(newLiveries, newLiveryPaths);

 //Functional copying of all of the existing libraries
/*
		try {
			for(int i = 0; i < baseLiveries.size(); i++){
				FileUtils.FileOps.copyDirectory(baseLiveries.get(i), newLiveries.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
*/


		//File liveryObject = new File(newLiveries.get(0).getAbsolutePath() + "\\SimObjects\\AirPlanes\\");
		//System.out.println(liveryObject.getAbsolutePath());
		//liveryFolderContents = liveryObject.list();

		 oldSimObjects = fillSubFolderList(baseLiveries, oldSimObjects);
		 newSimObjects = fillSubFolderList(newLiveries, newSimObjects);


		for(int i = 0; i < liveryFolderContents.length; i++){
			if(liveryFolderContents[i].contains("Asobo_A320_NEO")){
				//oldSimObjectFolder = new File(liveryObject.getAbsolutePath() + "\\" + liveryFolderContents[i]);
				liveryObject = new File(newLiveries.get(i).getAbsolutePath() + "\\SimObjects\\AirPlanes\\");
				oldSimObjects.add(new File(liveryObject.getAbsolutePath() + "\\" + liveryFolderContents[i]));
			}
		}


		for(int i = 0; i < oldSimObjects.size(); i++){
			simObjectSB.append(oldSimObjects.get(i).getAbsolutePath());
			newSimObjects.add(new File(simObjectSB.toString().replace("Asobo", "FlyByWire")));
			simObjectSB.delete(0, simObjectSB.length());
		}


		for(int i = 0; i< newLiveryFolders.size(); i++){
			System.out.println(newLiveryFolders.get(i));

		}


/*



		try {
			FileOps.copyDirectory(oldSimObjectFolder, newSimObjectFolder);
			FileOps.deleteDirectory(oldSimObjectFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(newSimObjectFolder.getAbsolutePath());*/

		// System.out.println(liveryObject.getAbsolutePath());

	}


	public static ArrayList<String> scanCommunityFolder(ArrayList<String> contents){
		File packages = new File(GetPlatform.finalInstallPath);
		File communityFolder = null;
		String communityContents[];
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


	public static ArrayList<File> fillSubFolderList(ArrayList<File> mainfolders, ArrayList<File> subfolders){
		String folderContents[] = new String[0];
		String folderName = null;
		ArrayList<File> liveryFolders = new ArrayList();


		for(int i = 0; i < mainfolders.size(); i++) {
			liveryFolders.add(new File( mainfolders.get(i).getAbsolutePath() + "\\SimObjects\\AirPlanes\\"));
		}


		for(int i = 0; i < liveryFolders.size(); i++){
			folderContents = liveryFolders.get(i).list();
			//System.out.println(liveryFolders.get(i).getAbsolutePath() + "\\" + folderContents[0]);
			subfolders.add(new File(liveryFolders.get(i).getAbsolutePath() + "\\" + folderContents[0]));
		}



		//folder
		//System.out.println(folderContents[0]);
			//folderContents = mainfolders.get(i).list();
			//for(int j = 0; j < folderContents.length; j++){
			//	System.out.println(folderContents[j]);
			//	if(folderContents[j].contains("Asobo") || folderContents[j].contains("FlyByWire")){
			//		folderName = folderContents[j];
			//	}
		//	}
			//subfolders.add(new File());

		return subfolders;
	}

}
