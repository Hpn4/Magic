package magic.logic.utils;

public class Paint {

	private static final String seq = "\u001b[";

	public final static String RESET = seq + "0m";

	public static final String BLACK = seq + "30m";

	public static final String RED = seq + "31m";

	public static final String GREEN = seq + "32m";

	public static final String YELLOW = seq + "33m";

	public static final String BLUE = seq + "34m";

	public static final String MAGENTA = seq + "35m";

	public static final String CYAN = seq + "36m";

	public static final String LIGHT_GREY = seq + "37m";

	public static final String GREY = seq + "90m";

	public static final String B_RED = seq + "91m";

	public static final String B_GREEN = seq + "92m";

	public static final String B_YELLOW = seq + "93m";

	public static final String B_BLUE = seq + "94m";

	public static final String B_MAGENTA = seq + "95m";

	public static final String B_CYAN = seq + "96m";

	public static final String WHITE = seq + "97m";
}
