package com.examples.notebook.repository.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class NotesMySqlRepositoryIT {
	
	private Connection connection;
	private NotesMySqlRepository notesMySqlRepository;
	
	private static final Logger LOGGER = LogManager.getLogger(NotesMySqlRepositoryIT.class);
	
	@Before
	public void setUp(){
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/notebook","user","password");
			notesMySqlRepository = new NotesMySqlRepository(connection);
			var truncateStatement = connection.createStatement();
			truncateStatement.executeUpdate("truncate notebook.Notes");
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
	}
	
	@After
	public void tearDown() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
	}

	
	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(notesMySqlRepository.findAll()).isEmpty();;
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestNoteToDatabase("2000/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2000/01/02", "Title2", "Body2");
		assertThat(notesMySqlRepository.findAll())
			.containsExactly(
				new Note("2000/01/01", "Title1", "Body1"),
				new Note("2000/01/02", "Title2", "Body2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(notesMySqlRepository.findById("2000/01/01-Title"))
			.isNull();
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
		assertThat(readAllNotesFromDatabase())
			.containsExactly(note);
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
			var tableRow = connection.createStatement().executeQuery("select * from notebook.Notes");
			while (tableRow.next())
				noteList.add(new Note(tableRow.getString("NoteDate"), tableRow.getString("Title"), tableRow.getString("Body")));
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
		return noteList;
	}

	private void addTestNoteToDatabase(String date, String title, String body) {
		String query = " insert into notebook.Notes values (?, ?, ?, ?)";
		try {
			var preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, date);
			preparedStmt.setString(2, title);
			preparedStmt.setString(3, body);
			preparedStmt.setString(4, date + "-" + title);
			preparedStmt.execute();
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
	}

}
