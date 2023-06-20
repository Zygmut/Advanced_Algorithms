package Model;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public record KeyPair(PrivateKey privateKey, PublicKey publicKey) {

	public BigInteger encrypt(String message) {
		return this.publicKey.encrypt(message);
	}

	public String encrypt(File file) throws IOException {
		return this.publicKey.encrypt(file);
	}

	public BigInteger decrypt(String message) {
		return this.privateKey.decrypt(message);
	}

	public String decrypt(File file) throws IOException {
		return this.privateKey.decrypt(file);
	}

}
