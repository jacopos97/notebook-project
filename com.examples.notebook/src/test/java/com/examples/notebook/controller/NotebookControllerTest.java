package com.examples.notebook.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

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
	
	/*@Test
	public void testGetAllNotes() {
		List<Note> notes = asList(new Note("2000/01/01", "Title", "Body"));
		when(notesRepository.findAll()).
			thenReturn(notes);
		notebookController.getAllNotes();
		verify(notebookView).
			showAllNotes(notes);
	}*/
	
	@Test
	public void testAddNoteWhenNoteDoesNotAlreadyExist() {
		Note note = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01"+"-"+"Title")).
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
		Note noteToAdd = new Note(date, title, "BodyOfTheNewNote");
		Note existingNote = new Note(date, title, "BodyOfTheExistingNote");
		when(notesRepository.findById(date + "-" + title)).
			thenReturn(existingNote);
		notebookController.addNote(noteToAdd);
		verify(notebookView).
			showError("Already existing note with id " + date + "-" + title, existingNote);
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}
	
	@Test
	public void testDeleteNoteWhenNoteExists() {
		Note noteToDelete = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01" + "-" + "Title")).
			thenReturn(noteToDelete);
		notebookController.deleteNote(noteToDelete);
		InOrder inOrder = inOrder(notesRepository, notebookView);
		inOrder.verify(notesRepository).delete(noteToDelete);
		inOrder.verify(notebookView).noteRemoved(noteToDelete);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testDeleteNoteWhenNoteDoesNotExist() {
		var date = "2000/01/01";
		var title = "Title";
		Note noteToDelete = new Note(date, title, "Body");
		when(notesRepository.findById(date + "-" + title)).
			thenReturn(null);
		notebookController.deleteNote(noteToDelete);
		verify(notebookView).
			showError("No existing note with id " + date + "-" + title, noteToDelete);
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}
	
	@Test
	public void testModifyNoteWhenNoteExists() {
		Note noteToModify = new Note("2000/01/01", "Title", "Body");
		when(notesRepository.findById("2000/01/01" + "-" + "Title")).
			thenReturn(noteToModify);
		notebookController.modifyNote(noteToModify);
		InOrder inOrder = inOrder(notesRepository, notebookView);
		inOrder.verify(notesRepository).modify(noteToModify);
		inOrder.verify(notebookView).noteModified(noteToModify);
		inOrder.verifyNoMoreInteractions();
	}
	
	@Test
	public void testModifyNoteWhenNoteDoesNoteExist() {
		var date = "2000/01/01";
		var title = "Title";
		Note noteToModify = new Note(date, title, "Body");
		when(notesRepository.findById(date + "-" + title)).
			thenReturn(null);
		notebookController.modifyNote(noteToModify);
		verify(notebookView).
			showError("No existing note with id " + date + "-" + title, noteToModify);
		verifyNoMoreInteractions(ignoreStubs(notesRepository));
	}

}
