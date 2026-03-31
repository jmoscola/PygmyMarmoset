// Pygmy Marmoset - an assignment submission webapp for CS courses
// Copyright (c) 2017, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This is free software distributed under the terms of the
// GNU Affero Public License v3 or later.  See LICENSE.txt for details.

package edu.ycp.cs.pygmymarmoset.app.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import edu.ycp.cs.pygmymarmoset.app.controller.CreateSubmissionController;
import edu.ycp.cs.pygmymarmoset.app.model.Project;
import edu.ycp.cs.pygmymarmoset.app.model.PygmyMarmosetException;
import edu.ycp.cs.pygmymarmoset.app.model.Submission;
import edu.ycp.cs.pygmymarmoset.app.model.User;

@Route(pattern="/u/submit/*", view="/_view/studentSubmit.jsp")
@Navigation(parent=StudentProject.class)
@CrumbSpec(text="Upload submission")
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=16*1024*1024)
public class StudentSubmit extends AbstractFormServlet {
	@Serial
	private static final long serialVersionUID = 1L;

	@Override
	protected Params createParams(HttpServletRequest req) {
		// Note that there are no actual model objects.
		return new Params(req);
	}

	@Override
	protected LogicOutcome doLogic(Params params, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Project project = (Project) req.getAttribute("project");
		User student = (User) req.getAttribute("student");
		
		// The model object unmarshaling code doesn't handle blobs,
		// so we do this manually.  But that's fine, it's not really
		// that complicated.
		
		Part filePart;
		try {
			filePart = req.getPart("uploadData");
		} catch (ServletException e) {
			throw new PygmyMarmosetException("Could not retrieve file upload data", e);
		}
		
		CreateSubmissionController createSubmission = new CreateSubmissionController();
		InputStream uploadData = filePart.getInputStream();
		try {
			Submission submission = createSubmission.execute(project, student, filePart.getSubmittedFileName(), uploadData);
			// Success!
			req.setAttribute("resultmsg", "Successfully uploaded submission " + submission.getSubmissionNumber());
			return LogicOutcome.STAY_ON_PAGE;
		} finally {
			IOUtils.closeQuietly(uploadData);
		}
	}

}
