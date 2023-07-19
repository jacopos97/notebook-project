package com.examples.notebook.app;

import java.awt.EventQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.examples.notebook.view.swing.NotebookSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@Command(mixinStandardHelpOptions = true)
public class NotebookApp implements Callable<Void> {
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "notebook";
	
	//@Option(names = { "--db-collection" }, description = "Collection name")
	private static final String collectionName = "notes";
	
	private static final Logger LOGGER = LogManager.getLogger(NotebookApp.class);
	
	public static void main(String[] args) {
		new CommandLine(new NotebookApp()).execute(args);
	}
	
	@Override
	public Void call() {
		EventQueue.invokeLater(() -> {
			try {
				var client = new MongoClient(new ServerAddress(
					"localhost",
					Integer.parseInt(System.getProperty("mongo.port", "27017"))));
				var notesRepository = new NotesMongoRepository(client , databaseName, collectionName);
				var notebookView = new NotebookSwingView();
				var notebookController = new NotebookController(notesRepository, notebookView);
				notebookView.setNotebookController(notebookController);
				notebookView.setVisible(true);
				notebookController.getAllNotes();
			} catch (Exception e) {
				LOGGER.error("Exception", e);
			}
		});
		return null;
	}

}
