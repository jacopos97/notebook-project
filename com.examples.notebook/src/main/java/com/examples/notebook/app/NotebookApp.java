package com.examples.notebook.app;

import java.awt.EventQueue;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.examples.notebook.repository.sql.NotesMySqlRepository;
import com.examples.notebook.view.swing.NotebookSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@Command(mixinStandardHelpOptions = true)
public class NotebookApp implements Callable<Void> {

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "notebook";
	
	@Option(names = { "--collection-name" }, description = "Collection name")
	private String collectionName = "notes";

	@Option(names = {
			"--mysql-db" }, description = "Database type. true for MySQL, false for MongoDB")
	private boolean mysqlDB = false;

	@Option(names = { "--mysql-root-pw" }, description = "MySQL root password")
	private String mysqlRootPassword = null;

	@Option(names = { "--mysql-user" }, description = "MySQL user")
	private String mysqlUser = null;

	@Option(names = { "--mysql-user-pw" }, description = "MySQL user password")
	private String mysqlUserPassword = null;

	private static final Logger LOGGER = LogManager.getLogger(NotebookApp.class);

	public static void main(String[] args) {
		new CommandLine(new NotebookApp()).execute(args);
	}

	@Override
	public Void call() {
		EventQueue.invokeLater(() -> {
			try {
				if (checkDatabaseNameAndCollectionNameValidity()) {
					NotesRepository notesRepository;
					if (mysqlDB) {
						if (checkMySqlArgumentsInsertion(mysqlRootPassword, mysqlUser, mysqlUserPassword)) {
							var port = Integer.parseInt(System.getProperty("mysql.port", "3306"));
							var url = "jdbc:mysql://localhost:" + port + "/";
							configureDatabase(url);
							notesRepository = new NotesMySqlRepository(
									DriverManager.getConnection(url + databaseName, mysqlUser, mysqlUserPassword),
									collectionName);
						} else {
							LOGGER.error(
									"If you want to use a MySQL database, you must insert: root's password, user and user's password");
							return;
						}
					} else {
						var client = new MongoClient(new ServerAddress(
								"localhost",
								Integer.parseInt(System.getProperty("mongo.port", "27017"))));
						notesRepository = new NotesMongoRepository(client , databaseName, collectionName);
					}
					var notebookView = new NotebookSwingView();
					var notebookController = new NotebookController(notesRepository, notebookView);
					notebookView.setNotebookController(notebookController);
					notebookView.setVisible(true);
					notebookController.getAllNotes();
				} else {
					LOGGER.error(
							"Database and collection names must be only alphabet letters");
				}
			} catch (Exception e) {
				LOGGER.error("Exception", e);
			}
		});
		return null;
	}

	private boolean checkDatabaseNameAndCollectionNameValidity() {
		return databaseName.matches("^[A-Za-z]+$") && collectionName.matches("^[A-Za-z]+$");
	}

	private boolean checkMySqlArgumentsInsertion(String rootPassword, String user, String userPassword) {
		return rootPassword != null && user != null && userPassword != null;
	}

	private void configureDatabase(String url) {
		try (var connection = DriverManager.getConnection(url, "root", mysqlRootPassword);
				var statement = connection.createStatement();) {
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
			var query = "CREATE TABLE IF NOT EXISTS " + databaseName +
					"." + collectionName + "(NoteDate varchar(255), " +
					"Title varchar(255), " + "Body varchar(255), " +
					"Id varchar(255), " + "PRIMARY KEY (Id))";
			statement.executeUpdate(query);
			statement.executeUpdate(
					"GRANT ALL PRIVILEGES ON " + databaseName + ".* TO '" +
					mysqlUser + "'@'%' IDENTIFIED BY '" + mysqlUserPassword + "'");
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
	}

}
