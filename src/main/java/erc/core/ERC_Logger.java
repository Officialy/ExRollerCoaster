package erc.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ERC_Logger {
	 
	public static Logger logger = LogManager.getLogger("ERC");

	public static void trace(String msg) {
		ERC_Logger.logger.trace(msg);
	}
 
	public static void info(String msg) {
		ERC_Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		ERC_Logger.logger.warn(msg);
	}
	
	public static void debugInfo(String msg) {
		info(msg);
	}
 
}