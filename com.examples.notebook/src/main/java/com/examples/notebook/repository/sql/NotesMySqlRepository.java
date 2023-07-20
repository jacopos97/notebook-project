package com.examples.notebook.repository.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
		manageSqlConnection(preparedStatement -> {
			var tableRow = preparedStatement.executeQuery();
			while (tableRow.next())
				noteList.add(fromRowElementToNote(tableRow));
		}, query);
		return noteList;
	}

	@Override
	public Note findById(String id) {
		var arrayLength = 1;
		var noteWrapper = new Note[arrayLength];
		var query = "select * from " + tableName + " where Id=?";
		manageSqlConnection(preparedStatement -> {
			preparedStatement.setString(1, id);
			var tableRow = preparedStatement.executeQuery();
			if (tableRow.next())
				noteWrapper[arrayLength - 1] = fromRowElementToNote(tableRow);
		}, query);
		return noteWrapper[arrayLength - 1];
	}

	@Override
	public void save(Note noteToAdd) {
		var query = "insert into " + tableName + " values (?, ?, ?, ?)";
		manageSqlConnection(preparedStatement -> {
			preparedStatement.setString(1, noteToAdd.getDate());
			preparedStatement.setString(2, noteToAdd.getTitle());
			preparedStatement.setString(3, noteToAdd.getBody());
			preparedStatement.setString(4, noteToAdd.getId());
			preparedStatement.executeUpdate();
		}, query);
	}

	@Override
	public void delete(String idNoteToDelete) {
		var query = "delete from " + tableName + " where Id=?";
		manageSqlConnection(preparedStatement -> {
			preparedStatement.setString(1, idNoteToDelete);
			preparedStatement.executeUpdate();
		}, query);
	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		var query = "update " + tableName + " set NoteDate=?, Title=?, Body=?, Id=? where Id=?";
		manageSqlConnection(preparedStatement -> {
			preparedStatement.setString(1, noteModified.getDate());
			preparedStatement.setString(2, noteModified.getTitle());
			preparedStatement.setString(3, noteModified.getBody());
			preparedStatement.setString(4, noteModified.getId());
			preparedStatement.setString(5, idNoteToModify);
			preparedStatement.executeUpdate();
		}, query);
	}

	private Note fromRowElementToNote(ResultSet row) throws SQLException {
		return new Note(row.getString("NoteDate"), row.getString("Title"), row.getString("Body"));
	}
	
	private void manageSqlConnection(ConnectionAction action, String query) {
		try (PreparedStatement preparedStatement = connectionDatabase.prepareStatement(query)){
			action.execute(preparedStatement);
		} catch (SQLException e) {
			LOGGER.error(SQL_EXCEPTION_MESSAGE, e);
		}
	}
	
	@FunctionalInterface
	interface ConnectionAction{
			void execute(PreparedStatement preparedStatement) throws SQLException;
	}

}
