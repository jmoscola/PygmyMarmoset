package edu.ycp.cs.pygmymarmoset.model.persist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ycp.cs.pygmymarmoset.app.model.Course;
import edu.ycp.cs.pygmymarmoset.app.model.Pair;
import edu.ycp.cs.pygmymarmoset.app.model.PersistenceException;
import edu.ycp.cs.pygmymarmoset.app.model.Project;
import edu.ycp.cs.pygmymarmoset.app.model.Role;
import edu.ycp.cs.pygmymarmoset.app.model.Roles;
import edu.ycp.cs.pygmymarmoset.app.model.Term;
import edu.ycp.cs.pygmymarmoset.app.model.User;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.AddInstructor;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.CreateModelClassTable;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.CreateProject;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.FindCourseForCourseId;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.FindUserForUsername;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.GetAllCourses;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.GetAllTerms;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.GetProjectsInCourse;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.GetRoster;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.InsertModelObject;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.RegisterStudent;
import edu.ycp.cs.pygmymarmoset.model.persist.txn.SuggestUsernames;

public class MariaDBDatabase implements IDatabase {
	public static final String JDBC_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
	static {
		try {
			Class.forName(JDBC_DRIVER_CLASS);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't load MariaDB driver", e);
		}
	}
	
	private static Logger logger = LoggerFactory.getLogger(MariaDBDatabase.class);
	
	// TODO: don't hard-code
	public static final String JDBC_URL =
			"jdbc:mysql://localhost/pygmymarmoset?user=root&password=root";

	@Override
	public void createModelClassTable(Class<?> modelCls) {
		execute(new CreateModelClassTable(modelCls));
	}
	
	@Override
	public User findUserForUsername(String username) {
		return execute(new FindUserForUsername(username));
	}
	
	@Override
	public boolean createCourse(Course course) {
		return execute(new InsertModelObject<Course>(course));
	}
	
	@Override
	public boolean createUser(User user) {
		return execute(new InsertModelObject<User>(user));
	}
	
	@Override
	public boolean createTerm(Term term) {
		return execute(new InsertModelObject<Term>(term));
	}
	
	@Override
	public List<Term> getAllTerms() {
		return execute(new GetAllTerms());
	}
	
	@Override
	public List<Pair<Course,Term>> getAllCourses() {
		return execute(new GetAllCourses());
	}
	
	@Override
	public Pair<Course,Term> findCourseForCourseId(int courseId) {
		return execute(new FindCourseForCourseId(courseId));
	}
	
	@Override
	public boolean registerStudent(User student, Course course, Role role) {
		return execute(new RegisterStudent(student, course, role));
	}
	
	@Override
	public List<Pair<User, Role>> getRoster(Course course) {
		return execute(new GetRoster(course));
	}
	
	@Override
	public boolean addInstructor(Course course, String username, int section) {
		return execute(new AddInstructor(course, username, section));
	}
	
	@Override
	public Roles getUserRolesInCourse(User user, Course course) {
		return execute(new GetUserRolesInCourse(user, course));
	}
	
	@Override
	public List<String> suggestUsernames(String term) {
		return execute(new SuggestUsernames(term));
	}
	
	@Override
	public boolean createProject(Course course, Project project) {
		return execute(new CreateProject(course, project));
	}
	
	@Override
	public List<Project> getProjectsInCourse(Course course) {
		return execute(new GetProjectsInCourse(course));
	}

	private Connection createConnection() {
		try {
			return DriverManager.getConnection(JDBC_URL);
		} catch (SQLException e) {
			throw new PersistenceException("Couldn't create JDBC connection", e);
		}
	}
	
	private void releaseConnection(Connection conn) {
		DBUtil.closeQuietly(conn);
	}
	
	private<E> E execute(DatabaseRunnable<E> txn) {
		Connection conn = createConnection();
		try {
			return doExecute(conn, txn);
		} finally {
			txn.cleanup();
			releaseConnection(conn);
		}
	}

	private<E> E doExecute(Connection conn, DatabaseRunnable<E> txn) {
		final int MAX_ATTEMPTS = 10;
		int attempts = 0;
		
		while (attempts < MAX_ATTEMPTS) {
			try {
				conn.setAutoCommit(false);
				E result = txn.execute(conn);
				conn.commit();
				
				return result;
			} catch (SQLException e) {
				if (isDeadlock(e)) {
					logger.warn("Deadlock detected, retrying transaction " + txn.getName(), e);
					attempts++;
				} else if (e.getSQLState().equals("23000")) {
					throw new PersistenceException("Integrity constraint violation (duplicate field value detected)");
				} else {
					throw new PersistenceException("Transaction " + txn.getName() + " failed", e);
				}
			}
		}
		
		throw new PersistenceException("Gave up retrying transaction " + txn.getName() + " after " + attempts + " attempts");
	}

	private boolean isDeadlock(SQLException e) {
		String sqlState = e.getSQLState();
		return sqlState != null && (sqlState.equals("40001") || sqlState.equals("41000"));
	}

}
