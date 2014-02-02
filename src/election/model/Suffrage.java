package election.model;

import java.io.Serializable;

public class Suffrage implements Serializable {
	
	private Person person;
	
	private Poll poll;
	
	public Suffrage(Person person, Poll poll) {
		this.person = person;
		this.poll = poll;
	}
	
	public Person getPerson() {
		return this.person;
	}
	
	public Poll getPoll() {
		return this.poll;
	}

}
