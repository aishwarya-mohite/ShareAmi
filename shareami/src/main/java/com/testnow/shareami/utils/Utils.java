package com.testnow.shareami.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testnow.shareami.exception.InvalidArgumentsException;
import com.testnow.shareami.model.CmdLineArguments;

public class Utils {
	public static final String JAR = "shareAmi-generator-0.0.1.jar";

	public static void example() {
		StringBuffer cmdBuffer = new StringBuffer();
		cmdBuffer.append("java -jar ").append(JAR).append(" ").append(CmdLineArguments.INPUT_JSON_FILE_PATH.value())
				.append("=/tmp/input.json");
		System.out.println(
				"#===========================================================================================================================#");
		System.out.println("How to use ? \nExample : ");
		System.out.println("input.json : " + cmdBuffer);
		System.out.println(
				"#===========================================================================================================================#");
	}

	public static String validArgumentsExample() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Following are the valid arguments : \n");
			buffer.append(CmdLineArguments.INPUT_JSON_FILE_PATH.value()).append("\n");
		
		return buffer.toString();
	}

	public static void validateArguments(Map<String, String> arguments) {
		if (arguments != null && arguments.size() > 0) {
				String value = arguments.get(CmdLineArguments.INPUT_JSON_FILE_PATH.value());
				if (value == null || value.trim().length() == 0) {
					throw new InvalidArgumentsException("Required command line argument \"" + CmdLineArguments.INPUT_JSON_FILE_PATH.value()
							+ "\" is not passed while running command.");
				}
				validateArgumentValue(arguments.get(CmdLineArguments.INPUT_JSON_FILE_PATH.value()), CmdLineArguments.INPUT_JSON_FILE_PATH);
		} else {
			throw new InvalidArgumentsException(
					"Required command line arguments are not passed while running command.\n"
							+ validArgumentsExample());
		}
	}

	public static void validateArgumentValue(String value, CmdLineArguments arg) {
		switch (arg) {
		case INPUT_JSON_FILE_PATH:
			checkIfValidFilePresent(value);
			break;
		default:
			throw new InvalidArgumentsException(arg.value() + " is not valid argument.\n" + validArgumentsExample());
		}
	}

	public static void checkIfValidDirPresent(String dirPath) {
		if (!new File(dirPath).isDirectory()) {
			throw new InvalidArgumentsException(dirPath + " is not valid directory.");
		}
	}

	public static void checkIfValidFilePresent(String fileNameWithPath) {
		File file = new File(fileNameWithPath);
		if (file.isDirectory() || !file.exists())
			throw new InvalidArgumentsException(fileNameWithPath + " is not valid file.");
	}

	public static JsonNode readJsonFromFileAsTree(String filePath) {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = null;
		try {
			is = new FileInputStream(new File(filePath));
			JsonNode node = mapper.readTree(is);
			return node;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Object parseBoolean(Object value) {
		if (value instanceof Boolean) {
			value = (Boolean) value;
		} else if (value instanceof String) {
			String v = (String) value;
			if ("true".equalsIgnoreCase(v) || "false".equalsIgnoreCase(v)) {
				value = Boolean.parseBoolean(v);
			}
		}
		return value;
	}

	public static Object parseNumber(Object value) {
		if (value instanceof Long) {
			value = (Long) value;
		} else if (value instanceof String) {
			String v = (String) value;
			try {
				Long parseLong = Long.parseLong(v);
				value = parseLong;
			} catch (NumberFormatException e) {

			}
		}
		return value;
	}
}