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
		Note existingNote = findNoteInRepository(noteToAdd);
		if (existingNote != null) {
			notebookView.showError(
					"Already existing note with id " + noteToAdd.getDate() + "-" + noteToAdd.getTitle(),
					existingNote);
			return;
		}
		notesRepository.save(noteToAdd);
		notebookView.noteAdded(noteToAdd);
	}

	public void deleteNote(Note noteToDelete) {
		if (findNoteInRepository(noteToDelete) == null) {
			notebookView.showError(
					"No existing note with id " + noteToDelete.getDate() + "-" + noteToDelete.getTitle(),
					noteToDelete);
			return;
		}
		notesRepository.delete(noteToDelete);
		notebookView.noteRemoved(noteToDelete);
	}
	
	public void modifyNote(Note noteToModify) {
		if (findNoteInRepository(noteToModify) == null) {
			notebookView.showError(
					"No existing note with id " + noteToModify.getDate() + "-" + noteToModify.getTitle(),
					noteToModify);
			return;
		}
		notesRepository.modify(noteToModify);
		notebookView.noteModified(noteToModify);
	}
	
	private Note findNoteInRepository(Note noteToAdd) {
		return notesRepository.findById(noteToAdd.getId());
	}

}
