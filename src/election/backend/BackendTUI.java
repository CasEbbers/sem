package election.backend;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import election.model.Body;
import election.model.Person;
import election.model.Poll;
import election.model.Seat;

/**
 * The Backend maintains all the information
 * for multiple elections, and has the options
 * to add, modify and delete elections, bodies
 * and persons.
 */
public class BackendTUI {

    /** Scanner to scan for user input */
	private Scanner keyboard = new Scanner(System.in);

    /** List of Person objects */
	public List<Person> people = new ArrayList<Person>();
    /** List of Body objects */
	public List<Body> bodies = new ArrayList<Body>();
    /** List of Poll objects */
	public List<Poll> polls = new ArrayList<Poll>();
	
	private int nextPollNumber = 1;

    /**
     * Shows the main menu of the Backend.
     */
	public void mainMenu() {
		while(true) {
			clearScreen();
			System.out.println("1) Manage bodies                   [MAIN MENU]");
			System.out.println("2) Manage people                              ");
			System.out.print("Choice [1-2]: ");
			
			switch(getChoice()) {
			case 1:
				bodiesMenu();
				break;
			case 2:
				peopleMenu();
				break;
			default:
				break;
			}
		}
	}

    /**
     * Shows the "Manage Bodies" menu.
     */
	private void bodiesMenu() {
		while(true) {
			clearScreen();
			System.out.println("1) Add new body                [MANAGE BODIES]");
			System.out.println("2) Search for body                            ");
			System.out.println("3) *** Back to main                           ");
			System.out.print("Choice [1-3]: ");
			
			switch(getChoice()) {
			case 1:
				addBodyMenu();
				break;
			case 2:
				searchBodyMenu();
				break;
			case 3:
				return;
			default:
				break;
			}
		}
	}

    /**
     * Creates the option to add a Body.
     */
	private void addBodyMenu() {
		try {
			System.out.print("Enter name              > ");
			String name = keyboard.nextLine();
			System.out.print("Enter number of seats   > ");
			int numSeats = Integer.parseInt(keyboard.nextLine());
			
			List<Seat> seats = new ArrayList<Seat>();
			for(int i = 0; i < numSeats; i++) {
				System.out.print("Name for seat " + i + "         > ");
				String seatName = keyboard.nextLine();
				seats.add(new Seat(seatName));
			}
			
			System.out.print("Add body [" + name + "] with " + numSeats + " seats? Enter '1' to confirm: ");
			
			switch(getChoice()) {
			case 1:
				bodies.add(new Body(name, seats));
				return;
			default:
				System.out.println("Not confirmed, aborting insertion.");
				pause();
				return;
			}
		} catch (Exception e) {
			System.out.println("Input error, aborting insertion.");
			pause();
			return;
		}
	}

    /**
     * Class to search for an existing Body.
     */
	private void searchBodyMenu() {
		List<Body> results = new ArrayList<Body>();
		
		try {
			System.out.print("Enter search query   > ");
			String query = keyboard.nextLine();
			
			for(Body body : bodies) {
				if(body.getName().contains(query)) {
					results.add(body);
				}
			}
			
			if(results.size() > 15) {
				System.out.println("Too many results, try a narrower search.");
				pause();
				return;
			} else if(results.size() == 0) {
				System.out.println("No results.");
				pause();
				return;
			}
			
			int counter = 1;
			System.out.println("0) *** Cancel");
			for(Body body : results) {
				System.out.println("" + counter++ + ") " + body);
			}
			System.out.print("Pick a result: ");
			int result = getChoice();
			
			if(result == 0 || result < 0 || result > results.size()) {
				return;
			}
			
			bodyMenu(results.get(result - 1));
		} catch(Exception e) {
			
		}
	}

