package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

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

			}
		}

		return sb.toString();
	}

}
