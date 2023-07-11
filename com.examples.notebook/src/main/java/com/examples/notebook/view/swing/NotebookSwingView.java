package com.examples.notebook.view.swing;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.validator.GenericValidator;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.model.Note;
import com.examples.notebook.view.NotebookView;
import javax.swing.JLabel;
import javax.swing.JTextField;
//import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultListModel;
//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.forms.layout.ColumnSpec;
//import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import java.awt.Color;

public class NotebookSwingView extends JFrame implements NotebookView {

	private JPanel contentPane;
	private JTextField textDate;
	private JTextField textTitle;
	private JTextArea textBody;
	private JList<Note> listNotes;

	private DefaultListModel<Note> listNotesModel;
	private JLabel lblError;
	private JButton btnNew;
	private JButton btnModify;
	private JButton btnDelete;
	private JButton btnAdd;
	
	private NotebookController notebookController;

	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NotebookSwingView frame = new NotebookSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	DefaultListModel<Note> getListNotesModel() {
		return listNotesModel;
	}
	
	JList<Note> getListNotes() {
		return listNotes;
	}

	JLabel getLblError() {
		return lblError;
	}

	public void setNotebookController(NotebookController notebookController) {
		this.notebookController = notebookController;
	}

	/**
	 * Create the frame.
	 */
	public NotebookSwingView() {
		setTitle("Notebook");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 314);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 90, 0, 73, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 5, 0, 0, 20, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblListaNote = new JLabel("Note List");
		GridBagConstraints gbc_lblListaNote = new GridBagConstraints();
		gbc_lblListaNote.insets = new Insets(0, 0, 5, 5);
		gbc_lblListaNote.gridx = 0;
		gbc_lblListaNote.gridy = 0;
		contentPane.add(lblListaNote, gbc_lblListaNote);

		JScrollPane scrollPaneList = new JScrollPane();
		GridBagConstraints gbc_scrollPaneList = new GridBagConstraints();
		gbc_scrollPaneList.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneList.gridheight = 4;
		gbc_scrollPaneList.gridwidth = 2;
		gbc_scrollPaneList.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneList.gridx = 0;
		gbc_scrollPaneList.gridy = 1;
		contentPane.add(scrollPaneList, gbc_scrollPaneList);

		listNotesModel = new DefaultListModel<Note>();
		listNotes = new JList<Note>(listNotesModel);
		listNotes.addListSelectionListener(e -> {
			btnDelete.setEnabled(listNotes.getSelectedIndex() != -1);
			btnModify.setEnabled(listNotes.getSelectedIndex() != -1);
			btnNew.setEnabled(listNotes.getSelectedIndex() != -1);
			if (listNotes.getSelectedIndex() != -1) {
				textDate.setText(listNotes.getSelectedValue().getDate());
				textTitle.setText(listNotes.getSelectedValue().getTitle());
				textBody.setText(listNotes.getSelectedValue().getBody());
			} else {
				textDate.setText("");
				textTitle.setText("");
				textBody.setText("");
			}
		});
		listNotes.setName("noteList");
		listNotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneList.setViewportView(listNotes);
		listNotes.setName("noteList");

		JLabel lblData = new JLabel("Date (yyyy/mm/dd)");
		GridBagConstraints gbc_lblData = new GridBagConstraints();
		gbc_lblData.insets = new Insets(0, 0, 5, 5);
		gbc_lblData.anchor = GridBagConstraints.WEST;
		gbc_lblData.gridx = 2;
		gbc_lblData.gridy = 1;
		contentPane.add(lblData, gbc_lblData);

		JLabel lblTitolo = new JLabel("Title");
		GridBagConstraints gbc_lblTitolo = new GridBagConstraints();
		gbc_lblTitolo.anchor = GridBagConstraints.WEST;
		gbc_lblTitolo.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitolo.gridx = 3;
		gbc_lblTitolo.gridy = 1;
		contentPane.add(lblTitolo, gbc_lblTitolo);

		JLabel lblNota = new JLabel("Note");
		GridBagConstraints gbc_lblNota = new GridBagConstraints();
		gbc_lblNota.anchor = GridBagConstraints.WEST;
		gbc_lblNota.insets = new Insets(0, 0, 5, 5);
		gbc_lblNota.gridx = 2;
		gbc_lblNota.gridy = 3;
		contentPane.add(lblNota, gbc_lblNota);

