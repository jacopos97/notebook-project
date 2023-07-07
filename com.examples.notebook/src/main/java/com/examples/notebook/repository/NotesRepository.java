package com.examples.notebook.repository;

import java.util.List;

import com.examples.notebook.model.Note;

public interface NotesRepository {

	public List<Note> findAll();

	public Note findById(String id);

	public void save(Note noteToAdd);

	public void delete(String idNoteToDelete);

	public void modify(String idNoteToModify, Note noteToModify);

}
