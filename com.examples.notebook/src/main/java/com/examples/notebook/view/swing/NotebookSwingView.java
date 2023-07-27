package com.examples.notebook.view.swing;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.examples.notebook.controller.NotebookController;
import com.examples.notebook.model.Note;
import com.examples.notebook.view.NotebookView;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import java.awt.Color;

public class NotebookSwingView extends JFrame implements NotebookView {

	private static final long serialVersionUID = -2049746861681669915L;
	
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

	private transient NotebookController notebookController;

	DefaultListModel<Note> getListNotesModel() {
		return listNotesModel;
	}

	JList<Note> getListNotes() {
		return listNotes;
	}

	JLabel getLblError() {
		return lblError;
	}

	JButton getBtnModify() {
		return btnModify;
	}

	public void setNotebookController(NotebookController notebookController) {
		this.notebookController = notebookController;
	}

	public NotebookSwingView() {
		setTitle("Notebook");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 314);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWidths = new int[] { 0, 0, 90, 0, 73, 0, 0 };
		gblContentPane.rowHeights = new int[] { 0, 0, 0, 5, 0, 0, 20, 0 };
		gblContentPane.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gblContentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gblContentPane);

		JLabel lblListaNote = new JLabel("Note List");
		GridBagConstraints gbcLblListaNote = new GridBagConstraints();
		gbcLblListaNote.insets = new Insets(0, 0, 5, 5);
		gbcLblListaNote.gridx = 0;
		gbcLblListaNote.gridy = 0;
		contentPane.add(lblListaNote, gbcLblListaNote);

		JScrollPane scrollPaneList = new JScrollPane();
		GridBagConstraints gbcScrollPaneList = new GridBagConstraints();
		gbcScrollPaneList.fill = GridBagConstraints.BOTH;
		gbcScrollPaneList.gridheight = 4;
		gbcScrollPaneList.gridwidth = 2;
		gbcScrollPaneList.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneList.gridx = 0;
		gbcScrollPaneList.gridy = 1;
		contentPane.add(scrollPaneList, gbcScrollPaneList);

		listNotesModel = new DefaultListModel<>();
		listNotes = new JList<>(listNotesModel);
		listNotes.addListSelectionListener(e -> {
			btnDelete.setEnabled(listNotes.getSelectedIndex() != -1);
			btnNew.setEnabled(listNotes.getSelectedIndex() != -1);
			if (listNotes.getSelectedIndex() != -1) {
				textDate.setText(listNotes.getSelectedValue().getDate());
				textTitle.setText(listNotes.getSelectedValue().getTitle());
				textBody.setText(listNotes.getSelectedValue().getBody());
				lblError.setText(" ");
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

		JLabel lblData = new JLabel("Date (yyyy-MM-dd)");
		GridBagConstraints gbcLblData = new GridBagConstraints();
		gbcLblData.insets = new Insets(0, 0, 5, 5);
		gbcLblData.anchor = GridBagConstraints.WEST;
		gbcLblData.gridx = 2;
		gbcLblData.gridy = 1;
		contentPane.add(lblData, gbcLblData);

		JLabel lblTitolo = new JLabel("Title");
		GridBagConstraints gbcLblTitolo = new GridBagConstraints();
		gbcLblTitolo.anchor = GridBagConstraints.WEST;
		gbcLblTitolo.insets = new Insets(0, 0, 5, 5);
		gbcLblTitolo.gridx = 3;
		gbcLblTitolo.gridy = 1;
		contentPane.add(lblTitolo, gbcLblTitolo);

		JLabel lblNota = new JLabel("Body");
		GridBagConstraints gbcLblNota = new GridBagConstraints();
		gbcLblNota.anchor = GridBagConstraints.WEST;
		gbcLblNota.insets = new Insets(0, 0, 5, 5);
		gbcLblNota.gridx = 2;
		gbcLblNota.gridy = 3;
		contentPane.add(lblNota, gbcLblNota);

		JScrollPane scrollPaneNote = new JScrollPane();
		GridBagConstraints gbcScrollPaneNote = new GridBagConstraints();
		gbcScrollPaneNote.fill = GridBagConstraints.BOTH;
		gbcScrollPaneNote.gridwidth = 4;
		gbcScrollPaneNote.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneNote.gridx = 2;
		gbcScrollPaneNote.gridy = 4;
		contentPane.add(scrollPaneNote, gbcScrollPaneNote);

		btnNew = new JButton("New");
		btnNew.addActionListener( e -> {
			textDate.setText("");
			textTitle.setText("");
			textBody.setText("");
			listNotes.clearSelection();
			lblError.setText(" ");
		});
		btnNew.setEnabled(false);
		GridBagConstraints gbcBtnNew = new GridBagConstraints();
		gbcBtnNew.insets = new Insets(0, 0, 5, 5);
		gbcBtnNew.gridx = 1;
		gbcBtnNew.gridy = 5;
		contentPane.add(btnNew, gbcBtnNew);

		btnModify = new JButton("Modify");
		getBtnModify().addActionListener(e -> {
			notebookController.modifyNote(
					listNotes.getSelectedValue().getId(),
					new Note(textDate.getText(), textTitle.getText(), textBody.getText()));
			getBtnModify().setEnabled(false);
		});
		getBtnModify().setEnabled(false);
		GridBagConstraints gbcBtnModify = new GridBagConstraints();
		gbcBtnModify.anchor = GridBagConstraints.EAST;
		gbcBtnModify.insets = new Insets(0, 0, 5, 5);
		gbcBtnModify.gridx = 3;
		gbcBtnModify.gridy = 5;
		contentPane.add(getBtnModify(), gbcBtnModify);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(
				e -> notebookController.deleteNote(listNotes.getSelectedValue())
		);
		btnDelete.setEnabled(false);
		GridBagConstraints gbcBtnDelete = new GridBagConstraints();
		gbcBtnDelete.anchor = GridBagConstraints.EAST;
		gbcBtnDelete.insets = new Insets(0, 0, 5, 5);
		gbcBtnDelete.gridx = 4;
		gbcBtnDelete.gridy = 5;
		contentPane.add(btnDelete, gbcBtnDelete);

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(e -> {
			notebookController.addNote(new Note(textDate.getText(), textTitle.getText(), textBody.getText()));
			btnAdd.setEnabled(false);
			textDate.setText("");
			textTitle.setText("");
			textBody.setText("");
		});
		btnAdd.setEnabled(false);
		GridBagConstraints gbcBtnAdd = new GridBagConstraints();
		gbcBtnAdd.anchor = GridBagConstraints.EAST;
		gbcBtnAdd.insets = new Insets(0, 0, 5, 0);
		gbcBtnAdd.gridx = 5;
		gbcBtnAdd.gridy = 5;
		contentPane.add(btnAdd, gbcBtnAdd);

		lblError = new JLabel(" ");
		getLblError().setForeground(new Color(224, 27, 36));
		getLblError().setName("errorMessageLabel");
		GridBagConstraints gbcLblError = new GridBagConstraints();
		gbcLblError.fill = GridBagConstraints.HORIZONTAL;
		gbcLblError.gridwidth = 6;
		gbcLblError.gridx = 0;
		gbcLblError.gridy = 6;
		contentPane.add(getLblError(), gbcLblError);

		textDate = new JTextField();
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(
						!textDate.getText().trim().isEmpty() &&
						!textTitle.getText().trim().isEmpty() &&
						listNotes.isSelectionEmpty());
			}
		};
		KeyAdapter btnModifyEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getBtnModify().setEnabled(
						!textDate.getText().trim().isEmpty() &&
						!textTitle.getText().trim().isEmpty() &&
						!listNotes.isSelectionEmpty());
			}
		};
		textDate.addKeyListener(btnAddEnabler);
		textDate.addKeyListener(btnModifyEnabler);
		textDate.setName("date");
		GridBagConstraints gbcTextFieldDate = new GridBagConstraints();
		gbcTextFieldDate.fill = GridBagConstraints.HORIZONTAL;
		gbcTextFieldDate.insets = new Insets(0, 0, 5, 5);
		gbcTextFieldDate.gridx = 2;
		gbcTextFieldDate.gridy = 2;
		contentPane.add(textDate, gbcTextFieldDate);
		textDate.setColumns(10);

		textTitle = new JTextField();
		textTitle.addKeyListener(btnAddEnabler);
		textTitle.addKeyListener(btnModifyEnabler);
		textTitle.setName("title");
		GridBagConstraints gbcTextFieldTitle = new GridBagConstraints();
		gbcTextFieldTitle.gridwidth = 3;
		gbcTextFieldTitle.fill = GridBagConstraints.HORIZONTAL;
		gbcTextFieldTitle.insets = new Insets(0, 0, 5, 0);
		gbcTextFieldTitle.gridx = 3;
		gbcTextFieldTitle.gridy = 2;
		contentPane.add(textTitle, gbcTextFieldTitle);
		textTitle.setColumns(10);
		
		textBody = new JTextArea();
		scrollPaneNote.setViewportView(textBody);
		textBody.addKeyListener(btnModifyEnabler);
		textBody.setLineWrap(true);
		textBody.setName("body");
	}

	@Override
	public void showAllNotes(List<Note> notes) {
		notes.stream().forEach(listNotesModel::addElement);
	}

	@Override
	public void noteAdded(Note noteAdded) {
		listNotesModel.addElement(noteAdded);
		resetErrorlabel();
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
		lblError.setText(" ");
	}

}
