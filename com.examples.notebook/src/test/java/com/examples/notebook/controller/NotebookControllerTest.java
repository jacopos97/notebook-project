package com.examples.notebook.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.notebook.model.Notes;

import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.view.NotebookView;

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
	public void testAllNotes() {
		List<Notes> notes = asList(new Notes());
		when(notesRepository.findAll()).
			thenReturn(notes);
		notebookController.allNotes();
		verify(notebookView).
			showAllNotes(notes);
	}

}
