package com.examples.notebook.controller;

import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.view.NotebookView;

public class NotebookController {
	
	private NotesRepository notesRepository;
	private NotebookView notebookView;

	public NotebookController(NotesRepository notesRepository, NotebookView notebookView) {
		this.notesRepository = notesRepository;
		this.notebookView = notebookView;
	}

	public void allNotes() {
		notebookView.showAllNotes(notesRepository.findAll());
	}

}
