import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvertLiveries extends FindLiveries{

	static ArrayList<File> manifests = new ArrayList<>();
	static ArrayList<File> layouts = new ArrayList<>();
	static ArrayList<File> aircraftConfigs = new ArrayList<>();

	static String textureFolderName = null;
	static String modelFolderName = null;

	public static void copyLiveries() {
		for(int i = 0; i < installedLiveries.size(); i++){
			try{
				FileOps.copyLivery(installedLiveries.get(i), newLiveries.get(i));
			} catch(IndexOutOfBoundsException ignored){

			}
		}
	}


	public static void copySimObjects(){
		for(int j = 0; j < simObjects.size(); j++){
			FileOps.copySimObjects(simObjects.get(j), newSimObjects.get(j));
		}
	}


	// Looks for the aircraft
	public static void findAllFiles(){
		manifests = populateList(newLiveries, manifests, "\\manifest.json");
		layouts = populateList(newLiveries, layouts, "\\layout.json");
		aircraftConfigs = populateList(newSimObjects, aircraftConfigs, "\\aircraft.cfg");
		startFileConversion();
	}


	public static ArrayList<File> populateList(ArrayList<File> baseDir, ArrayList<File> list, String fileName){
		for(int i = 0; i < baseDir.size(); i++){
			list.add(new File(baseDir.get(i).getAbsolutePath() + fileName));
		}
		return list;
	}


	public static void startFileConversion(){
		// Iteration variable within each one says what it does.
		for(int confs = 0; confs < aircraftConfigs.size(); confs++){
			convertAircraftConfig(aircraftConfigs.get(confs), newSimObjects.get(confs));
		}

		for(int layouts = 0; layouts < newLiveries.size(); layouts++){
			ConvertLayout.readJsonFile(newLiveries.get(layouts));
		}

		for(int manifests = 0; manifests < newLiveries.size(); manifests++){
			ConvertManifest.readManifest(newLiveries.get(manifests));
		}

	}


	public static void convertAircraftConfig(File aircraftConfig, File path) {
		ArrayList<String> aircraftConfigContents = new ArrayList<>();
		
		String oldAircraft = "base_container = \"..\\Asobo_A320_NEO\"";
		String newAircraft = "base_container = \"..\\FlyByWire_A320_NEO\"";

		// Main Texture Found, Main Model Found, Manufacturer Found, Type Found
		Boolean[] findings = {false, false, false, false};

		// Index of "base_container", Index of "ui_manufacturer, Index of "ui_type"
		Integer[] indexes = {0, 0, 0};

		String textureFolder = null;
		String modelFolder = null;
		String currentLine;

		try {
			Scanner aircraftReader = new Scanner(aircraftConfig);
			
			while(aircraftReader.hasNext()) {
				currentLine = aircraftReader.nextLine();
				aircraftConfigContents.add(currentLine);
			}
			
			aircraftReader.close();
			aircraftConfig.delete();
			
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
				fnfe.printStackTrace();
		}


		for(int i = 0; i < aircraftConfigContents.size(); i++) {
			if(aircraftConfigContents.get(i).equals(oldAircraft)) {
				indexes[0] = i;
			} else {
				// Could not find the base container.
			}

			if(aircraftConfigContents.get(i).contains("texture") && !findings[0]) {
				textureFolder = aircraftConfigContents.get(i);
				findings[0] = true;
			}
			
			if(aircraftConfigContents.get(i).contains("model") && !findings[1]) {
				modelFolder = aircraftConfigContents.get(i);
				findings[1] = true;
			}
			
			if(aircraftConfigContents.get(i).contains("ui_manufacturer") && !findings[2]){
				indexes[1] = i;
				findings[2] = true;
			}
			
			if(aircraftConfigContents.get(i).contains("ui_type") && !findings[3]) {
				indexes[2] = i;
				findings[3] = true;
			}
		}

		
		aircraftConfigContents.set(indexes[0], newAircraft);
		aircraftConfigContents.set(indexes[1], "ui_manufacturer = \"FlyByWire Simulations\"");
		aircraftConfigContents.set(indexes[2], "ui_type = \"A320neo (LEAP)\"");

		textureFolderName = removeQuotes(textureFolder);
		modelFolderName = removeQuotes(modelFolder);

		writeNewConfigFile(aircraftConfigContents, path.getAbsolutePath() + "\\aircraft.cfg");
		convertTextureConfig(path, textureFolderName);
	}
	
	
	public static String removeQuotes(String s) {
		StringBuilder sb = new StringBuilder();
		int[] sbPoints = new int[2];
		boolean firstFound = false;
		
		sb.append(s);
		
		for(int i =0; i < sb.length(); i++) {
			if(sb.charAt(i) == '\"' && !firstFound) {
				sbPoints[0] = i;
				firstFound = true;
			}
			
			if(sb.charAt(i) == '\"' && firstFound) {
				sbPoints[1] = i;
			}
		}

		s = sb.substring(sbPoints[0], sbPoints[1]);
		sb.delete(0, sb.length());
		sb.append(s);
		sb.deleteCharAt(0);
		return sb.toString();
	}
	

	public static void convertTextureConfig(File path, String folderName) {
		int textureFolderIndex = 0;
		int textureFileIndex = 0;
		
		ArrayList<String> textureConfigContents = new ArrayList<>();
		ArrayList<String> textureContents = new ArrayList<>();
		
		String mainFolderContents[] = path.list();
		String textureFolderContents[];
		String currentLine;
		
		File textureFolder;
		File textureConfigFile;

		StringBuilder textureSB = new StringBuilder();

		for(int i = 0; i < mainFolderContents.length; i++) {
			if(mainFolderContents[i].contains(folderName)) {
				textureFolderIndex = i;
			}
		}

		textureFolder = new File(path.getAbsolutePath() + "\\" + mainFolderContents[textureFolderIndex]);
		textureFolderContents = textureFolder.list();
		
		for (int i = 0; i < textureFolderContents.length; i++) {
			if(textureFolderContents[i].contains("texture")) {
				textureFileIndex = i;
			}
		}
		
		textureConfigFile = new File(textureFolder.getAbsolutePath() + "\\" + textureFolderContents[textureFileIndex]);
		
		try {
			Scanner textureReader = new Scanner(textureConfigFile);
			
			while(textureReader.hasNext()) {
				currentLine = textureReader.nextLine();
				textureConfigContents.add(currentLine);
			}
			textureReader.close();
			textureConfigFile.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < textureConfigContents.size(); i++) {
			if(textureConfigContents.get(i).contains("Asobo_A320_NEO")) {
				textureContents.add(textureConfigContents.get(i));
			}
		}


		for(int i = 0; i <textureContents.size(); i++) {
			textureContents.get(i).replace("Asobo", "FlyByWire");
			textureSB.append(textureContents.get(i));
			textureContents.set(i , textureSB.toString().replace("Asobo", "FlyByWire"));
			textureSB.delete(0, textureSB.length());
		}

		for(int i = 0; i < textureConfigContents.size(); i++) {
			if(textureConfigContents.get(i).contains("Asobo_A320_NEO")) {
				for(int j = 0; j < textureContents.size(); j++) {
					textureConfigContents.set(i, textureContents.get(j));
				}		
			}
		}

		writeNewConfigFile(textureConfigContents, textureFolder.getAbsolutePath() + "\\texture.cfg");
		convertModelConfig(path);
	}
	
	

	public static void convertModelConfig(File path) {
		File modelConfigFile = new File(path.getAbsolutePath() + "\\MODEL." + modelFolderName + "\\" + "model.cfg");
		ArrayList<String> modelConfigContents = new ArrayList<>();

		String currentLine = null;
		String exteriorLine = null;
		String interiorLine = null;
		int modelsPoint = 0;
		
		try {
			Scanner modelReader = new Scanner(modelConfigFile);
			
			while(modelReader.hasNext()) {
				currentLine = modelReader.nextLine();
				modelConfigContents.add(currentLine);
			}
			
			modelReader.close();
			modelConfigFile.delete();
			
		} catch (FileNotFoundException e) {
			Main.printErr("Model config not found. either something went wrong, or this livery doesn't have one to begin with.");
		}
		
		
		for(int i = 0; i < modelConfigContents.size(); i++) {
			if(modelConfigContents.get(i).contains("[models]")) {
				modelsPoint = i;
			}
		}

		try {
			if (modelConfigContents.get(modelsPoint + 1).contains("exterior")) {
				exteriorLine = modelConfigContents.get(modelsPoint + 1);
				StringBuilder exteriorSB = new StringBuilder();
				exteriorSB.append(exteriorLine);
				exteriorLine = exteriorSB.toString().replace("Asobo_A320_NEO", "FlyByWire_A320_NEO");
				modelConfigContents.set(modelsPoint + 1, exteriorLine);
			}

			if(modelConfigContents.get(modelsPoint + 2).contains("interior")) {
				exteriorLine = modelConfigContents.get(modelsPoint + 2);
				StringBuilder interiorSB = new StringBuilder();
				interiorSB.append(exteriorLine);
				interiorLine = interiorSB.toString().replace("Asobo_A320_NEO", "FlyByWire_A320_NEO");
				modelConfigContents.set(modelsPoint + 2, interiorLine);
			}

			writeNewConfigFile(modelConfigContents, path.getAbsolutePath() + "\\" + "Model." + modelFolderName + "\\" + "model.cfg");
		} catch(IndexOutOfBoundsException ignored){

		}
	}
	

	
			
	public static void writeNewConfigFile(ArrayList<String> fileContents, String fileName) {
		File newConfigFile = new File(fileName);
	
		try {
			FileWriter newConfigWriter = new FileWriter(newConfigFile);

			for(int i = 0; i < fileContents.size(); i++) {
				newConfigWriter.write(fileContents.get(i) + "\n");
			}

			newConfigWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
