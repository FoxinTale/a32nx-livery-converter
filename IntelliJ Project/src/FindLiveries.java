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

		ArrayList<File> baseLiveries = new ArrayList<>();
		ArrayList<File> newLiveries = new ArrayList<>();


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


		StringBuilder liverySB = new StringBuilder();

		for(int i = 0; i < trueLiveryPaths.size(); i++){
			if(trueLiveryPaths.get(i).contains("Asobo_A320_NEO")){
				liverySB.append(trueLiveryPaths.get(i));
				newLiveryPaths.add(liverySB.toString().replace("Asobo", "FlyByWire"));
				liverySB.delete(0, liverySB.length());
			}
		}

		addFilestoList(newLiveries, newLiveryPaths);

		try {
			FileOps.copyDirectory(baseLiveries.get(0), newLiveries.get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}


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
		File item;

		for(int i = 0; i < filePaths.size(); i++){
			item = new File(filePaths.get(i));
			fileList.add(item);
		}

		return fileList;
	}


}
