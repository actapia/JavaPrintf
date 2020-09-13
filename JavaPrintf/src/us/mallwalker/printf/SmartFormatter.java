package us.mallwalker.printf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class SmartFormatter {
	private static char firstChar(String s) {
		return s.charAt(0);
	}
	
	private static String stringIdentity(String s) {
		return s;
	}
	
	private static Pattern FORMAT_PATTERN = Pattern.compile("%[0-9]*([abcdefgisx])");
	private String formatString;
	private static HashMap<String,Function> FORMATTER_MAP = null;
	private ArrayList<Function> formatterFunctions;
	
	public SmartFormatter(String fs) {
		if (FORMATTER_MAP == null) {
			FORMATTER_MAP = new HashMap<String,Function>();
			Function<String,Float> parseFloat = Float::parseFloat;
			FORMATTER_MAP.put("s", parseFloat);
			Function<String,Boolean> parseBoolean = Boolean::parseBoolean;
			FORMATTER_MAP.put("b", parseBoolean);
			Function<String,Character> parseCharacter = SmartFormatter::firstChar;
			FORMATTER_MAP.put("c", parseCharacter);
			Function<String,Integer> parseInteger = Integer::parseInt;
			FORMATTER_MAP.put("d", parseInteger);
			FORMATTER_MAP.put("e", parseFloat);
			FORMATTER_MAP.put("f", parseFloat);
			FORMATTER_MAP.put("o", parseInteger);
			Function<String,String> id = SmartFormatter::stringIdentity;
			FORMATTER_MAP.put("s", id);
			FORMATTER_MAP.put("x", parseInteger);
		}
		formatterFunctions = new ArrayList<Function>();
		Matcher matcher = FORMAT_PATTERN.matcher(fs);
		while (matcher.find()) {
			formatterFunctions.add(FORMATTER_MAP.get(matcher.group(1)));
		}
		formatString = fs;
	}
	
	public String getFormatString() {
		return formatString;
	}
	
	public String format(String ... values) {
		if (values.length == 0) {
			return formatString;
		}
		Object[] newValues = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = formatterFunctions.get(i).apply(values[i]);
		}
		return String.format(formatString, newValues);
	}
	
	public static void main(String[] args) {
		String[] escapeArgs = new String[args.length-1];
		for (int i = 1; i < args.length; i++) {
			escapeArgs[i-1] = StringEscapeUtils.unescapeJava(args[i]);
		}
		System.out.print((new SmartFormatter(StringEscapeUtils.unescapeJava(args[0]))).format(escapeArgs));
	}
}
