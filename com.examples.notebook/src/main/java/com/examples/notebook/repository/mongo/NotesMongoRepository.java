package com.examples.notebook.repository.mongo;

import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.examples.notebook.model.Note;
import com.examples.notebook.repository.NotesRepository;

public class NotesMongoRepository implements NotesRepository {

	private static final String DATE = "date";
	private static final String TITLE = "title";
	private static final String BODY = "body";
	private static final String ID = "id";
	private MongoCollection<Document> noteCollection;

	public NotesMongoRepository(MongoClient client, String databaseName, String collectionName) {
		noteCollection = client
				.getDatabase(databaseName)
				.getCollection(collectionName);
	}

	@Override
	public List<Note> findAll() {
		return StreamSupport.
				stream(noteCollection.find().spliterator(), false)
				.map(this::fromDocumentToNote)
				.toList();
	}

	@Override
	public Note findById(String id) {
		var d = noteCollection.find(Filters.eq(ID, id)).first();
		if (d != null)
			return fromDocumentToNote(d);
		return null;
	}

	@Override
	public void save(Note noteToAdd) {
		noteCollection.insertOne(
				new Document()
						.append(DATE, noteToAdd.getDate())
						.append(TITLE, noteToAdd.getTitle())
						.append(BODY, noteToAdd.getBody())
						.append(ID, noteToAdd.getId()));
	}

	@Override
	public void delete(String idNoteToDelete) {
		noteCollection.deleteOne(
				Filters.eq(ID, idNoteToDelete));
	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		noteCollection.replaceOne(
				Filters.eq(ID, idNoteToModify),
				new Document()
						.append(DATE, noteModified.getDate())
						.append(TITLE, noteModified.getTitle())
						.append(BODY, noteModified.getBody())
						.append(ID, noteModified.getId()));
	}
	
	private Note fromDocumentToNote(Document d) {
		return new Note(""+d.get(DATE), ""+d.get(TITLE), ""+d.get(BODY));
	}

}
