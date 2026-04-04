package edu.ycp.cs.pygmymarmoset.app.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;

public class RegisterServlets implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent e) {
		ClassLoader classLoader = e.getServletContext().getClassLoader();
		List<String> classNames = loadServletList(classLoader);

		for (String className : classNames) {
			try {
				Class<?> cls = classLoader.loadClass(className);
				if (!AbstractServlet.class.isAssignableFrom(cls)) {
					continue;
				}
				if (Modifier.isAbstract(cls.getModifiers())) {
					continue;
				}
				Class<? extends Servlet> servletClass = cls.asSubclass(Servlet.class);
				Route route = AbstractServlet.getRouteForClass(cls.asSubclass(AbstractServlet.class));
				ServletRegistration.Dynamic reg = e.getServletContext().addServlet(cls.getSimpleName(), servletClass);
				reg.addMapping(route.pattern());
			} catch (ClassNotFoundException ex) {
				System.err.println("RegisterServlets: could not load class " + className + ": " + ex.getMessage());
			}
		}

		// map Welcome servlet to root URL to handle http://host/marmoset/
		ServletRegistration welcomeReg = e.getServletContext().getServletRegistration("Welcome");
		if (welcomeReg != null) {
			welcomeReg.addMapping("/");
			System.out.println("RegisterServlets: mapped Welcome servlet to /");
		} else {
			System.err.println("RegisterServlets: Welcome servlet not found, root URL will show directory listing");
		}
	}

	private List<String> loadServletList(ClassLoader classLoader) {
		List<String> names = new ArrayList<>();
		try (InputStream in = classLoader.getResourceAsStream("servlets.txt")) {
			if (in == null) {
				System.err.println("RegisterServlets: servlets.txt not found in classpath");
				return names;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) {
						names.add(line);
					}
				}
			}
		} catch (IOException ex) {
			System.err.println("RegisterServlets: error reading servlets.txt: " + ex.getMessage());
		}
		return names;
	}

	@Override
	public void contextDestroyed(ServletContextEvent e) {
		// nothing to do
	}
}