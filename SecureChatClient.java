import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/** Primitive chat client.
 * This client connects to a server so that messages can be typed and forwarded
 * to all other clients.  Try it out in conjunction with ImprovedChatServer.java.
 * You will need to modify / update this program to incorporate the secure
 * elements as specified in the Assignment description.  Note that the PORT used
 * below is not the one required in the assignment -- for your SecureChatClient
 * be sure to change the port that so that it matches the port specified for the
 * secure  server.
 * Adapted from Dr. John Ramirez's CS 1501 Assignment 4
 */
public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    BigInteger E;
    BigInteger N;
    String cipher;
    SymCipher sc;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;

    public SecureChatClient ()
    {
        try {

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new

        myWriter =
             new ObjectOutputStream(connection.getOutputStream());             // Socket
        myReader =
             new ObjectInputStream(connection.getInputStream());   // Get Reader and Writer
        E=(BigInteger) myReader.readObject();
        N=(BigInteger) myReader.readObject();
        System.out.println("E: "+E.toString());
        System.out.println("N: "+E.toString());
        
        cipher=(String) myReader.readObject();
        if(cipher.equals("Sub")) {
        	sc=new Substitute();
        	System.out.println("Substitute cipher selected.");
        	System.out.print("Key: ");
        	byte[] key=sc.getKey();
        	for(byte b: key)
        		System.out.print(String.format("%02x", b)+" ");
        	System.out.println();
        }
        else if(cipher.equals("Add")) {
        	sc=new Add128();
        	System.out.println("Add128 cipher selected.");
        	System.out.print("Key: ");
        	byte[] key=sc.getKey();
        	for(byte b: key)
        		System.out.print(String.format("%02x", b)+" ");
        	System.out.println();
        }
        else
        	throw new Exception("Invalid cipher!");

        
        myWriter.writeObject(new BigInteger(1,sc.getKey()).modPow(E, N));
        myWriter.flush();

        myWriter.writeObject(sc.encode(myName));;   // Send name to Server.  Server will need
        myWriter.flush();                         	// this to announce sign-on and sign-off
        											// of clients

        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

	      addWindowListener(
              new WindowAdapter()
              {
                  public void windowClosing(WindowEvent e)
                  { 
                	 try {
                		 myWriter.writeObject(sc.encode("CLIENT CLOSING"));
                		 myWriter.flush();
                	 }
                	 catch(Exception e2) {
                		 System.err.println("Error closing client!");
                		 e2.printStackTrace();
                	 }
                	 System.exit(0);
                   }
              }
          );

        setSize(500, 200);
        setVisible(true);

        }
        catch (Exception e)
        {
        	e.printStackTrace();
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try {
             	byte[] data=(byte[]) myReader.readObject();
            	String currMsg = sc.decode(data);
            	outputArea.append(currMsg+"\n");
             	System.out.print("Data recieved: ");
             	for(byte b: data)
             		System.out.print(String.format("%02x", b)+" ");
             	System.out.println();
             	System.out.print("Decoded data: ");
             	byte[] decode=currMsg.getBytes();
             	for(byte b: decode)
             		System.out.print(String.format("%02x", b)+" ");
             	System.out.println();
             	System.out.println("Message Recieved: "+currMsg);
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = (myName+": "+e.getActionCommand());      // Get input value
        inputField.setText("");
        System.out.println("Current message: "+currMsg);
     	System.out.print("Byte data: ");
     	byte[] bdat=currMsg.getBytes();
     	for(byte b: bdat)
     		System.out.print(String.format("%02x", b)+" ");
     	System.out.println();
        byte[] data=sc.encode(currMsg);
     	System.out.print("Encoded data: ");
     	for(byte b: data)
     		System.out.print(String.format("%02x", b)+" ");
     	System.out.println();
        try {
			myWriter.writeObject(data);
	        myWriter.flush();
		} catch (IOException e1) {
			outputArea.append("Error sending message!\n");
			e1.printStackTrace();
		}
    }                                               // to Server

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