    /**
     * Options menu for a specific Body.
     * @param b selected Body
     * @throws ParseException
     */
	private void bodyMenu(Body b) throws ParseException {
		clearScreen();
		System.out.println("*** BODY: [" + b + "]");
		System.out.println("1) Edit");
		System.out.println("2) Delete");
		System.out.println("3) Add seat");
		System.out.println("4) Remove seat");
		System.out.println("5) *** Abort");
		if(b.inElection()) {
			System.out.println("6) Finalize election");
		} else {
			System.out.println("6) Start election");
		}
		System.out.print("Choice [1-5]: ");
		
		switch(getChoice()) {
		case 1:
			System.out.print("New name? Leave blank for no change      > ");
			String name = keyboard.nextLine();
			if(name.length() > 0) b.setName(name);
			
			System.out.println("Saved " + b);
			pause();
			return;
		case 2:
			System.out.print("Confirm delete? Enter '1': ");
			if(getChoice() == 1) {
				bodies.remove(b);
				System.out.println("Body removed! Press a key to continue...");
				pause();
			}
			return;
		case 3:
			System.out.print("Enter seat name: ");
			b.getSeats().add(new Seat(keyboard.nextLine()));
			return;
		case 4:
			System.out.println("0) *** Abort");
			int counter = 1;
			for(Seat s : b.getSeats()) {
				System.out.println("" + counter++ + ") " + s.getName());
			}
			int choice = getChoice();
			if(choice == 0 || choice < 0 || choice > b.getSeats().size()) {
				return;
			}
			Seat s = b.getSeats().remove(choice - 1);
			System.out.println("Seat " + s + " removed!");
			pause();
			return;
		case 5:
		default:
			return;
		case 6:
			if(b.inElection()) {
				// finalize
			} else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				System.out.print("Enter election date    > ");
				String dateString = keyboard.nextLine();
				Date date = format.parse(dateString);
				
				List<Seat> seats = new ArrayList<Seat>();
				System.out.print("Enter seats to be elected (ids, comma-separated) > ");
				String seatString = keyboard.nextLine();
				for(String id : seatString.split(",")) {
					seats.add(b.getSeats().get(Integer.parseInt(id)));
				}
				
				List<Person> candidates = new ArrayList<Person>();
				System.out.print("Enter candidates (ids, comma-separated) > ");
				String candidateString = keyboard.nextLine();
				for(String id : candidateString.split(",")) {
					candidates.add(people.get(Integer.parseInt(id)));
				}
				
				System.out.print("How many polling stations?  > ");
				int numPolls = Integer.parseInt(keyboard.nextLine());
				
				List<Poll> polls = b.startElection(date, seats, candidates, this.people, this.nextPollNumber, numPolls);
				this.polls.addAll(polls);
				System.out.println("Election created, polling station range [" + this.nextPollNumber + " - " + (this.nextPollNumber + numPolls - 1) + "]");
				this.nextPollNumber += numPolls;
				pause();
			}
		}
	}

    /**
     * Shows the menu to "Manage People".
     */
	private void peopleMenu() {
		while(true) {
			clearScreen();
			System.out.println("1) Add new person              [MANAGE PEOPLE]");
			System.out.println("2) Search for person                          ");
			System.out.println("3) *** Back to main                           ");
			System.out.print("Choice [1-3]: ");
			
			switch(getChoice()) {
			case 1:
				addPersonMenu();
				break;
			case 2:
				searchPersonMenu();
				break;
			case 3:
				return;
			default:
				break;
			}
		}
	}

    /**
     * Creates the option to add a Person.
     */
	private void addPersonMenu() {
		try {
			System.out.print("Enter name      > ");
			String name = keyboard.nextLine();
			System.out.print("Enter address   > ");
			String address = keyboard.nextLine();
			System.out.print("Add person [" + name + ", " + address + "]? Enter '1' to confirm: ");
			
			switch(getChoice()) {
			case 1:
				people.add(new Person(name, address));
				return;
			default:
				System.out.println("Not confirmed, aborting insertion.");
				pause();
				return;
			}
		} catch (Exception e) {
			System.out.println("Input error, aborting insertion.");
			pause();
			return;
		}
	}

    /**
     * Search for a Person.
     */
	private void searchPersonMenu() {
		List<Person> results = new ArrayList<Person>();
		
		try {
			System.out.print("Enter search query   > ");
			String query = keyboard.nextLine();
			
			for(Person person : people) {
				if(person.getName().contains(query)) {
					results.add(person);
				}
			}
			
			if(results.size() > 15) {
				System.out.println("Too many results, try a narrower search.");
				pause();
				return;
			} else if(results.size() == 0) {
				System.out.println("No results.");
				pause();
				return;
			}
			
			int counter = 1;
			System.out.println("0) *** Cancel");
			for(Person person : results) {
				System.out.println("" + counter + ") " + person.getName() + ", " + person.getAddress());
			}
			System.out.print("Pick a result: ");
			int result = getChoice();
			
			if(result == 0 || result < 0 || result > results.size()) {
				return;
			}
			
			personMenu(results.get(result - 1));
		} catch(Exception e) {
			
		}
	}

    /**
     * Options menu for a specific Person.
     * @param p selected Person
     */
	private void personMenu(Person p) {
		clearScreen();
		System.out.println("*** PERSON: [" + p.getName() + ", " + p.getAddress() + "]");
		System.out.println("1) Edit");
		System.out.println("2) Delete");
		System.out.println("3) *** Abort");
		System.out.print("Choice [1-3]: ");
		
		switch(getChoice()) {
		case 1:
			System.out.print("New name? Leave blank for no change      > ");
			String name = keyboard.nextLine();
			if(name.length() > 0) p.setName(name);
			System.out.print("New address? Leave blank for no change   > ");
			String address = keyboard.nextLine();
			if(address.length() > 0) p.setAddress(address);
			
			System.out.println("Saved " + p);
			pause();
			return;
		case 2:
			System.out.print("Confirm delete? Enter '1': ");
			if(getChoice() == 1) {
				people.remove(p);
				System.out.println("Person removed! Press a key to continue...");
				pause();
			}
			return;
		case 3:
		default:
			return;
		}
	}

    /**
     * Clears the screen and prints the title bar.
     */
	private void clearScreen() {
		for(int i = 0; i < 25; i++) { System.out.println(); }
		System.out.println("          OOTUMLIA ELECTION MANAGEMENT        ");
		System.out.println("         SERVER ADMINISTRATION TERMINAL       ");
		System.out.println("______________________________________________");
	}

    /**
     * Waits for the user to make a choice.
     * @return choice the integer the user chose
     */
	private int getChoice() {
		Scanner line = new Scanner(keyboard.nextLine());
		int result;
		
		if(line.hasNextInt()) {
			result = line.nextInt();
		} else {
			result = -1;
		}
		
		line.close();
		return result;
	}
	
	private void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) throws IOException {
		// boot server
		BackendTUI backend = new BackendTUI();
		BackendServer server = new BackendServer(9667, backend);
		server.listen();

		backend.mainMenu();
	}

}
