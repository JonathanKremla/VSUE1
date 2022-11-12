package dslab.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.logging.Logger;

import dslab.ComponentFactory;
import dslab.transfer.dmtp.DmtpListenerThread;
import dslab.shell.IShell;
import dslab.util.Config;

public class TransferServer implements ITransferServer, Runnable {

    private InputStream in;
    private PrintStream out;
    private final int tcpDmtpPort;
    private ServerSocket dmtpSocket;
    private DmtpListenerThread dmtpListenerThread;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * Creates a new server instance.
     *
     * @param componentId the id of the component that corresponds to the Config resource
     * @param config the component config
     * @param in the input stream to read console input from
     * @param out the output stream to write console output to
     */
    public TransferServer(String componentId, Config config, InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        tcpDmtpPort = config.getInt("tcp.port");
    }

    @Override
    public void run() {
        createDmtpListenerThread();
        System.out.println("Server is up!");

        try {
            IShell shell = ComponentFactory.createMailboxShell("shell-transfer", in, out);
            shell.run();
        }
        catch (Exception e){
            e.printStackTrace();
            shutdown();
        }
        shutdown();
    }

    @Override
    public void shutdown() {
        try {
            dmtpSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        dmtpListenerThread.stopThread();
    }

    public void createDmtpListenerThread(){
        try {
            dmtpSocket = new ServerSocket(tcpDmtpPort);
            dmtpListenerThread = new DmtpListenerThread(dmtpSocket);
            dmtpListenerThread.start();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        ITransferServer server = ComponentFactory.createTransferServer(args[0], System.in, System.out);
        server.run();
        System.out.println("Done");
    }

}
