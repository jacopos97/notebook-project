package com.examples.notebook.view;

import java.util.List;

import com.examples.notebook.model.Note;

public interface NotebookView {

	void showAllNotes(List<Note> notes);

	void noteAdded(Note noteAdded);

	void showError(String string, Note note);

	void noteRemoved(Note noteRemoved);

	void noteModified(Note noteModified);

}
