package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

import Controller.Cryptography;

public record PrivateKey(BigInteger d, BigInteger n) implements Serializable {

	public BigInteger decrypt(String message) {
		return Cryptography.modularExponentiation(new BigInteger(message), this.d, this.n);
	}

	public String decrypt(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals("$")) {
					sb.append("\n");
					continue;
				}
				sb.append((char) this.decrypt(line).intValue());
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return this.d + "\n" + this.n;
	}

}