		JScrollPane scrollPaneNote = new JScrollPane();
		GridBagConstraints gbc_scrollPaneNote = new GridBagConstraints();
		gbc_scrollPaneNote.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneNote.gridwidth = 4;
		gbc_scrollPaneNote.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneNote.gridx = 2;
		gbc_scrollPaneNote.gridy = 4;
		contentPane.add(scrollPaneNote, gbc_scrollPaneNote);

		textBody = new JTextArea();
		scrollPaneNote.setViewportView(textBody);
		textBody.setLineWrap(true);
		textBody.setName("body");

		btnNew = new JButton("New");
		btnNew.addActionListener( e -> {
			textDate.setText("");
			textTitle.setText("");
			textBody.setText("");
			listNotes.clearSelection();
		});
		btnNew.setEnabled(false);
		GridBagConstraints gbc_btnNew = new GridBagConstraints();
		gbc_btnNew.insets = new Insets(0, 0, 5, 5);
		gbc_btnNew.gridx = 1;
		gbc_btnNew.gridy = 5;
		contentPane.add(btnNew, gbc_btnNew);

		btnModify = new JButton("Modify");
		btnModify.addActionListener(
				e -> notebookController.modifyNote(
						listNotes.getSelectedValue().getId(),
						new Note(textDate.getText(), textTitle.getText(), textBody.getText()))
		);
		btnModify.setEnabled(false);
		GridBagConstraints gbc_btnModify = new GridBagConstraints();
		gbc_btnModify.anchor = GridBagConstraints.EAST;
		gbc_btnModify.insets = new Insets(0, 0, 5, 5);
		gbc_btnModify.gridx = 3;
		gbc_btnModify.gridy = 5;
		contentPane.add(btnModify, gbc_btnModify);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(
			e -> notebookController.deleteNote(listNotes.getSelectedValue())
		);
		btnDelete.setEnabled(false);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.anchor = GridBagConstraints.EAST;
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.gridx = 4;
		gbc_btnDelete.gridy = 5;
		contentPane.add(btnDelete, gbc_btnDelete);

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(
			e -> notebookController.addNote(new Note(textDate.getText(), textTitle.getText(), textBody.getText()))
		);
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.anchor = GridBagConstraints.EAST;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 5;
		gbc_btnAdd.gridy = 5;
		contentPane.add(btnAdd, gbc_btnAdd);

		lblError = new JLabel(" ");
		getLblError().setForeground(new Color(224, 27, 36));
		getLblError().setName("errorMessageLabel");
		GridBagConstraints gbc_lblError = new GridBagConstraints();
		gbc_lblError.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblError.gridwidth = 6;
		gbc_lblError.gridx = 0;
		gbc_lblError.gridy = 6;
		contentPane.add(getLblError(), gbc_lblError);

		textDate = new JTextField();
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(!textDate.getText().trim().isEmpty() && !textTitle.getText().trim().isEmpty() && listNotes.isSelectionEmpty());
			}
		};
		textDate.addKeyListener(btnAddEnabler);
		textDate.setName("date");
		GridBagConstraints gbc_textFieldDate = new GridBagConstraints();
		gbc_textFieldDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDate.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldDate.gridx = 2;
		gbc_textFieldDate.gridy = 2;
		contentPane.add(textDate, gbc_textFieldDate);
		textDate.setColumns(10);

		textTitle = new JTextField();
		textTitle.addKeyListener(btnAddEnabler);
		textTitle.setName("title");
		GridBagConstraints gbc_textFieldTitle = new GridBagConstraints();
		gbc_textFieldTitle.gridwidth = 3;
		gbc_textFieldTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldTitle.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldTitle.gridx = 3;
		gbc_textFieldTitle.gridy = 2;
		contentPane.add(textTitle, gbc_textFieldTitle);
		textTitle.setColumns(10);
	}

	@Override
	public void showAllNotes(List<Note> notes) {
		notes.stream().forEach(listNotesModel::addElement);
	}

	@Override
	public void noteAdded(Note noteAdded) {
		//if (checkNoteValidity(noteAdded)) {
		listNotesModel.addElement(noteAdded);
		resetErrorlabel();
		//}
	}

	@Override
	public void showError(String errorMessage) {
		getLblError().setText(errorMessage);
	}

	@Override
	public void noteRemoved(Note noteRemoved) {
		listNotesModel.removeElement(noteRemoved);
		resetErrorlabel();
	}

	@Override
	public void noteModified(Note noteModified) {
		var notePos = listNotes.getSelectedIndex();
		listNotesModel.removeElementAt(notePos);
		listNotesModel.addElement(noteModified);
		resetErrorlabel();
	}

	private void resetErrorlabel() {
		getLblError().setText(" ");
	}

}