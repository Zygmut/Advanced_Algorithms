package Master;

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
		System.out.println(kp);
		System.out.println(kp.publicKey().encrypt("2"));
		System.out.println(kp.privateKey().decrypt("29"));
		MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		mvc.start();
	}
}
