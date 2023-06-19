package Model;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public record KeyPair(PrivateKey privateKey, PublicKey publicKey) {

	public BigInteger encrypt(String message) {
		return this.publicKey.encrypt(new BigInteger(message));
	}

	public String encrypt(File file, final int BUFFER_SIZE) throws IOException {
		return this.publicKey.encrypt(file, BUFFER_SIZE);
	}

	public BigInteger decrypt(String message) {
		return this.privateKey.decrypt(new BigInteger(message));
	}

	public String decrypt(File file, final int BUFFER_SIZE) throws IOException {
		return this.privateKey.decrypt(file, BUFFER_SIZE);
	}

}
