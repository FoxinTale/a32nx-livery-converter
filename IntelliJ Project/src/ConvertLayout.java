import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConvertLayout {
	
	static String layout;
	static String liveryPath;

	
	public static void readJsonFile(File layoutFile) {

		layout = layoutFile.getAbsolutePath() + "//layout.json";
		liveryPath = layoutFile.getAbsolutePath();
		String currentLine;
		ArrayList<String> layoutContents = new ArrayList<>();
		
		try {
			Scanner layoutReader = new Scanner(new File(layout));
		
			while(layoutReader.hasNext()) {
				currentLine = layoutReader.nextLine();
				layoutContents.add(currentLine);
			}
			
			layoutReader.close();
			layoutFile.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		editJsonFile(layoutContents);
	}



	
	
	public static void editJsonFile(ArrayList<String> fileContents) {
		ArrayList<Integer> positions = new ArrayList<>();
		
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
		checkContentExists(fileContents, positions);
	}


	public static void checkContentExists(ArrayList<String> fileContents, ArrayList<Integer> positions){
		ArrayList<File> layoutFiles = new ArrayList<>();
		StringBuilder pathSB = new StringBuilder();
		String suffix;
		boolean allGood = false;
		int position;

		for(int a = 0; a < positions.size(); a++){
			position = positions.get(a);
			pathSB.append(fileContents.get(position));
			layoutFiles.add(new File(liveryPath + "\\" +  pathSB.substring(pathSB.indexOf("SimObjects"), pathSB.lastIndexOf("\""))));
			pathSB.delete(0, pathSB.length());
		}

		for(int b = 0; b < layoutFiles.size(); b++){
			suffix = layoutFiles.get(b).getAbsolutePath();
			if(!layoutFiles.get(b).exists()){
				if(suffix.endsWith(".dds") || suffix.endsWith(".cfg") || suffix.endsWith(".json") || suffix.endsWith(".xml")){
					System.out.println(layoutFiles.get(b).getAbsolutePath() + "  does not exist!");
					allGood = false;
				}
			} else{
				allGood = true;
			}
		}

		if(allGood){
			writeJsonFile(fileContents);
		}
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
