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
		Document d = noteCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToNote(d);
		return null;
	}

	@Override
	public void save(Note noteToAdd) {
		noteCollection.insertOne(
				new Document()
					.append("date", noteToAdd.getDate())
					.append("title", noteToAdd.getTitle())
					.append("body", noteToAdd.getBody())
					.append("id", noteToAdd.getId()));
	}

	@Override
	public void delete(String idNoteToDelete) {
		noteCollection.deleteOne(Filters.eq("id", idNoteToDelete));
	}

	@Override
	public void modify(String idNoteToModify, Note noteModified) {
		noteCollection.replaceOne(
				Filters.eq("id", idNoteToModify),
				new Document()
					.append("date", noteModified.getDate())
					.append("title", noteModified.getTitle())
					.append("body", noteModified.getBody())
					.append("id", noteModified.getId()));
	}
	
	private Note fromDocumentToNote(Document d) {
		return new Note(""+d.get("date"), ""+d.get("title"), ""+d.get("body"));
	}

}
