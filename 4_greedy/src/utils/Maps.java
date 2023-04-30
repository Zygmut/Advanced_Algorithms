package utils;

public enum Maps {
	IBIZA_FORMENTERA("Ibiza-Formentera");

	private String message;

	Maps(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}

	public String getMessage() {
		return message;
	}

	public static String[] getMaps() {
		String[] maps = new String[Maps.values().length];
		for (int i = 0; i < Maps.values().length; i++) {
			maps[i] = Maps.values()[i].getMessage();
		}
		return maps;
	}
}
