package edu.ycp.cs.pygmymarmoset.model.persist;

import java.util.List;

import edu.ycp.cs.pygmymarmoset.app.model.Course;
import edu.ycp.cs.pygmymarmoset.app.model.Term;
import edu.ycp.cs.pygmymarmoset.app.model.User;

public interface IDatabase {
	public void createModelClassTable(Class<?> modelCls);
	public User findUserForUsername(String username);
	public boolean createCourse(Course course);
	public boolean createUser(User user);
	public List<Course> getAllCourses();
	public boolean createTerm(Term term);
	public List<Term> getAllTerms();
}
