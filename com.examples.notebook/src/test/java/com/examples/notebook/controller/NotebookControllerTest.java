package com.examples.notebook.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.view.NotebookView;
import com.examples.notebook.model.Note;

public class NotebookControllerTest {
	
	@Mock
	private NotesRepository notesRepository;
	
	@Mock
	private NotebookView notebookView;
	
	@InjectMocks
	private NotebookController notebookController;
	
	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testGetAllNotes() {
		var notes = asList(new Note("2000/01/01", "Title", "Body"));
		when(notesRepository.findAll()).
			thenReturn(notes);
		notebookController.getAllNotes();
		verify(notebookView).
			showAllNotes(notes);
	}
	
	@Test
	public void testAddNoteWhenNoteDoesNotAlreadyExist() {
		var note = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01-Title")).
			thenReturn(null);
		notebookController.addNote(note);
		InOrder inOrder = inOrder(notesRepository, notebookView);
		inOrder.verify(notesRepository).save(note);
		inOrder.verify(notebookView).noteAdded(note);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testAddNoteWhenNoteAlreadyExists() {
		var date = "2000/01/01";
		var title = "Title";
		var noteToAdd = new Note(date, title, "BodyOfTheNewNote");
		var existingNote = new Note(date, title, "BodyOfTheExistingNote");
		when(notesRepository.findById(date + "-" + title)).
			thenReturn(existingNote);
		notebookController.addNote(noteToAdd);
		verify(notebookView).
			showError("Already existing note with id " + existingNote.getId());
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}
	
	@Test
	public void testDeleteNoteWhenNoteExists() {
		var noteToDelete = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01-Title")).
			thenReturn(noteToDelete);
		notebookController.deleteNote(noteToDelete);
		InOrder inOrder = inOrder(notesRepository, notebookView);
		inOrder.verify(notesRepository).delete(noteToDelete.getId());
		inOrder.verify(notebookView).noteRemoved(noteToDelete);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testDeleteNoteWhenNoteDoesNotExist() {
		var noteToDelete = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01-Title")).
			thenReturn(null);
		notebookController.deleteNote(noteToDelete);
		verify(notebookView).
			showError("No existing note with id " + noteToDelete.getId());
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}
	
	@Test
	public void testModifyNoteWhenNoteDoesNotChangeId() {
		var noteToModify = new Note("2000/01/01", "OldTitle", "OldBody");
		var noteModified = new Note("2002/02/02", "NewTitle", "NewBody");
		when(notesRepository.findById("2000/01/01" + "-" + "OldTitle"))
			.thenReturn(noteToModify);
		notebookController.modifyNote(noteToModify.getId(), noteModified);
		InOrder inOrder = inOrder(notesRepository, notebookView);
		inOrder.verify(notesRepository).modify("2000/01/01-OldTitle", noteModified);
		inOrder.verify(notebookView).noteModified(noteModified);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testModifyNoteWhenNoteDoesNoteExist() {
		var oldId= "2000/01/01-OldTitle";
		Note noteModified = new Note("2000/01/02", "Title", "Body");
		when(notesRepository.findById("2000/01/01-OldTitle")).
			thenReturn(null);
		notebookController.modifyNote("2000/01/01-OldTitle", noteModified);
		verify(notebookView).
			showError("No existing note with id " + oldId);
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}

}
