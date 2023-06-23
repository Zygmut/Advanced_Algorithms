package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

import Controller.Cryptography;

public record PublicKey(BigInteger e, BigInteger n) implements Serializable {

	public BigInteger encrypt(String message) {
		return Cryptography.modularExponentiation(new BigInteger(message), this.e, this.n);
	}

	public String encrypt(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				line.chars().forEach(e -> {
					final BigInteger encrypted = this.encrypt(String.valueOf(e));
					sb.append(encrypted.toString()).append("\n");
				});
				// Add special character to indicate end of line
				sb.append("$\n");
			}
			// Remove last special character
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}


	@Override
	public String toString() {
		return this.e + "\n" + this.n;
	}
}
