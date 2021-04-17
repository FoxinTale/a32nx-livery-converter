import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GetPlatform {
	
	static String finalInstallPath = null;
	
	
	public static void whichPlatform() {
		
		String userDir = System.getProperty("user.home");
		String path = null;
		String installPath = null;
		
		int platform = 0; //0 is default, 1 for Steam, and 2 for MS store. 3 is other.
		
		File steamUserConfig = new File(userDir + "\\AppData\\Roaming\\Microsoft Flight Simulator\\UserCfg.opt");
		File storeUserConfig = new File(userDir + "\\AppData\\Local\\Packages\\Microsoft.FlightSimulator_8wekyb3d8bbwe\\LocalCache\\UserCfg.opt");
		
		Scanner configReader = null;
		
		ArrayList<String> lines = new ArrayList<String>();
		
		StringBuilder installPathSB = new StringBuilder();
		
		
		if(steamUserConfig.exists()) {
			platform = 1;
		} 
		else if(storeUserConfig.exists()) {
			platform = 2;
		} else {
			platform = 3;
			Main.printErr("Could not find the user config. Aborting.");
			// FBW installer walks through the entire "AppData\Local"  folder and looks for a folder with "Flight" in the name.
		}
		
		
		try {
			if (platform == 1) {
				configReader = new Scanner(steamUserConfig);
			}	
			else if(platform == 2) {
				configReader = new Scanner(storeUserConfig);
			} else {
				//We'll do this later. 
			}			
			
			
			while(configReader.hasNext()) {
				lines.add(configReader.nextLine());
			}
		
			configReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int length = lines.size();
		for(int i = 0; i < length; i++) {
			if(lines.get(i).contains("InstalledPackagesPath")) {
				installPath = lines.get(i);
			} else {

			}
		}
		
		
		
		installPathSB.append(installPath);
		int spacePos = installPathSB.indexOf(" ");
		path = installPathSB.substring(spacePos, installPathSB.length());
		length = installPathSB.length();
		installPathSB.delete(0, length);
		installPathSB.append(path);
		length = installPathSB.length();
		
		for(int i = 0; i < length - 1; i++) {
			if(installPathSB.charAt(i) == '\"') {
				installPathSB.deleteCharAt(i);
			}
		}
		
		finalInstallPath = installPathSB.toString().strip();
		System.out.println(finalInstallPath);
		FindLiveries.getInstalledLiveries();
	}
}
