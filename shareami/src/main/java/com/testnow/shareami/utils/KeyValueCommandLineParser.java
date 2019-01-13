package com.testnow.shareami.utils;

import java.util.HashMap;
import java.util.Map;

import com.testnow.shareami.exception.CommandLineParseException;

public class KeyValueCommandLineParser implements CommandLineParser<Map<String, String>> {

	public Map<String, String> parse(String[] args) {
		Map<String, String> arguments = new HashMap<String, String>();
		if (args != null && args.length > 0) {
			for (String arg : args) {
				arg = arg.trim();
				if (arg.indexOf("=") > 0) {
					String[] split = arg.split("=");
					arguments.put(split[0].trim(), split[1].trim());
				} else {
					throw new CommandLineParseException(
							"Invalid argument " + arg + ". Argument must be in the form of key=value");
				}
			}
		}
		return arguments;
	}

}
