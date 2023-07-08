package com.examples.notebook.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class NotebookSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private NotebookSwingView notebookSwingView;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			notebookSwingView = new NotebookSwingView();
			return notebookSwingView;
		});
		window = new FrameFixture(robot(), notebookSwingView);
		window.show();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Date (yyyy/mm/dd)"));
		window.textBox("date").requireEnabled();
		window.label(JLabelMatcher.withText("Title"));
		window.textBox("title").requireEnabled();
		window.label(JLabelMatcher.withText("Note"));
		window.textBox("note").requireEnabled();
		window.label(JLabelMatcher.withText("Note List"));
		window.list("noteList");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.button(JButtonMatcher.withText("Modify")).requireDisabled();
		window.button(JButtonMatcher.withText("New")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testWhenDateAndTitleAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("date").enterText("2000/01/01");
		window.textBox("title").enterText("Title");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	public void testWhenEitherDateOrTitleAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture date = window.textBox("date");
		JTextComponentFixture title = window.textBox("title");

		date.enterText("2000/01/01");
		title.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		date.setText("");
		title.setText("");

		date.enterText(" ");
		title.enterText("Title");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

}
