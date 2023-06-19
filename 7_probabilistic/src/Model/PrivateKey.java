package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import Controller.Cryptography;

public record PrivateKey(BigInteger d, BigInteger n) {

	public BigInteger decrypt(BigInteger encrypted) {
		return Cryptography.modularExponentiation(encrypted, this.d, this.n);
	}

	public String decrypt(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				BigInteger encrypted = new BigInteger(line);
				System.out.println("Decrypting: " + encrypted);
				BigInteger decrypted = this.decrypt(encrypted);
				byte[] bytes = decrypted.toByteArray();
				System.out.println("Decrypting: " + decrypted + " -> " + Arrays.toString(bytes));
				System.out.println("Decrypting: " + decrypted + " -> " + new String(bytes));
				sb.append(new String(bytes));
				sb.append("\n");
			}
		}

		return sb.toString();
	}

}
