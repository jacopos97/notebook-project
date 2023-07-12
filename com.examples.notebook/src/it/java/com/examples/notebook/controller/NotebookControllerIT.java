package com.examples.notebook.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.examples.notebook.view.NotebookView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class NotebookControllerIT {

	@Mock
	private NotebookView notebookView;
	
	private NotesRepository notesRepository;
	private NotebookController notebookController;
	
	private AutoCloseable closeable;
	
	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	private static final String NOTEBOOK_DB_NAME = "notebook";
	private static final String NOTE_COLLECTION_NAME = "note";

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		notesRepository = new NotesMongoRepository(
				new MongoClient(
						new ServerAddress("localhost", mongoPort)),
						NOTEBOOK_DB_NAME,
						NOTE_COLLECTION_NAME);
		for (var note : notesRepository.findAll()) {
			notesRepository.delete(note.getId());
		}
		notebookController = new NotebookController(notesRepository, notebookView);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testGetAllNotes() {
		var note = new Note("2000/01/01", "Title", "Body");
		notesRepository.save(note);
		notebookController.getAllNotes();
		verify(notebookView).
			showAllNotes(asList(note));
	}
	
	@Test
	public void testAddNote() {
		var note = new Note("2000/01/01", "Title", "Body");
		notebookController.addNote(note);
		verify(notebookView).noteAdded(note);
	}
	
	@Test
	public void testDeleteNote() {
		var noteToDelete = new Note("2000/01/01", "Title", "Body");
		notesRepository.save(noteToDelete);
		notebookController.deleteNote(noteToDelete);
		verify(notebookView).noteRemoved(noteToDelete);
	}
	
	@Test
	public void testModifyNote() {
		var noteToModify = new Note("2000/01/01", "OldTitle", "OldBody");
		var noteModified = new Note("2002/02/02", "NewTitle", "NewBody");
		notesRepository.save(noteToModify);
		notebookController.modifyNote(noteToModify.getId(), noteModified);
		verify(notebookView).noteModified(noteModified);
	}
}
