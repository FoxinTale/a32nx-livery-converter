import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.io.IOException;

//Class name explains itself.
public class GUI {
	
	static JTextArea consoleOutput = new JTextArea();
	static JScrollPane scroll = new JScrollPane(consoleOutput);

	public static void makeGUI() throws IOException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame converter = new JFrame();
		converter.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		converter.setTitle("A32NX - Standalone Livery Converter");
		consoleOutput.setEditable(false);
	
		scroll.setBounds(20,20,750,450);
		
		converter.add(scroll);
		converter.setSize(960, 540); // qHD resolution.
		converter.setResizable(false);
		converter.setLayout(null); // No layout manager.
		converter.setVisible(true);

		GetPlatform.whichPlatform();
	}
	
	
	//May remove this later.
	public static boolean advancedSteps() {
		String t = "Basic conversion completed. Would you like to further into advanced steps?";
		int o = JOptionPane.showConfirmDialog(new JFrame(), t, "Advanced options", JOptionPane.YES_NO_OPTION);
		if (o == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
}
