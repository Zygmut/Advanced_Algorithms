package Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import Controller.Cryptography;

public record PrivateKey(BigInteger d, BigInteger n) {

	public BigInteger decrypt(BigInteger encrypted) {
        return Cryptography.modularExponentiation(encrypted, this.d, this.n);
    }

	public String decrypt(File file, final int BUFFER_SIZE) throws IOException{
		StringBuilder sb = new StringBuilder();

		try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
			byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = 0;

			while((bytesRead = bis.read(buffer)) != -1 ) {
                for (int i = 0; i < bytesRead; i++) {
                    sb.append(decrypt(BigInteger.valueOf(buffer[i]))).append(" ");
                }
			}

		}

        return sb.toString();
	}

}
