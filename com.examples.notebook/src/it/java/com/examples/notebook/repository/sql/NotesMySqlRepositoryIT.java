package com.examples.notebook.repository.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.notebook.model.Note;

public class NotesMySqlRepositoryIT {
	
	private static final String DATABASE_NAME = "notebook";
	private static final int port = Integer.parseInt(System.getProperty("mysql.port", "3306"));
	private static final String url = "jdbc:mysql://localhost:" + port + "/";

	private static final String CREATE_DATABASE_NOTEBOOK = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
	private static final String CREATE_TABLE_NOTES = "CREATE TABLE IF NOT EXISTS " +
														DATABASE_NAME +".notes" +
														"(NoteDate varchar(255), " +
														"Title varchar(255), " +
														"Body varchar(255), " +
														"Id varchar(255), " +
														"PRIMARY KEY (Id))";

	private static final String SELECT_ALL_NOTES = "select * from notes";
	private static final String SAVE_NOTE = "insert into notes values (?, ?, ?, ?)";
	private static final String SQL_EXCEPTION_MESSAGE = "SQLException";

	private static Connection connection;
	private NotesMySqlRepository notesMySqlRepository;

	private static final Logger LOGGER = LogManager.getLogger(NotesMySqlRepositoryIT.class);

	@BeforeClass
	public static void setUpServer() {
		try {
			connection = DriverManager.getConnection(url, "root", "secret");
			var statement = connection.createStatement();
			statement.executeUpdate(CREATE_DATABASE_NOTEBOOK);
			statement.executeUpdate(CREATE_TABLE_NOTES);
			statement.executeUpdate("GRANT ALL PRIVILEGES ON " + DATABASE_NAME + ".* TO 'user'@'%' IDENTIFIED BY 'password'");
			connection.close();
		} catch (Exception e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}

	@Before
	public void setUp() {
		try {
			connection = DriverManager.getConnection(url + DATABASE_NAME,"user","password");
			notesMySqlRepository = new NotesMySqlRepository(connection);
			var statement = connection.createStatement();
			statement.executeUpdate("truncate notes");
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}

	@After
	public void tearDown() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}
	
	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(notesMySqlRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestNoteToDatabase("2000/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2000/01/02", "Title2", "Body2");
		assertThat(notesMySqlRepository.findAll()).containsExactly(new Note("2000/01/01", "Title1", "Body1"),
				new Note("2000/01/02", "Title2", "Body2"));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(notesMySqlRepository.findById("2000/01/01-Title")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestNoteToDatabase("2000/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2000/01/02", "Title2", "Body2");
		assertThat(notesMySqlRepository.findById("2000/01/02-Title2"))
				.isEqualTo(new Note("2000/01/02", "Title2", "Body2"));
	}

	@Test
	public void testSave() {
		var note = new Note("2000/01/01", "Title", "Body");
		notesMySqlRepository.save(note);
		assertThat(readAllNotesFromDatabase()).containsExactly(note);
	}

	@Test
	public void testDelete() {
		addTestNoteToDatabase("2000/01/01", "Title", "Body");
		notesMySqlRepository.delete("2000/01/01-Title");
		assertThat(readAllNotesFromDatabase()).isEmpty();
	}

	@Test
	public void testModify() {
		addTestNoteToDatabase("2000/01/01", "OldTitle", "OldBody");
		var noteModified = new Note("2001/02/02", "NewTitle", "NewBody");
		notesMySqlRepository.modify("2000/01/01-OldTitle", noteModified);
		assertThat(readAllNotesFromDatabase()).containsExactly(noteModified);
	}

	private List<Note> readAllNotesFromDatabase() {
		var noteList = new ArrayList<Note>();
		try {
			var tableRow = connection.prepareStatement(SELECT_ALL_NOTES).executeQuery();
			while (tableRow.next())
				noteList.add(new Note(tableRow.getString("NoteDate"), tableRow.getString("Title"),
						tableRow.getString("Body")));
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
		return noteList;
	}

	private void addTestNoteToDatabase(String date, String title, String body) {
		try {
			var preparedStatement = connection.prepareStatement(SAVE_NOTE);
			preparedStatement.setString(1, date);
			preparedStatement.setString(2, title);
			preparedStatement.setString(3, body);
			preparedStatement.setString(4, date + "-" + title);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}

}
