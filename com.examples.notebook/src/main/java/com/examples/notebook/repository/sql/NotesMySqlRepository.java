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
	
	private static final String SQL_EXCEPTION_MESSAGE = "SQLException";

	private Connection connectionDatabase;
	private String tableName;

	private static final Logger LOGGER = LogManager.getLogger(NotesMySqlRepository.class);

	public NotesMySqlRepository(Connection connection, String tableName) {
		this.connectionDatabase = connection;
		this.tableName = tableName;
	}

	@Override
	public List<Note> findAll(){
		var noteList = new ArrayList<Note>();
		var query = "select * from " + tableName;
		try (var preparedStatement = connectionDatabase.prepareStatement(query)) {
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
		var query = "select * from " + tableName + " where Id=?";
		try (var preparedStatement = connectionDatabase.prepareStatement(query)) {
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
		var query = "insert into " + tableName + " values (?, ?, ?, ?)";
		try (var preparedStatement = connectionDatabase.prepareStatement(query)) {
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
		var query = "delete from " + tableName + " where Id=?";
		try (var preparedStatement = connectionDatabase.prepareStatement(query)) {
			preparedStatement.setString(1, idNoteToDelete);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}

	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		var query = "update " + tableName + " set NoteDate=?, Title=?, Body=?, Id=? where Id=?";
		try (var preparedStatement = connectionDatabase.prepareStatement(query)) {
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
