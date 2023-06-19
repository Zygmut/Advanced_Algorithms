package Master;

import java.io.File;
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
		KeyPair kp = Cryptography.generateRSAKeyPair("3", "11", 0);
		BigInteger message = kp.encrypt("109971161121081111161081059861615146554649");
		System.out.println(message);
		// Does not work
		BigInteger decrypted2 = kp.decrypt("12");
		System.out.println(decrypted2);

		MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		mvc.start();
	}
}
