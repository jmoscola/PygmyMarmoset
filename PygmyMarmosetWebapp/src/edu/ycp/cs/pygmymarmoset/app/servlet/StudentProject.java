package edu.ycp.cs.pygmymarmoset.app.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Route(pattern="/u/project/*", view="/_view/studentProject.jsp")
@Navigation(parent=StudentCourse.class)
@CrumbSpec(text="Project %p", items={PathInfoItem.COURSE_ID, PathInfoItem.STUDENT_ID, PathInfoItem.PROJECT_ID})
public class StudentProject extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO: load submissions
		delegateToView(req, resp);
	}
}