package it.jasonravagli.gym.model;

import java.util.List;
import java.util.UUID;

public class Course {
	private UUID id;
	private String name;
	private List<Member> subscribers;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Member> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<Member> subscribers) {
		this.subscribers = subscribers;
	}
}
