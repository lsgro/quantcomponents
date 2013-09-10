package com.quantcomponents.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtils {
	public static String hostname() {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "localhost";
		}
		return hostname;
	}
}
