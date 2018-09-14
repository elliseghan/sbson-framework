package ca.concordia.cs.aseg.sbson.core.codeanalyzer.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Converter {
	private static Converter converter = null;
	private Map<String, Integer> opCodeLookup;
	private BidiMap<String, String> typeLookup;

	private Converter() {
		// Initialize lookup tables for OpCodes and Types
		opCodeLookup = new HashMap<String, Integer>();
		typeLookup = new DualHashBidiMap<String, String>();
		initOpCodeLookup();
		initTypeLookup();
	}

	public static Converter getInstance() {
		if (converter == null)
			converter = new Converter();
		return converter;
	}

	public String convertByteAccessType(int accessType) {
		// Yet to find a better way to extract multiple access types from single
		// opcode
		switch (accessType) {
		case 1:
			return "public";
		case 2:
			return "private";
		case 4:
			return "protected";
		default:
			return null;
		}

	}

	public String convertStringReturnType(String returnType) {
		String prefix = "";
		String array = "";
		if (returnType.contains("[]")) {
			int startPos = returnType.indexOf("[");
			array = returnType.substring(startPos);
			returnType = returnType.substring(0, startPos);
			prefix = array.replace("[]", "[");
		}
		if (typeLookup.containsKey(returnType))
			return prefix + typeLookup.get(returnType);
		else
			return prefix + convertStringClassType(returnType);
	}

	public String convertStringParameterType(String parameterType) {
		return convertStringReturnType(parameterType);
	}

	public int convertStringAccessType(String accessType) {
		int value = -1;
		if (opCodeLookup.containsKey(accessType)) {
			value = opCodeLookup.get(accessType);
		}
		return value;
	}

	public String convertByteReturnType(String returnType) {
		String suffix = "";
		String array = "";
		if (returnType.contains("[")) {
			int endPos = returnType.lastIndexOf("[");
			array = returnType.substring(0, endPos + 1);
			returnType = returnType.substring(endPos + 1);
			for (int i = 0; i < array.length(); i++) {
				suffix = suffix + "[]";
			}
		}
		if (typeLookup.inverseBidiMap().containsKey(returnType))
			return typeLookup.inverseBidiMap().get(returnType) + suffix;
		else
			return convertByteClassType(returnType) + suffix;
	}

	public String convertByteParameterType(String parameterType) {
		return convertByteReturnType(parameterType);
	}

	public String convertByteParameterType(List<String> parameterTypes) {
		String paramString = "";
		for (int i = 0; i < parameterTypes.size() - 1; i++) {
			paramString = paramString
					+ convertByteParameterType(parameterTypes.get(i)) + ",";
		}
		paramString = paramString
				+ convertByteParameterType(parameterTypes.get(parameterTypes
						.size() - 1));
		return paramString;
	}

	private String convertStringClassType(String classType) {
		return "L" + classType + ";";
	}

	private String convertByteClassType(String classType) {
		classType = classType.replace("L", "");
		classType = classType.replace(";", "");
		return classType;
	}

	public static List<String> getParameterList(String byteParamString) {
		List<String> params = new ArrayList<String>();
		String param = "";
		char prevChar = ' ';
		for (int i = 0; i < byteParamString.length(); i++) {
			/*
			 * param starts with either: [ or L or typeChar
			 */
			char ch = byteParamString.charAt(i);
			if (ch == '[') {
				param = param + String.valueOf(ch);
			} else if (ch == 'L') {
				String left = byteParamString.substring(i);
				int pos = left.indexOf(';');
				String str = left.substring(0, pos + 1);
				param = param + str;
				params.add(param);
				i = i + str.length() - 1;
				param = "";
			} else {
				if (prevChar == '[') {
					param = param + String.valueOf(ch);
					params.add(param);
				} else {
					params.add(String.valueOf(ch));
				}
				param = "";
			}
			prevChar = ch;
		}
		return params;
	}

	private void initOpCodeLookup() {
		opCodeLookup.clear();
		opCodeLookup.put("private", 2);
		opCodeLookup.put("public", 1);
		opCodeLookup.put("protected", 4);
		opCodeLookup.put("static", 8);
		opCodeLookup.put("abstract", 1024);
		opCodeLookup.put("final", 16);
	}

	private void initTypeLookup() {
		typeLookup.clear();
		typeLookup.put("boolean", "Z");
		typeLookup.put("byte", "B");
		typeLookup.put("char", "C");
		typeLookup.put("short", "S");
		typeLookup.put("int", "I");
		typeLookup.put("long", "J");
		typeLookup.put("float", "F");
		typeLookup.put("double", "D");
		typeLookup.put("void", "V");
	}

	public static void main(String[] args) {
		List<String> params = Converter
		// .getParameterList("[[ZI[JLString;[LABC;");
		// .getParameterList("ILString;ZB[[I");
				.getParameterList("I[[Ljava/lang/String;[I");
		for (String param : params) {
			System.out.println(param);
		}
		Converter converter = Converter.getInstance();
		System.out.println(converter.convertByteParameterType(params));
	}

}
