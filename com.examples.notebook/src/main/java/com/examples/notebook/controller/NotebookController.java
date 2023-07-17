package com.examples.notebook.controller;

import org.apache.commons.validator.GenericValidator;

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
		if (checkNoteValidity(noteToAdd)) {
			notesRepository.save(noteToAdd);
			notebookView.noteAdded(noteToAdd);
		}
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
		if (checkNoteValidity(noteModified)) {
			notesRepository.modify(idNoteToModify, noteModified);
			notebookView.noteModified(noteModified);
		}
	}

	private boolean checkNoteValidity(Note noteModified) {
		if (!GenericValidator.isDate(noteModified.getDate(), "yyyy/mm/dd", true)) {
			notebookView.showError("Note's date must have yyyy/mm/dd form.");
			return false;
		}
		if (notesRepository.findById(noteModified.getId()) != null) {
			notebookView.showError(
					"Change date and/or title. Already exist a note with the same attributes.");
			return false;
		}
		return true;
	}

}
