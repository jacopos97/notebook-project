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
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "notebook";
	
	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "note";
	
	private static final Logger LOGGER = LogManager.getLogger(NotebookApp.class);
	
	public static void main(String[] args) {
		new CommandLine(new NotebookApp()).execute(args);
	}
	
	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				var client = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				var notesRepository = new NotesMongoRepository(client , "notebook", "note");
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
