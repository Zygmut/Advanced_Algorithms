package Model;

import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public enum TableName {
	ENCRYPT_RESULT {
		@Override
		public String getInsertString(Result res) {
			final StringBuilder sb = new StringBuilder();

			sb.append("INSERT INTO ")
					.append(name())
					.append("(time, encryption) VALUES ('")
					.append(gson.toJson(res.time()))
					.append("', '")
					.append(gson.toJson(res.result()))
					.append("');");

			return sb.toString();
		}
	},
	DECRYPT_RESULT {
		@Override
		public String getInsertString(Result res) {
			final StringBuilder sb = new StringBuilder();

			sb.append("INSERT INTO ")
					.append(name())
					.append("(time, decryption) VALUES ('")
					.append(gson.toJson(res.time()))
					.append("', '")
					.append(gson.toJson(res.result()))
					.append("');");

			return sb.toString();
		}
	},
	NEWTON_INTERPOLATION,
	IS_PRIME_RESULT {
		@Override
		public String getInsertString(Result res) {
			final StringBuilder sb = new StringBuilder();

			sb.append("INSERT INTO ")
					.append(name())
					.append("(time, is_prime) VALUES ('")
					.append(gson.toJson(res.time()))
					.append("', '")
					.append((boolean) res.result() ? "1" : "0")
					.append("');");

			return sb.toString();
		}
	},
	GET_FACTOR_RESULT {
		@Override
		public String getInsertString(Result res) {
			final StringBuilder sb = new StringBuilder();

			sb.append("INSERT INTO ")
					.append(name())
					.append("(time, factors) VALUES ('")
					.append(gson.toJson(res.time()))
					.append("', '")
					.append(gson.toJson(res.result()))
					.append("');");

			return sb.toString();
		}
	},
	RSA_KEY_RESULT {
		@Override
		public String getInsertString(Result res) {
			final StringBuilder sb = new StringBuilder();

			sb.append("INSERT INTO ")
					.append(name())
					.append("(time, key_pair) VALUES ('")
					.append(gson.toJson(res.time()))
					.append("', '")
					.append(gson.toJson(res.result()))
					.append("');");

			return sb.toString();

		}
	};

	Gson gson = new GsonBuilder()
			.registerTypeAdapter(Duration.class, new DurationTypeAdapter())
			.create();

	public String getInsertString(Result res) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
