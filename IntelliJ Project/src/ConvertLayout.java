import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvertLayout {
	
	static String layout;
	
	public static void readJsonFile(String layoutPath) {
		layout = layoutPath + "//layout.json";
		File layoutFile = new File(layout);
		String currentLine;
		ArrayList<String> layoutContents = new ArrayList<String>();
		
		try {
			Scanner layoutReader = new Scanner(layoutFile);
		
			while(layoutReader.hasNext()) {
				currentLine = layoutReader.nextLine();
				layoutContents.add(currentLine);
			}
			
			layoutReader.close();
			//layoutFile.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		editJsonFile(layoutContents);
	}
	
	
	
	
	public static void editJsonFile(ArrayList<String> fileContents) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		for(int i = 0; i < fileContents.size(); i++) {
			if(fileContents.get(i).contains("path")) {
				positions.add(i);
			}
		}
		
		StringBuilder lineSB = new StringBuilder();
		String line;
		
		for(int i = 0; i < positions.size(); i++) {
			lineSB.append(fileContents.get(positions.get(i)));
			line = lineSB.toString().replace("Asobo_A320_NEO", "FlyByWire_A320_NEO");
			fileContents.set(positions.get(i), line);
			lineSB.delete(0, lineSB.length());
		}
		//writeJsonFile(fileContents);
	}
	
	
	
	
	public static void writeJsonFile(ArrayList<String> fileContents) {
		File newConfigFile = new File(layout);
		
		try {
			FileWriter jsonWriter = new FileWriter(newConfigFile);
			
			for(int i = 0; i < fileContents.size(); i++) {
				jsonWriter.write(fileContents.get(i) + "\n");
			}

			jsonWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
