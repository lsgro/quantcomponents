package com.ib.client;

public class AnyWrapperMsgGenerator {
    public static String error( Exception ex) { return "Error - " + ex;}
    public static String error( String str) { return str;}

	public static String error(int id, int errorCode, String errorMsg) {
		String err = Integer.toString(id);
        err += " | ";
        err += Integer.toString(errorCode);
        err += " | ";
        err += errorMsg;
        return err;
	}

	public static String connectionClosed() {
		return "Connection Closed";
	}
}
