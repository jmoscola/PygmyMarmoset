package edu.ycp.cs.pygmymarmoset.app.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class RegisterServlets implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent e) {
		ClassLoader classLoader = e.getServletContext().getClassLoader();
		List<String> classNames = loadServletList(classLoader);

		if (classNames != null) {
			// Uberjar path — use pre-generated servlets.txt
			System.out.println("RegisterServlets: found " + classNames.size() + " class name(s) in servlet list");
			for (String className : classNames) {
				registerServlet(e, classLoader, className);
			}
		} else {
			// IDE path — fall back to Reflections scanning
			System.out.println("RegisterServlets: servlets.txt not found, falling back to Reflections scanning");
			String packageName = RegisterServlets.class.getPackage().getName();
			Reflections r = new Reflections(new ConfigurationBuilder()
					.setUrls(ClasspathHelper.forPackage(packageName, classLoader))
					.setScanners(Scanners.SubTypes)
					.addClassLoaders(classLoader)
			);
			Set<Class<? extends AbstractServlet>> servlets = r.getSubTypesOf(AbstractServlet.class);
			System.out.println("RegisterServlets: Reflections found " + servlets.size() + " servlet(s)");
			for (Class<? extends AbstractServlet> cls : servlets) {
				if (Modifier.isAbstract(cls.getModifiers())) {
					continue;
				}
				registerServletClass(e, cls);
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

	private void registerServlet(ServletContextEvent e, ClassLoader classLoader, String className) {
		try {
			Class<?> cls = classLoader.loadClass(className);
			if (!AbstractServlet.class.isAssignableFrom(cls)) {
				return;
			}
			if (Modifier.isAbstract(cls.getModifiers())) {
				return;
			}
			registerServletClass(e, cls.asSubclass(AbstractServlet.class));
		} catch (ClassNotFoundException ex) {
			System.err.println("RegisterServlets: could not load class " + className + ": " + ex.getMessage());
		}
	}

	private void registerServletClass(ServletContextEvent e, Class<? extends AbstractServlet> cls) {
		Class<? extends Servlet> servletClass = cls.asSubclass(Servlet.class);
		Route route = AbstractServlet.getRouteForClass(cls);
		System.out.println("Registering servlet " + cls.getSimpleName() + " using pattern " + route.pattern());
		ServletRegistration.Dynamic reg = e.getServletContext().addServlet(cls.getSimpleName(), servletClass);
		reg.addMapping(route.pattern());
	}

	// Returns null if servlets.txt is not found, indicating IDE mode
	private List<String> loadServletList(ClassLoader classLoader) {
		try (InputStream in = classLoader.getResourceAsStream("servlets.txt")) {
			if (in == null) {
				return null;
			}
			List<String> names = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) {
						names.add(line);
					}
				}
			}
			return names;
		} catch (IOException ex) {
			System.err.println("RegisterServlets: error reading servlets.txt: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent e) {
		// nothing to do
	}
}