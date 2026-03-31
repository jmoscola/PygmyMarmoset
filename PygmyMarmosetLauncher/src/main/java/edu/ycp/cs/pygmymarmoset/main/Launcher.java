// Pygmy Marmoset - an assignment submission webapp for CS courses
// Copyright (c) 2017, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This is free software distributed under the terms of the
// GNU Affero Public License v3 or later.  See LICENSE.txt for details.

package edu.ycp.cs.pygmymarmoset.main;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.webapp.Configuration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.ee10.annotations.AnnotationConfiguration;
import org.eclipse.jetty.ee10.webapp.FragmentConfiguration;
import org.eclipse.jetty.ee10.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.ee10.webapp.MetaInfConfiguration;
import org.eclipse.jetty.ee10.webapp.WebInfConfiguration;
import org.eclipse.jetty.ee10.webapp.WebXmlConfiguration;

public class Launcher {
	/**
	 * Create a {@link Server}, but do not start it.
	 *
	 * @param launchFromIDE true if launching from an IDE during debug (rather than from uberjar)
	 * @param port which port to listen on
	 * @param warUrl the URL of the webapp war directory
	 */
	public Server launch(boolean launchFromIDE, int port, String warUrl) throws Exception {
		Server server = new Server(port);

		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mbContainer);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/marmoset");
		webapp.setWar(warUrl);

		// Allow the webapp classloader to see Jetty's own servlet listener and JSP classes
		webapp.getHiddenClassMatcher().exclude("org.eclipse.jetty.ee10.servlet.");
		webapp.getHiddenClassMatcher().exclude("org.eclipse.jetty.ee10.jsp.");
		webapp.getHiddenClassMatcher().exclude("org.eclipse.jetty.ee10.apache.jsp.");

		onCreateWebAppContext(webapp);

		// In Jetty 12, configurations are set directly on the WebAppContext.
		// Jetty automatically handles ordering via topological sort.
		webapp.setConfigurations(new Configuration[] {
				new WebInfConfiguration(),
				new WebXmlConfiguration(),
				new MetaInfConfiguration(),
				new FragmentConfiguration(),
				new AnnotationConfiguration(),
				new JettyWebXmlConfiguration()
		});

		webapp.setAttribute(
				"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/[^/]*servlet-api-[^/]*\\.jar$" +
						"|.*/jakarta.servlet.jsp.jstl-.*\\.jar$" +
						"|.*/[^/]*taglibs.*\\.jar$" +
						"|.*/jetty-ee10-apache-jsp-[^/]*\\.jar$" +
						"|.*/mortbay-apache-jsp-[^/]*\\.jar$" +
						"|.*/jetty-ee10-servlet-[^/]*\\.jar$"
		);

		// Allow webapp to see server-side JSP and servlet classes
		webapp.setParentLoaderPriority(true);

		// Don't allow directory listings
		webapp.setInitParameter("org.eclipse.jetty.ee10.servlet.Default.dirAllowed", "false");

		// Allow the welcome file to be a servlet
		webapp.setInitParameter("org.eclipse.jetty.ee10.servlet.Default.welcomeServlets", "true");

		if (launchFromIDE) {
			webapp.setExtraClasspath(
					"../PygmyMarmosetModelClasses/build/classes/java/main/," +
							"../PygmyMarmosetPersistence/build/classes/java/main/," +
							"../PygmyMarmosetPersistence/build/resources/main/," +
							"../PygmyMarmosetControllers/build/classes/java/main/," +
							"../PygmyMarmosetWebapp/build/classes/java/main/"
			);
		}

		server.setHandler(webapp);

		return server;
	}

	protected void onCreateWebAppContext(WebAppContext webapp) {
		// Does nothing by default, subclasses may override
	}
}