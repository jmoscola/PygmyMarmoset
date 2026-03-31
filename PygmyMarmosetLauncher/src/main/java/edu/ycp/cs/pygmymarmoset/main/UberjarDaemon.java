package edu.ycp.cs.pygmymarmoset.main;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;

import org.apache.commons.io.FileUtils;
import org.cloudcoder.daemon.IDaemon;
import org.cloudcoder.daemon.Util;
import org.eclipse.jetty.ee10.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.ee10.servlet.ServletContainerInitializerHolder;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UberjarDaemon implements IDaemon {
	private static final Logger logger = LoggerFactory.getLogger(UberjarDaemon.class);

	// Version of Launcher that sets things up to allow JSPs to work.
	private final class UberjarLauncher extends Launcher {
		@Override
		protected void onCreateWebAppContext(WebAppContext webapp) {
			// Register JettyJasperInitializer to enable JSP support
			webapp.addServletContainerInitializer(
					new ServletContainerInitializerHolder(new JettyJasperInitializer())
			);

			try {
				webapp.setAttribute("jakarta.servlet.context.tempdir", getScratchDir());
			} catch (IOException e) {
				throw new IllegalStateException("Could not set servlet context temp dir", e);
			}
		}
	}

	private static final int PORT = 8080; // TODO: make configurable

	private Server server;
	private File tmpdir;

	@Override
	public void start(String instanceName) {
		logger.info("Preparing to launch Jetty server.");

		try {
			String path = "./" + instanceName + "-tmp-" + Util.getPid();
			File tmpdir = new File(path);
			if (!tmpdir.mkdirs()) {
				throw new IOException("Could not create directory " + path);
			}
			System.setProperty("java.io.tmpdir", tmpdir.getAbsolutePath());
			this.tmpdir = tmpdir;
		} catch (Exception e) {
			logger.warn("Error creating instance-specific temp dir: {}", e.getMessage());
			logger.warn("Webapp files will be placed in default temp dir {}", System.getProperty("java.io.tmpdir"));
		}

		// Launch the webapp!
		ProtectionDomain domain = getClass().getProtectionDomain();
		String codeBase = domain.getCodeSource().getLocation().toExternalForm();
		if (!codeBase.endsWith(".jar")) {
			throw new IllegalStateException("Unexpected non-jar codebase: " + codeBase);
		}
		String webappUrl = "jar:" + codeBase + "!/war";
		try {
			Launcher launcher = new UberjarLauncher();
			this.server = launcher.launch(false, PORT, webappUrl);
			this.server.start();
		} catch (Exception e) {
			throw new IllegalStateException("Could not launch Jetty", e);
		}
	}

	private static File getScratchDir() throws IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

		if (!scratchDir.exists()) {
			if (!scratchDir.mkdirs()) {
				throw new IOException("Unable to create scratch directory: " + scratchDir);
			}
		}
		return scratchDir;
	}

	@Override
	public void handleCommand(String command) {
		// No runtime commands are supported yet
		System.out.println("Unsupported command: " + command);
	}

	@Override
	public void shutdown() {
		logger.info("Preparing to shutdown Jetty server.");

		try {
			server.stop();
			server.join();
		} catch (Exception e) {
			throw new IllegalStateException("Exception shutting down Jetty", e);
		}

		// Clean up temp dir
		if (tmpdir != null) {
			try {
				FileUtils.deleteDirectory(tmpdir);
			} catch (IOException e) {
				logger.error("Error deleting temp directory: {}", e.getMessage());
			}
		}
	}
}