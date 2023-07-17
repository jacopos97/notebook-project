package com.examples.notebook.model;

import java.util.Objects;

public class Note {

	private String id;
	private String date;
	private String title;
	private String body;
	
	public Note(String date, String title, String body) {
		this.date = date;
		this.title = title;
		this.body = body;
		this.id = this.date + "-" + this.title;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, date, id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		var other = (Note) obj;
		return Objects.equals(body, other.body) && Objects.equals(date, other.date) && Objects.equals(id, other.id)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return id;
	}

}
