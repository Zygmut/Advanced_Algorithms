package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import Controller.Cryptography;

public record PublicKey(BigInteger e, BigInteger n) {

	public BigInteger encrypt(BigInteger message) {
		return Cryptography.modularExponentiation(message, this.e, this.n);
	}

	public String encrypt(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

			}
		}
		return sb.toString();
	}

}
