package com.examples.notebook.repository.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;

public class NotesMySqlRepository implements NotesRepository {

	private Connection connection;

	private static final Logger LOGGER = LogManager.getLogger(NotesMySqlRepository.class);

	public NotesMySqlRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Note> findAll() {
		var noteList = new ArrayList<Note>();
		try {
			var tableRow = connection.createStatement().executeQuery("select * from notebook.Notes");
			while (tableRow.next())
				noteList.add(fromRowElementToNote(tableRow));
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
		return noteList;
	}

	@Override
	public Note findById(String id) {
		Note note = null;
		try {
			var tableRow = connection.createStatement()
					.executeQuery("select * from notebook.Notes where Id='" + id + "'");
			if (tableRow.next())
				note = fromRowElementToNote(tableRow);
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
		return note;
	}

	@Override
	public void save(Note noteToAdd) {
		String query = " insert into notebook.Notes values (?, ?, ?, ?)";
		try {
			var preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, noteToAdd.getDate());
			preparedStmt.setString(2, noteToAdd.getTitle());
			preparedStmt.setString(3, noteToAdd.getBody());
			preparedStmt.setString(4, noteToAdd.getId());
			preparedStmt.execute();
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}

	}

	@Override
	public void delete(String idNoteToDelete) {
		try {
			connection.createStatement().executeUpdate("delete from notebook.Notes where Id='" + idNoteToDelete + "'");
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}

	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		try {
			connection.createStatement()
					.executeUpdate("update notebook.Notes set " + "NoteDate='" + noteModified.getDate() + "', Title='"
							+ noteModified.getTitle() + "', Body='" + noteModified.getBody() + "', Id='"
							+ noteModified.getId() + "' where Id='" + idNoteToModify + "'");
		} catch (SQLException e) {
			LOGGER.error("SQLException", e);
		}
	}

	private Note fromRowElementToNote(ResultSet row) throws SQLException {
		return new Note(row.getString("NoteDate"), row.getString("Title"), row.getString("Body"));
	}

}
