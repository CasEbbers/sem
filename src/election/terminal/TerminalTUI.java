package election.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import election.model.Candidate;
import election.model.Poll;
import election.model.Suffrage;
import election.model.Tally;
import ocsf.server.ConnectionToClient;

public class TerminalTUI {

    private Scanner keyboard = new Scanner(System.in);

    private int pollID;
    private Poll poll;

    private int clientPort = 9667;
    private int serverPort = 9668;

    private List<ConnectionToClient> clients;

    public TerminalTUI(String host, int pollID) {
        this.pollID = pollID;
        clients = new ArrayList<ConnectionToClient>();
        try {
            new TerminalServer(this.serverPort, this).listen();
            new TerminalClient(host, this.clientPort, this).openConnection();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void mainMenu() {
        while(true) {
            clearScreen();
            System.out.println("1) Search person                   [MAIN MENU]");
            System.out.println("2) Activate printer                           ");
            System.out.print("Choice [1-2]: ");

            switch(getChoice()) {
                case 1:
                    searchPersonMenu();
                    break;
                case 2:
                    activatePrinterMenu();
                    break;
                default:
                    break;
            }
        }
    }

    private void searchPersonMenu() {
        boolean result = false;

        try {
            System.out.print("Enter search query   > ");
            String query = keyboard.nextLine();

            List<Suffrage> suffrages = poll.getSuffrages();

            for(Suffrage suffrage : suffrages) {
                if(suffrage.getPerson().getName().equals(query)) {
                    result = true;
                }
            }

            if(result) {
                System.out.println("This person is eligible to vote.");
                System.out.println("0) *** Return");
                System.out.print("Choice [0]: ");
                pause();
                return;
            } else if(!result) {
                System.out.println("This person is not eligible to vote.");
                System.out.println("0) *** Return");
                System.out.print("Choice [0]: ");
                pause();
                return;
            }
        } catch(Exception e) {

        }
    }

    private void activatePrinterMenu() {
        boolean result = false;

        try {
            System.out.println("Select printer to activate: ");

            for(int i = 0; i < clients.size(); i++) {
                System.out.format("[%d] %d\n", i, clients.get(i).getId());
            }

            int choice = getChoice();
            clients.get(choice).sendToClient("activate");
            return;

        } catch(Exception e) {

        }
    }

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

    private void clearScreen() {
        for(int i = 0; i < 25; i++) { System.out.println(); }
        System.out.println("          OOTUMLIA ELECTION MANAGEMENT        ");
        System.out.println("          POLL ADMINISTRATION TERMINAL        ");
        System.out.println("______________________________________________");
    }

    private void pause() {
        try {
            System.in.read();
        } catch (IOException e) {
        }
    }

    protected void setPoll(Poll poll) {
        this.poll = poll;
    }

    protected void setTally(Tally tally) {
        for (Tally t : poll.getTallies()) {
            if(tally.getCandidate().equals(t.getCandidate())) {
                t.setVotes(tally.getVotes());
                break;
            }
        }
    }

    protected int getPollID() {
        return this.pollID;
    }

    protected Poll getPoll() {
        return this.poll;
    }

    protected void setClients(List<ConnectionToClient> clients) {
        this.clients = clients;
    }

    public static void main(String[] args) {
        new TerminalTUI(args[0], Integer.parseInt(args[1])).mainMenu();
    }
}
