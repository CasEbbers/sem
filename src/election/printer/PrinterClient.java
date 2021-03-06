package election.printer;

import election.model.Poll;
import ocsf.client.AbstractClient;

/**
 * PrinterClient class allows the Printer to connect
 * to the Terminal in the Polling Station.
 */
public class PrinterClient extends AbstractClient {

    /** PrinterTUI object to maintain */
    PrinterTUI tui;

    /**
     * Initializes a PrinterClient object.
     * @param host the host to connect to.
     * @param port the port to connect on.
     * @param tui PrinterTUI object to maintain.
     */
    public PrinterClient(String host, int port, PrinterTUI tui) {
        super(host, port);
        this.tui = tui;

    }

    /**
     * Handles messages sent by the server.
     * E.g.     - activate the printer client
     *          - load Poll data
     * @param msg   the message sent.
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        if(msg instanceof String) {
            String message = (String)msg;
            if(message.equals("activate")) {
                tui.activate();
            }
        }

        else if(msg instanceof Poll) {
            tui.setPoll((Poll)msg);
        }
    }
}
