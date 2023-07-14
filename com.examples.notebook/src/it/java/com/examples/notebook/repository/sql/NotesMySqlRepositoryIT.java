package com.examples.notebook.repository.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class NotesMySqlRepositoryIT {
	
	private NotesMySqlRepository notesMySqlRepository;
	
	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database","user","password");
		notesMySqlRepository = new NotesMySqlRepository(connection);
		/*client = new MongoClient(new ServerAddress("localhost", mongoPort));
		var database = client.getDatabase(NOTEBOOK_DB_NAME);
		database.drop();
		notesCollection = database.getCollection(NOTE_COLLECTION_NAME);*/
	}
	
	@After
	public void tearDown() {
		//client.close();
	}

	
	/*@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(notesMongoRepository.findAll()).isEmpty();;
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestNoteToDatabase("2000/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2000/01/02", "Title2", "Body2");
		assertThat(notesMongoRepository.findAll())
			.containsExactly(
				new Note("2000/01/01", "Title1", "Body1"),
				new Note("2000/01/02", "Title2", "Body2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(notesMongoRepository.findById("2000/01/01-Title"))
			.isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		addTestNoteToDatabase("2000/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2000/01/02", "Title2", "Body2");
		assertThat(notesMongoRepository.findById("2000/01/02-Title2"))
			.isEqualTo(new Note("2000/01/02", "Title2", "Body2"));
	}
	
	@Test
	public void testSave() {
		var note = new Note("2000/01/01", "Title", "Body");
		notesMongoRepository.save(note);
		assertThat(readAllNotesFromDatabase())
			.containsExactly(note);
	}
	
	@Test
	public void testDelete() {
		addTestNoteToDatabase("2000/01/01", "Title", "Body");
		notesMongoRepository.delete("2000/01/01-Title");
		assertThat(readAllNotesFromDatabase()).isEmpty();
	}
	
	@Test
	public void testModify() {
		addTestNoteToDatabase("2000/01/01", "OldTitle", "OldBody");
		var noteModified = new Note("2001/02/02", "NewTitle", "NewBody");
		notesMongoRepository.modify("2000/01/01-OldTitle", noteModified);
		assertThat(readAllNotesFromDatabase()).containsExactly(noteModified);
	}

	private List<Note> readAllNotesFromDatabase() {
		return StreamSupport
				.stream(notesCollection.find().spliterator(), false)
				.map(d -> new Note(""+d.get("date"), ""+d.get("title"), ""+d.get("body")))
				.toList();
	}

	private void addTestNoteToDatabase(String date, String title, String body) {
		notesCollection.insertOne(
				new Document()
				.append("date", date)
				.append("title", title)
				.append("body", body)
				.append("id", date + "-" + title));
	}*/

}
