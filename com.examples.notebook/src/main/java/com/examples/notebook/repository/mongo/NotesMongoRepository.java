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

	public static final String NOTEBOOK_DB_NAME = "notebook";
	public static final String NOTE_COLLECTION_NAME = "note";
	private MongoCollection<Document> noteCollection;

	public NotesMongoRepository(MongoClient client) {
		noteCollection = client
				.getDatabase(NOTEBOOK_DB_NAME)
				.getCollection(NOTE_COLLECTION_NAME);
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
		Document d = noteCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToNote(d);
		return null;
	}

	@Override
	public void save(Note noteToAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Note noteToDelete) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modify(Note noteToModify) {
		// TODO Auto-generated method stub
	}
	
	private Note fromDocumentToNote(Document d) {
		return new Note(""+d.get("date"), ""+d.get("title"), ""+d.get("body"));
	}

}
