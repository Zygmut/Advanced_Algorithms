package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

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
				byte[] bytes = line.getBytes();
				System.out.println("Encrypting: " + Arrays.toString(bytes));
				// Contacatenate all bytes
				StringBuilder sb2 = new StringBuilder();
				for (byte b : bytes) {
					sb2.append(b);
				}
				BigInteger message = new BigInteger(sb2.toString());
				System.out.println("Encrypting(Message): " + message);
				String encrypted = this.encrypt(message).toString();
				System.out.println("Encrypting: " + encrypted);
				sb.append(encrypted);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
