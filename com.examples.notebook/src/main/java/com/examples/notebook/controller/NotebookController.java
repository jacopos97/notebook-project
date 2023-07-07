package com.examples.notebook.controller;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.view.NotebookView;

public class NotebookController {
	
	private NotesRepository notesRepository;
	private NotebookView notebookView;

	public NotebookController(NotesRepository notesRepository, NotebookView notebookView) {
		this.notesRepository = notesRepository;
		this.notebookView = notebookView;
	}

	public void getAllNotes() {
		notebookView.showAllNotes(notesRepository.findAll());
	}

	public void addNote(Note noteToAdd) {
		if (notesRepository.findById(noteToAdd.getId())!= null) {
			notebookView.showError(
					"Already existing note with id " + noteToAdd.getId());
			return;
		}
		notesRepository.save(noteToAdd);
		notebookView.noteAdded(noteToAdd);
	}

	public void deleteNote(Note noteToDelete) {
		if (notesRepository.findById(noteToDelete.getId()) == null) {
			notebookView.showError(
					"No existing note with id " + noteToDelete.getId());
			return;
		}
		notesRepository.delete(noteToDelete.getId());
		notebookView.noteRemoved(noteToDelete);
	}
	
	public void modifyNote(String idNoteToModify, Note noteModified) {
		if (notesRepository.findById(idNoteToModify) == null) {
			notebookView.showError(
					"No existing note with id " + idNoteToModify);
			return;
		}
		notesRepository.modify(idNoteToModify, noteModified);
		notebookView.noteModified(noteModified);
	}

}
