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

	private static final String SELECT_ALL_NOTES = "select * from notebook.Notes";
	private static final String SELECT_NOTE_BY_ID = "select * from notebook.Notes where Id=?";
	private static final String SAVE_NOTE = "insert into notebook.Notes values (?, ?, ?, ?)";
	private static final String DELETE_NOTE = "delete from notebook.Notes where Id=?";
	private static final String MODIFY_NOTE = "update notebook.Notes set NoteDate=?, Title=?, Body=?, Id=? where Id=?";
	private static final String SQL_EXCEPTION_MESSAGE = "SQLException";

	private Connection connection;

	private static final Logger LOGGER = LogManager.getLogger(NotesMySqlRepository.class);

	public NotesMySqlRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Note> findAll(){
		var noteList = new ArrayList<Note>();
		try (var preparedStatement = connection.prepareStatement(SELECT_ALL_NOTES)) {
			var tableRow = preparedStatement.executeQuery();
			while (tableRow.next())
				noteList.add(fromRowElementToNote(tableRow));
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		} 
		return noteList;
	}

	@Override
	public Note findById(String id) {
		Note note = null;
		try (var preparedStatement = connection.prepareStatement(SELECT_NOTE_BY_ID)) {
			preparedStatement.setString(1, id);
			var tableRow = preparedStatement.executeQuery();
			if (tableRow.next())
				note = fromRowElementToNote(tableRow);
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
		return note;
	}

	@Override
	public void save(Note noteToAdd) {
		try (var preparedStatement = connection.prepareStatement(SAVE_NOTE)) {
			preparedStatement.setString(1, noteToAdd.getDate());
			preparedStatement.setString(2, noteToAdd.getTitle());
			preparedStatement.setString(3, noteToAdd.getBody());
			preparedStatement.setString(4, noteToAdd.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}

	@Override
	public void delete(String idNoteToDelete) {
		try (var preparedStatement = connection.prepareStatement(DELETE_NOTE)) {
			preparedStatement.setString(1, idNoteToDelete);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}

	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		try (var preparedStatement = connection.prepareStatement(MODIFY_NOTE)) {
			preparedStatement.setString(1, noteModified.getDate());
			preparedStatement.setString(2, noteModified.getTitle());
			preparedStatement.setString(3, noteModified.getBody());
			preparedStatement.setString(4, noteModified.getId());
			preparedStatement.setString(5, idNoteToModify);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}

	private Note fromRowElementToNote(ResultSet row) throws SQLException {
		return new Note(row.getString("NoteDate"), row.getString("Title"), row.getString("Body"));
	}

}
