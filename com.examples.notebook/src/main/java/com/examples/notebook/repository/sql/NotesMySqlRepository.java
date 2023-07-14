package com.examples.notebook.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;

public class NotesMySqlRepository implements NotesRepository {

	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			var con = DriverManager.getConnection("jdbc:mysql://localhost:3306/notebook", "user", "password");
			var stmt = con.createStatement();
			var rs = stmt.executeQuery("select * from Notes");
			
			String query = " insert into notebook.Notes"+ " values (?, ?, ?, ?)";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString(1, "2000/10/10");
			preparedStmt.setString(2, "Title");
			preparedStmt.setString(3, "Body");
			preparedStmt.setString(4, "2000/10/10-Title");
			preparedStmt.execute();
			
			while (rs.next())
				System.out.println(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getString(3)+ "  " + rs.getString(4));
			/*var metadata = con.getMetaData();
            ResultSet resultSet = metadata.getTables(null, null, null, null);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.println(tableName);*/
            //resultSet.close();
			con.close();
		}catch(

	Exception e)
	{
		System.out.println(e);
	}
	}

	public NotesMySqlRepository(Connection connection) {
		// connection
	}

	@Override
	public List<Note> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Note findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Note noteToAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String idNoteToDelete) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modify(String idNoteToModify, Note noteToModify) {
		// TODO Auto-generated method stub

	}

}
