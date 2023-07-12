package com.examples.notebook.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;
import com.examples.notebook.repository.mongo.NotesMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class NotebookSwingViewIT extends AssertJSwingJUnitTestCase {

	private MongoClient mongoClient;

	private FrameFixture window;
	private NotebookSwingView notebookSwingView;
	private NotesRepository notesRepository;
	private NotebookController notebookController;

	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(new ServerAddress("localhost", mongoPort));
		notesRepository = new NotesMongoRepository(mongoClient);
		for (var note : notesRepository.findAll()) {
			notesRepository.delete(note.getId());
		}
		GuiActionRunner.execute(() -> {
			notebookSwingView = new NotebookSwingView();
			notebookController = new NotebookController(notesRepository, notebookSwingView);
			notebookSwingView.setNotebookController(notebookController);
			return notebookSwingView;
		});
		window = new FrameFixture(robot(), notebookSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testGetAllNotes() {
		var note1 = new Note("2000/01/01", "Title1", "Body1");
		var note2 = new Note("2000/01/02", "Title2", "Body2");
		notesRepository.save(note1);
		notesRepository.save(note2);
		GuiActionRunner.execute(() -> notebookController.getAllNotes());
		assertThat(window.list().contents()).containsExactly(note1.toString(), note2.toString());
	}

	@Test
	@GUITest
	public void testAddButtonSuccess() {
		window.textBox("date").enterText("2000/01/01");
		window.textBox("title").enterText("Title");
		window.textBox("body").enterText("Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).containsExactly(new Note("2000/01/01", "Title", "Body").toString());
	}

	@Test
	@GUITest
	public void testAddButtonErrorCausedByNotValidDate() {
		window.textBox("date").enterText("20004/01/051");
		window.textBox("title").enterText("Title");
		window.textBox("body").enterText("Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).isEmpty();
		window.label("errorMessageLabel").requireText("Note's date must have yyyy/mm/dd form.");
	}

	@Test
	@GUITest
	public void testAddButtonErrorCausedByAlreadyExistentId() {
		notesRepository.save(new Note("2000/01/01", "Title", "Body"));
		window.textBox("date").enterText("2000/01/01");
		window.textBox("title").enterText("Title");
		window.textBox("body").enterText("Body");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).isEmpty();
		window.label("errorMessageLabel")
			.requireText("Change date and/or title. Already exist a note with the same attributes.");
	}

	@Test
	@GUITest
	public void testDeleteButtonSuccess() {
		GuiActionRunner.execute(
			() -> notebookController.addNote(new Note("2000/01/01", "Title", "Body")));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteButtonError() {
		var note = new Note("2000/01/01", "Title", "Body");
		GuiActionRunner.execute(
			() -> notebookSwingView.getListNotesModel().addElement(note));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete")).click();
		assertThat(window.list().contents()).containsExactly(note.toString());
		window.label("errorMessageLabel").requireText("No existing note with id " + note.getId());
	}
	
	@Test
	@GUITest
	public void testModifyButtonSuccess() {
		GuiActionRunner.execute(
			() -> notebookController.addNote(new Note("2000/01/01", "Title", "Body")));
		window.list().selectItem(0);
		window.textBox("date").deleteText().enterText("2000/01/02");
		window.textBox("title").deleteText().enterText("NewTitle");
		window.textBox("body").deleteText().enterText("NewBody");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.list().contents()).containsExactly(new Note("2000/01/02", "NewTitle", "NewBody").toString());
	}
	
	@Test
	@GUITest
	public void testModifyButtonErrorCausedByNotValidDate() {
		var note = new Note("2000/01/01", "Title", "Body");
		GuiActionRunner.execute(
			() -> notebookController.addNote(note));
		window.list().selectItem(0);
		window.textBox("date").deleteText().enterText("20004/01/051");
		window.textBox("title").deleteText().enterText("NewTitle");
		window.textBox("body").deleteText().enterText("NewBody");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.list().contents()).containsExactly(note.toString());
		window.label("errorMessageLabel").requireText("Note's date must have yyyy/mm/dd form.");
	}
	
	@Test
	@GUITest
	public void testModifyButtonErrorCausedByAlreadyExistentId() {
		var note1 = new Note("2000/01/01", "Title1", "Body1");
		var note2 = new Note("2000/01/02", "Title2", "Body2");
		GuiActionRunner.execute(() -> {
			notebookController.addNote(note1);
			notebookController.addNote(note2);
		});
		window.list().selectItem(0);
		window.textBox("date").deleteText().enterText("2000/01/02");
		window.textBox("title").deleteText().enterText("Title2");
		window.textBox("body").deleteText().enterText("NewBody");
		window.button(JButtonMatcher.withText("Modify")).click();
		assertThat(window.list().contents()).containsExactly(note1.toString(), note2.toString());
		window.label("errorMessageLabel")
			.requireText("Change date and/or title. Already exist a note with the same attributes.");
	}

}
