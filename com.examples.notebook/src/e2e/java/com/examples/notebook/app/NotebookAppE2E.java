package com.examples.notebook.app;

import static org.assertj.swing.launcher.ApplicationLauncher.*;

import javax.swing.JFrame;


import static org.assertj.core.api.Assertions.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class NotebookAppE2E extends AssertJSwingJUnitTestCase {

	private MongoClient mongoClient;
	private FrameFixture window;

	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	private static final String NOTEBOOK_DB_NAME = "notebook";
	private static final String NOTE_COLLECTION_NAME = "note";

	@Override
	protected void onSetUp() {
		var serverAddress = new ServerAddress("localhost", mongoPort);
		mongoClient = new MongoClient(serverAddress);
		mongoClient.getDatabase(NOTEBOOK_DB_NAME).drop();
		addTestNoteToDatabase("2001/01/01", "Title1", "Body1");
		addTestNoteToDatabase("2002/02/02", "Title2", "Body2");
		application("com.examples.notebook.app.NotebookApp")
			.withArgs(
				"--mongo-host=" + serverAddress,
				"--mongo-port=" + mongoPort,
				"--db-name=" + NOTEBOOK_DB_NAME,
				"--db-collection=" + NOTE_COLLECTION_NAME)
			.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Notebook".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
			.anySatisfy(note -> assertThat(note).contains("2001/01/01-Title1"))
			.anySatisfy(note -> assertThat(note).contains("2002/02/02-Title2"));
	}
	
	private void addTestNoteToDatabase(String date, String title, String body) {
		mongoClient
			.getDatabase(NOTEBOOK_DB_NAME)
			.getCollection(NOTE_COLLECTION_NAME)
			.insertOne(
				new Document()
					.append("date", date)
					.append("title", title)
					.append("body", body)
					.append("id", date + "-" + title));
	}
}
