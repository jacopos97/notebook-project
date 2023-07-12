package com.examples.notebook.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.model.Note;

@RunWith(GUITestRunner.class)
public class NotebookSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private NotebookSwingView notebookSwingView;
	
	@Mock
	private NotebookController notebookController;
	
	private AutoCloseable closeable;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			notebookSwingView = new NotebookSwingView();
			notebookSwingView.setNotebookController(notebookController);
			return notebookSwingView;
		});
		window = new FrameFixture(robot(), notebookSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Date (yyyy/mm/dd)"));
		window.textBox("date").requireEnabled();
		window.label(JLabelMatcher.withText("Title"));
		window.textBox("title").requireEnabled();
		window.label(JLabelMatcher.withText("Note"));
		window.textBox("body").requireEnabled();
		window.label(JLabelMatcher.withText("Note List"));
		window.list("noteList");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify")).requireDisabled();
		window.button(JButtonMatcher.withText("New")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testWhenDateAndTitleAreNonEmptyAndNoElementFromTheListIsSelectedThenAddButtonShouldBeEnabled() {
		window.textBox("date").enterText("2000/01/01");
		window.textBox("title").enterText("Title");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	
	@Test
	public void testWhenDateAndTitleAreNonEmptyAndAnElementFromTheListIsSelectedThenAddButtonShouldBeDisabled() {
		GuiActionRunner.execute(
				() -> notebookSwingView.getListNotesModel().addElement(new Note("2000/01/01", "Title", "Body")));
		window.list("noteList").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}
	@Test
	public void testWhenEitherDateOrTitleAreBlankThenAddButtonShouldBeDisabled() {
		var date = window.textBox("date");
		var title = window.textBox("title");

		date.enterText("2000/01/01");
		title.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		date.setText("");
		title.setText("");

		date.enterText(" ");
		title.enterText("Title");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testDeleteButtonAndModifyButtonAndNewButtonShouldBeEnabledOnlyWhenANoteIsSelected() {
		GuiActionRunner.execute(
				() -> notebookSwingView.getListNotesModel().addElement(new Note("2000/01/01", "Title", "Body")));
		window.list("noteList").selectItem(0);
		var deleteButton = window.button(JButtonMatcher.withText("Delete"));
		var modifyButton = window.button(JButtonMatcher.withText("Modify"));
		var newButton = window.button(JButtonMatcher.withText("New"));
		deleteButton.requireEnabled();
		modifyButton.requireEnabled();
		newButton.requireEnabled();
		window.list("noteList").clearSelection();
		deleteButton.requireDisabled();
		modifyButton.requireDisabled();
		newButton.requireDisabled();
	}
	
	@Test
	public void testDateTextAndTitleTextAndNoteTextShouldBeShownOnlyWhenANoteIsSelected() {
		GuiActionRunner.execute(
				() -> notebookSwingView.getListNotesModel().addElement(new Note("2000/01/01", "Title", "Body")));
		window.list("noteList").selectItem(0);
		var date = window.textBox("date");
		var title = window.textBox("title");
		var note = window.textBox("body");
		assertThat(date.text()).isEqualTo("2000/01/01");
		assertThat(title.text()).isEqualTo("Title");
		assertThat(note.text()).isEqualTo("Body");
		window.list("noteList").clearSelection();
		assertThat(date.text()).isEmpty();
		assertThat(title.text()).isEmpty();
		assertThat(note.text()).isEmpty();
	}

	@Test
	public void testsShowAllNotesShouldAddNoteDescriptionsToTheList() {
		Note note1 = new Note("2000/01/01", "Title1", "Body1");
		Note note2 = new Note("2000/01/02", "Title2", "Body2");
		GuiActionRunner.execute(() -> notebookSwingView.showAllNotes(Arrays.asList(note1, note2)));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(note1.toString(), note2.toString());
	}

	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(() -> notebookSwingView.showError("error message"));
		window.label("errorMessageLabel").requireText("error message");
	}

	@Test
	public void testNoteAddedShouldAddTheNoteToTheListAndResetTheErrorLabel() {
		var note = new Note("2000/01/01", "Title", "Body");
		GuiActionRunner.execute(() -> {
			var errorLabel = notebookSwingView.getLblError();
			errorLabel.setText("error");
		});
		GuiActionRunner.execute(() -> notebookSwingView.noteAdded(new Note("2000/01/01", "Title", "Body")));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(note.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testNoteRemovedShouldRemoveTheNoteFromTheListAndResetTheErrorLabel() {
		var note1 = new Note("2000/01/01", "Title1", "Body1");
		var note2 = new Note("2000/01/02", "Title2", "Body2");
		GuiActionRunner.execute(() -> {
			var listNotesModel = notebookSwingView.getListNotesModel();
			listNotesModel.addElement(note1);
			listNotesModel.addElement(note2);
			var errorLabel = notebookSwingView.getLblError();
			errorLabel.setText("error");
		});
		GuiActionRunner.execute(
			() -> notebookSwingView.noteRemoved(new Note("2000/01/01", "Title1", "Body1"))
		);
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(note2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testNoteModifiedShouldModifyTheNoteFromTheListAndResetTheErrorLabel() {
		var oldNote = new Note("2000/01/01", "OldTitle", "OldBody");
		var newNote = new Note("2000/01/02", "NewTitle", "NewBody");
		GuiActionRunner.execute(() -> {
			notebookSwingView.getListNotesModel().addElement(oldNote);
			var listNotes = notebookSwingView.getListNotes();
			listNotes.setSelectedValue(oldNote, false);
			var errorLabel = notebookSwingView.getLblError();
			errorLabel.setText("error");
		});
		GuiActionRunner.execute(
			() -> notebookSwingView.noteModified(newNote));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(newNote.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	

	@Test
	public void testAddButtonShouldDelegateToNotebookControllerAddNote() {
		window.textBox("date").enterText("2000/01/01");
		window.textBox("title").enterText("Title");
		window.textBox("body").enterText("Body");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(notebookController).addNote(new Note("2000/01/01", "Title", "Body"));
	}
	
	@Test
	public void testDeleteButtonShouldDelegateToNotebookControllerDeleteNote() {
		var note1 = new Note("2000/01/01", "Title1", "Body1");
		var note2 = new Note("2000/01/02", "Title2", "Body2");
		GuiActionRunner.execute(() -> {
			var listNotesModel = notebookSwingView.getListNotesModel();
			listNotesModel.addElement(note1);
			listNotesModel.addElement(note2);
		});
		window.list("noteList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete")).click();
		verify(notebookController).deleteNote(note2);
	}
	
	@Test
	public void testModifyButtonShouldDelegateToNotebookControllerModifyNote() {
		GuiActionRunner.execute(
				() -> notebookSwingView.getListNotesModel().addElement(new Note("2000/01/01", "Title", "Body")));
		window.list("noteList").selectItem(0);
		window.textBox("date").deleteText().enterText("2000/01/02");
		window.textBox("title").deleteText().enterText("NewTitle");
		window.textBox("body").deleteText().enterText("NewBody");
		window.button(JButtonMatcher.withText("Modify")).click();
		verify(notebookController).modifyNote("2000/01/01-Title", new Note("2000/01/02", "NewTitle", "NewBody"));
	}
	
	@Test
	public void testNewButtonShouldClearTextBoxesAndDeselectNoteFromTheList() {
		GuiActionRunner.execute(
				() -> notebookSwingView.getListNotesModel().addElement(new Note("2000/01/01", "Title", "Body")));
		window.list("noteList").selectItem(0);
		window.button(JButtonMatcher.withText("New")).click();
		assertThat(window.textBox("date").text()).isEmpty();
		assertThat(window.textBox("title").text()).isEmpty();
		assertThat(window.textBox("body").text()).isEmpty();
		window.list("noteList").requireNoSelection();
	}
}
