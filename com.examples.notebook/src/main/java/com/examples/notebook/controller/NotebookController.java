package com.examples.notebook.controller;

import java.time.LocalDate;

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
		if (checkDateValidity(noteToAdd) && checkIdValidity(noteToAdd)) {
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
		if (checkDateValidity(noteModified)) {
			if (!idNoteToModify.equals(noteModified.getId()) &&
					!checkIdValidity(noteModified)) {
				return;
			}
			notesRepository.modify(idNoteToModify, noteModified);
			notebookView.noteModified(noteModified);
		}
	}
	
	private boolean checkDateValidity(Note noteModified) {
		try {
			LocalDate.parse(noteModified.getDate());
		} catch (Exception e) {
			notebookView.showError("Note's date must have yyyy-MM-dd form and must be valid.");
			return false;
		}
		return true;
	}
	
	private boolean checkIdValidity(Note noteModified) {
		if (notesRepository.findById(noteModified.getId()) != null) {
			notebookView.showError(
					"Change date and/or title. Already exist a note with the same attributes.");
			return false;
		}
		return true;
	}

}
