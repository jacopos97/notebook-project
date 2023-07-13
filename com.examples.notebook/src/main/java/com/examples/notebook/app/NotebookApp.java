package com.examples.notebook.app;

import java.awt.EventQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.examples.notebook.view.swing.NotebookSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class NotebookApp {
	
	private static final Logger LOGGER = LogManager.getLogger(NotebookApp.class);

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				var mongoHost = "localhost";
				var mongoPort = 27017;
				if (args.length > 0)
					mongoHost = args[0];
				if (args.length > 1)
					mongoPort = Integer.parseInt(args[1]);
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
	}
}
