import java.io.File;
import java.util.ArrayList;

public class FindLiveries {


	static ArrayList<File> aircraftConfigs = new ArrayList<File>();
	static ArrayList<File> textureConfigs = new ArrayList<File>();
	
	public static void getInstalledLiveries() {

		File packages = new File(GetPlatform.finalInstallPath);
		File communityFolder = null;
		File livery;
		File manifestFile;
		File aircraftFile;
		

		String objectsPath = "\\SimObjects\\AirPlanes\\";
		String asoboAircraft = "Asobo_A320_NEO";
		String aircraftConfig = "\\aircraft.cfg";
		String communityContents[] = null;
		String communityPath = null;
		String filePath;

		
		ArrayList<String> installedAddons = new ArrayList<String>();
		ArrayList<String> simobjectsPath = new ArrayList<String>();
		ArrayList<String> liveries = new ArrayList<String>();
		
		ArrayList<File> installedLiveries = new ArrayList<File>();
		

		
		if(packages.exists()) {
			communityFolder = new File(packages.getAbsolutePath() + "\\Community\\");
		} else {
			Main.printErr("Could not find the packages folder. Oops.");
		}
		

		if(communityFolder.exists()) {
			 communityContents = communityFolder.list();
			communityPath =  communityFolder.getAbsolutePath() + "\\";

			for(int i = 0; i < communityContents.length; i++) {
				manifestFile = new File(communityPath + communityContents[i] + "\\manifest.json"); 
				
				if(manifestFile.exists()) {
					installedAddons.add(communityPath + communityContents[i]);
					// Has a valid manifest.json file, and is a proper addon / mod.
				} else {
					// No manifest json file.  Ignore this or throw an error.
				}
			}	
		} else {
			Main.printErr("Could not find community folder.. How?");
		}
		

		
		for(int i = 0; i < installedAddons.size(); i++) {
			simobjectsPath.add(installedAddons.get(i) + objectsPath);
		}
		

		for(int i = 0; i < simobjectsPath.size(); i++){
			filePath = simobjectsPath.get(i) + communityContents[i];

			if(filePath.contains(asoboAircraft)) {
				livery = new File(filePath);		
				installedLiveries.add(livery);
				liveries.add(communityPath + communityContents[i]);
				//System.out.println(communityContents[i] + " is a valid livery.");
			}	else {
				//This just means it is not a livery for the A320. Ignore it.
			}
		}
		
		for(int i = 0; i < installedLiveries.size(); i++) {
			aircraftFile = new File(installedLiveries.get(i).getAbsolutePath() + aircraftConfig);
				aircraftConfigs.add(aircraftFile);
		}
		
		
		ConvertLayout.readJsonFile(liveries.get(0));


		
		/*
		 * if(aircraftConfigs.size() == installedLiveries.size()) { for(int i = 0; i <
		 * aircraftConfigs.size(); i++) {
		 * ConvertLiveries.convertAircraftConfig(aircraftConfigs.get(i),
		 * installedLiveries.get(i)); } } else { // They should be the same size, but
		 * output an error nevertheless. }
		 */


		ConvertLiveries.convertAircraftConfig(aircraftConfigs.get(1), installedLiveries.get(1));
		

	}
}
