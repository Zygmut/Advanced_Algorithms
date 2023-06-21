package Services.Comunication.Request;

/**
 * This class contains all the possible request codes that a request can have.
 */
public enum RequestCode {
	CHECK_PRIMALITY,
	GET_FACTORS,
	FETCH_STATS,
	CREATE_DB,
	GET_MESURAMENT,
	ENCRYPT_TEXT,
	DECRYPT_TEXT,
	SAVE_FACTOR_TIME,
	POPULATE_DB,
	GENERATE_RSA_KEYS,
	LOAD_ENCRYPTED_FILE,
	SAVE_ENCRYPTED_FILE,
	GET_STORED_KEYS
}
