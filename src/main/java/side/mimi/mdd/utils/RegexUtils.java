package side.mimi.mdd.utils;

public class RegexUtils {
	public static boolean isAlphanumeric(String input) {
		return input.matches("^[a-zA-Z0-9]+$");
	}
	public static boolean isNumeric(String input) {
		return input.matches("[0-9]+");
	}

}