package com.examples.notebook.repository;

import java.util.List;

import com.examples.notebook.model.Notes;

public interface NotesRepository {

	public List<Notes> findAll();

}
