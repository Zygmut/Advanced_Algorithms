package Services.Comunication.Request;

/**
 * This class contains all the possible request codes that a request can have.
 */
public enum RequestCode {
	LOAD_DB,
	GET_LANG_NAMES,
	FETCH_LANGS,
	LEVENSHTEIN,
	ADD_RESULT,
	GUESS_LANG,
	GET_STATS,
	GET_ALL_LANGS,
	TRAIN_NAIVE_MODEL,
	SAVE_MODEL_TO_DB,
	LOAD_MODEL_FROM_DB
}
