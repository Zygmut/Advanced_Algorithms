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

	public String encrypt(File file, final int NUMBER_OF_WORDS_PER_CHUNK) throws IOException{
		StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
           }

        }
        return sb.toString();
	}
}
