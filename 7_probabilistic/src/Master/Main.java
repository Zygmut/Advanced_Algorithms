package Master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Controller.Cryptography;
import Model.KeyPair;
import mesurament.Mesurament;
import utils.Config;

public class Main {

	public static void main(String[] args) {
		if (!Config.DEBUG) {
			Mesurament.mesura();
		}
		KeyPair kp = Cryptography.generateRSAKeyPair(Cryptography.generatePrime(100, 0).toString(),
				Cryptography.generatePrime(100, 1).toString(), 0);

		/*
		 * final String text = "hola que tal";
		 * for (char car : text.toCharArray()) {
		 * final BigInteger encrypt = kp.encrypt(String.valueOf((int) car));
		 * final BigInteger decrypt = kp.decrypt(encrypt.toString());
		 * System.out.println(car + " (" + (int) car + ") -> " + encrypt + " -> " +
		 * (char) decrypt.intValue() + " (" + decrypt + ")");
		 * }
		 */

 		File file = new File(
				"C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\test.txt");
		String encrypted = "";
		try {
			encrypted = kp.encrypt(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Write the file using buffered writer
		try (FileWriter fileWriter = new FileWriter(
				"C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\test_en.txt")) {
			fileWriter.write(encrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File en_file = new File(
				"C:\\Users\\ruben\\Documents\\Github\\Advanced_Algorithms\\7_probabilistic\\test_en.txt");
		try {
			System.out.println(kp.decrypt(en_file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		 * mvc.start();
		 */
	}
}
