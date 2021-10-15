package it.jasonravagli.gym.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Member {
	private UUID id;
	private String name;
	private String surname;
	private LocalDate dateOfBirth;
	private List<Course> subscriptions;

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

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public List<Course> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Course> subscriptions) {
		this.subscriptions = subscriptions;
	}
}
