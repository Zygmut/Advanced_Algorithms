package Master;

import java.io.File;
import java.io.FileWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import Controller.Cryptography;
import Model.KeyPair;
import mesurament.Mesurament;
import utils.Config;

public class Main {

	public static void main(String[] args) {
		if (!Config.DEBUG) {
			Mesurament.mesura();
		}
		File file = new File("C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\tools\\requirements.txt");
		KeyPair kp = Cryptography.generateRSAKeyPair("3", "11", 0);
		String encrypted = "";
		try {
			encrypted = kp.encrypt(file, 8192);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Write the file using buffered writer
		try(FileWriter fileWriter = new FileWriter("C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\tools\\requirements_encrypted.txt")) {
		    fileWriter.write(encrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File en_file = new File("C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\tools\\requirements_encrypted.txt");
		try {
			System.out.println(kp.decrypt(en_file, 8192));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		mvc.start();
	}
}
