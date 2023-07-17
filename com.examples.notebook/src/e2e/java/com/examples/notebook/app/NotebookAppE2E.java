package com.examples.notebook.app;

import static org.assertj.swing.launcher.ApplicationLauncher.*;

import java.util.regex.Pattern;

import javax.swing.JFrame;


import static org.assertj.core.api.Assertions.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class NotebookAppE2E extends AssertJSwingJUnitTestCase {

	private static final String NOTE_FIXTURE_1_DATE = "2001/01/01";
	private static final String NOTE_FIXTURE_1_TITLE = "Title1";
	private static final String NOTE_FIXTURE_1_BODY = "Body1";
	private static final String NOTE_FIXTURE_1_ID = "2001/01/01-Title1";

	private static final String NOTE_FIXTURE_2_DATE = "2002/02/02";
	private static final String NOTE_FIXTURE_2_TITLE = "Title2";
	private static final String NOTE_FIXTURE_2_BODY = "Body2";
	private static final String NOTE_FIXTURE_2_ID = "2002/02/02-Title2";

	private MongoClient mongoClient;
	private FrameFixture window;

	private static final String NOTEBOOK_DB_NAME = "notebook";
	private static final String NOTE_COLLECTION_NAME = "note";

	@Override
	protected void onSetUp() {
		var mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
		var serverAddress = new ServerAddress("localhost", mongoPort);
		mongoClient = new MongoClient(serverAddress);
		mongoClient.getDatabase(NOTEBOOK_DB_NAME).drop();
		addTestNoteToDatabase(NOTE_FIXTURE_1_DATE, NOTE_FIXTURE_1_TITLE, NOTE_FIXTURE_1_BODY);
		addTestNoteToDatabase(NOTE_FIXTURE_2_DATE, NOTE_FIXTURE_2_TITLE, NOTE_FIXTURE_2_BODY);
		application("com.examples.notebook.app.NotebookApp")
				.withArgs(
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

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
			.anySatisfy(note -> assertThat(note).contains(NOTE_FIXTURE_1_ID))
			.anySatisfy(note -> assertThat(note).contains(NOTE_FIXTURE_2_ID));
	}

	@Test
	@GUITest
	public void testAddButtonSuccess() {
		window.textBox("date").enterText("2010/04/05");
		window.textBox("title").enterText("Some Title");
		window.textBox("body").enterText("Some Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents())
				.anySatisfy(note -> assertThat(note).contains("2010/04/05-Some Title"));
	}

	@Test
	@GUITest
	public void testAddButtonErrorCausedByAnAlreadyExistentId() {
		window.textBox("date").enterText(NOTE_FIXTURE_1_DATE);
		window.textBox("title").enterText(NOTE_FIXTURE_1_TITLE);
		window.textBox("body").enterText("Some Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("Change date and/or title. Already exist a note with the same attributes.");
	}

	@Test
	@GUITest
	public void testAddButtonErrorCausedByANotValidDate() {
		window.textBox("date").enterText("2010/44/");
		window.textBox("title").enterText("Some Title");
		window.textBox("body").enterText("Some Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("Note's date must have yyyy/mm/dd form.");
	}

	@Test
	@GUITest
	public void testDeleteButtonSuccess() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.list().contents())
				.noneMatch(note -> note.contains(NOTE_FIXTURE_1_ID));
	}

	@Test
	@GUITest
	public void testDeleteButtonError() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		removeTestNoteFromDatabase(NOTE_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("No existing note with id " + NOTE_FIXTURE_1_ID);
	}

	@Test
	@GUITest
	public void testModifyButtonSuccess() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		window.textBox("date").deleteText().enterText("2010/04/05");
		window.textBox("title").deleteText().enterText("Some Title");
		window.textBox("body").deleteText().enterText("Some Body");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.list().contents())
				.noneMatch(note -> note.contains(NOTE_FIXTURE_1_ID))
				.anySatisfy(note -> assertThat(note).contains("2010/04/05-Some Title"));
	}

	@Test
	@GUITest
	public void testModifyButtonErrorCausedByANotFoundNote() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		window.textBox("date").deleteText().enterText("2010/04/05");
		window.textBox("title").deleteText().enterText("Some Title");
		window.textBox("body").deleteText().enterText("Some Body");
		removeTestNoteFromDatabase(NOTE_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("No existing note with id " + NOTE_FIXTURE_1_ID);
	}

	@Test
	@GUITest
	public void testModifyButtonErrorCausedByAnAlreadyExistentId() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		window.textBox("date").deleteText().enterText(NOTE_FIXTURE_2_DATE);
		window.textBox("title").deleteText().enterText(NOTE_FIXTURE_2_TITLE);
		window.textBox("body").deleteText().enterText("Some Body");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("Change date and/or title. Already exist a note with the same attributes.");
	}

	@Test
	@GUITest
	public void testModifyButtonErrorCausedByANotValidDate() {
		window.list("noteList")
				.selectItem(Pattern.compile(NOTE_FIXTURE_1_ID));
		window.textBox("date").deleteText().enterText("2010/44/");
		window.textBox("title").deleteText().enterText("Some Title");
		window.textBox("body").deleteText().enterText("Some Body");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("Note's date must have yyyy/mm/dd form.");
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

	private void removeTestNoteFromDatabase(String id) {
		mongoClient
				.getDatabase(NOTEBOOK_DB_NAME)
				.getCollection(NOTE_COLLECTION_NAME)
				.deleteOne(Filters.eq("id", id));
	}
}
