// Pygmy Marmoset - an assignment submission webapp for CS courses
// Copyright (c) 2017, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This is free software distributed under the terms of the
// GNU Affero Public License v3 or later.  See LICENSE.txt for details.

package edu.ycp.cs.pygmymarmoset.main;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Launch from an uberjar.
public class UberjarMain {
	private static final Logger logger = LoggerFactory.getLogger(UberjarMain.class);

	public static void main(String[] args) throws Exception {
		// Check to see if the first argument is an admin command.
		String cmd = args.length > 0 ? args[0] : "";
		if (cmd.equals("createdb")) {
			logger.info("Preparing to create database.");
			NestedJarClassLoader.runMain(UberjarMain.class, "edu.ycp.cs.pygmymarmoset.model.persist.CreateDatabase", trimArgs(args));
		} else {
			logger.info("Preparing to execute daemon controller.");
			UberjarDaemonController controller = new UberjarDaemonController();
			controller.exec(args);
		}
	}
	
	private static List<String> trimArgs(String[] args) {
		return Arrays.asList(args).subList(1, args.length);
	}
}
