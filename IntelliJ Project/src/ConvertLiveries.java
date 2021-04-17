import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvertLiveries {

	static String textureFolderName = null;
	static String modelFolderName = null;
	
	public static void convertAircraftConfig(File aircraftConfig, File path) {
		
		if(!aircraftConfig.exists()) {
			// This handles if the folder name has a "-" when the others have a "_" in it.
			StringBuilder configSB = new StringBuilder();
			configSB.append(aircraftConfig.getAbsolutePath());
			configSB.delete(configSB.length() - 13, configSB.length());
			
			int underscoreIndex = configSB.lastIndexOf("_");
			configSB.replace(underscoreIndex, underscoreIndex + 1, "-");
			path = new File(configSB.toString());
			aircraftConfig = new File(configSB.toString() + "\\aircraft.cfg");
			
		}
		
		ArrayList<String> aircraftConfigContents = new ArrayList<String>();
		
		String oldAircraft = "base_container = \"..\\Asobo_A320_NEO\"";
		String newAircraft = "base_container = \"..\\FlyByWire_A320_NEO\"";
		
		int baseContIndex = 0;
		int uiManIndex = 0;
		int uiTypeIndex = 0;
		String textureFolder = null;
		String modelFolder = null;
		String currentLine;
		boolean mainTextureFound = false;
		boolean mainModelFound = false;
		boolean manufacturerFound = false;
		boolean typeFound = false;
		
		try {
			Scanner aircraftReader = new Scanner(aircraftConfig);
			
			while(aircraftReader.hasNext()) {
				currentLine = aircraftReader.nextLine();
				aircraftConfigContents.add(currentLine);
			}
			
			aircraftReader.close();
			//System.out.println("File deleted");
			//aircraftConfig.delete();
			
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
				fnfe.printStackTrace();
		}
		
		

		for(int i = 0; i < aircraftConfigContents.size(); i++) {
			if(aircraftConfigContents.get(i).equals(oldAircraft)) {
				baseContIndex = i;
			} else {
				// Could not find the base container.
			}
			
			
			if(aircraftConfigContents.get(i).contains("texture") && !mainTextureFound) {
				textureFolder = aircraftConfigContents.get(i);
				mainTextureFound = true;
			}
			
			if(aircraftConfigContents.get(i).contains("model") && !mainModelFound) {
				modelFolder = aircraftConfigContents.get(i);
				mainModelFound = true;
			}
			
			if(aircraftConfigContents.get(i).contains("ui_manufacturer") && !manufacturerFound){
				uiManIndex = i;
				manufacturerFound = true;
			}
			
			if(aircraftConfigContents.get(i).contains("ui_type") && !typeFound) {
				uiTypeIndex = i;
				typeFound = true;
			}
		}
		
		
		mainTextureFound = false;
		mainModelFound = false;
		manufacturerFound = false;
		typeFound = false;
		
		
		aircraftConfigContents.set(baseContIndex, newAircraft);
		aircraftConfigContents.set(uiManIndex, "ui_manufacturer = \"FlyByWire Simulations\"");
		aircraftConfigContents.set(uiTypeIndex, "ui_type = \"A320neo (LEAP)\"");
		

		
		textureFolderName = removeQuotes(textureFolder);
		modelFolderName = removeQuotes(modelFolder);
		
		//printArrList(aircraftConfigContents);

		//writeNewConfigFile(aircraftConfigContents, newConfigFile + "\\aircraft.cfg");
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
		firstFound = false;
		
		s = sb.substring(sbPoints[0], sbPoints[1]);
		sb.delete(0, sb.length());
		sb.append(s);
		sb.deleteCharAt(0);
		return sb.toString();
	}
	
	
	
	
	
	public static void printArrList(ArrayList<String> arrlist) {
			for(int i = 0; i < arrlist.size(); i++) {
				System.out.println(arrlist.get(i));
			}
	}
	
	
	
	
	public static void convertTextureConfig(File path, String folderName) {
		int textureFolderIndex = 0;
		int textureFileIndex = 0;
		
		ArrayList<String> textureConfigContents = new ArrayList<String>();
		ArrayList<String> textureContents = new ArrayList<String>();
		
		String mainFolderContents[] = path.list();
		String textureFolderContents[];
		String currentLine;
		
		File textureFolder;
		File textureConfigFile;
	
		
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
			//textureConfigFile.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < textureConfigContents.size(); i++) {
			if(textureConfigContents.get(i).contains("Asobo_A320_NEO")) {
				textureContents.add(textureConfigContents.get(i));
			}
		}
		
		StringBuilder textureSB = new StringBuilder();
		
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
		//writeNewConfigFile(textureConfigContents, textureFolder.getAbsolutePath() + "\\texture.cfg");
		convertModelConfig(path);
	}
	
	
	
	
	public static void convertModelConfig(File path) {
		File modelConfigFile = new File(path.getAbsolutePath() + "\\MODEL." + modelFolderName + "\\" + "model.cfg");
		ArrayList<String> modelConfigContents = new ArrayList<String>();
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
			//modelConfigFile.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < modelConfigContents.size(); i++) {
			if(modelConfigContents.get(i).contains("[models]")) {
				modelsPoint = i;
			}
		}
		
		if(modelConfigContents.get(modelsPoint + 1).contains("exterior")) {
			exteriorLine = modelConfigContents.get(modelsPoint + 1);
			StringBuilder exteriorSB = new StringBuilder();
			exteriorSB.append(exteriorLine);
			exteriorLine = exteriorSB.toString().replace("Asobo_A320_NEO", "FlyByWire_A320_NEO");
			modelConfigContents.set(modelsPoint + 1, exteriorLine);
		} else {
			// Could not find the exterior line.
		}
		
		
		if(modelConfigContents.get(modelsPoint + 2).contains("interior")) {
			exteriorLine = modelConfigContents.get(modelsPoint + 2);
			StringBuilder interiorSB = new StringBuilder();
			interiorSB.append(exteriorLine);
			interiorLine = interiorSB.toString().replace("Asobo_A320_NEO", "FlyByWire_A320_NEO");
			modelConfigContents.set(modelsPoint + 2, interiorLine);
		} else {
			// Could not find the interior line
		}
		
		//writeNewConfigFile(modelConfigContents, path.getAbsolutePath() + "\\" + "Model." + modelFolderName + "\\" + "model.cfg");
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
