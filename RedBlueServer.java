/**
* UDP Server Program
* Listens on a UDP port
* Receives a line of input from a UDP client
* Returns an upper case version of the line to the client
*
*  @author: Michael Fahy
*  email: fahy@chapman.edu
*  date: 2/4/2018
*  @version: 3.0
*
* PROJECT MEMBERS: Matt Greenberg, Samir Kamnani, Josh Anderson
* Email: green327@mail.chapman.edu
* Date: 9/26/2018
*/


import java.io.*;
import java.net.*;

class ChatServer {
  public static void main(String args[]) throws Exception {

    DatagramSocket serverSocket = null;
    int port = 0;
    int port1 = 0; //Port number of the first client to connect
    int port2 = 0; //Port number of the second client to connect
    String name1  = ""; //Name of first client
    String name2 = ""; //Name of second client
    InetAddress ipAddress = null;
    InetAddress ipAddress1 = null; //IP Address of first client
    InetAddress ipAddress2 = null; //IP Address of second client
    String message = "";
    String response = "";
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    int state = 0;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    byte[] messageBytes = new byte[1024];

    try
    {
      serverSocket = new DatagramSocket(9876);
    }

    catch(Exception e)
    {
      System.out.println("Failed to open UDP socket");
      System.exit(0);
    }

    while (state < 3)
    {
      receiveData = new byte[1024];
      sendData  = new byte[1024];

      switch (state)
      {
        case 0:
          //Message from first Client
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          String receivedString = new String(receivePacket.getData());

          //Stores First Client's Data
          name1 = receivedString.substring(6);
          ipAddress1 = receivePacket.getAddress();
          port1 = receivePacket.getPort();

          //When first connection is made
          sendData = "100".getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
          serverSocket.send(sendPacket);

          state = 1;
          break;

        case 1:
          //Waits for Message from second Client
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          receivedString = new String(receivePacket.getData());

          //Stores Second Client's Data
          name2 = receivedString.substring(6);
          ipAddress2 = receivePacket.getAddress();
          port2 = receivePacket.getPort();

          // Send 200 to both clients confirming that a connection has been made
          sendData = "200".getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
          serverSocket.send(sendPacket);
          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress2, port2);
          serverSocket.send(sendPacket);

          state = 2;
          break;

        case 2:
          //Chat Loop
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          response = new String(receivePacket.getData());
          //If the Client disconnects by saying Goodbye
          if (response.length() >= 7 && response.substring(0, 7).equals("Goodbye"))
          {
            state = 3;
            break;
          }

          //Stores received ip address, port, and message
          ipAddress = receivePacket.getAddress();
          port = receivePacket.getPort();
          receivedString = new String(receivePacket.getData());
          //Figures out whose message is being sent vs whose is being received
          if ((port == port1) && (ipAddress.equals(ipAddress1)))
          {
            ipAddress = ipAddress2;
            port = port2;
          } else {
            ipAddress = ipAddress1;
            port = port1;
          }

          sendData = receivedString.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
          serverSocket.send(sendPacket);

          break;
      }
    }

    //Send goodbye to both clients and then exits the program
    sendData = "Goodbye".getBytes();

    sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress1, port1);
    serverSocket.send(sendPacket);

    sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress2, port2);
    serverSocket.send(sendPacket);

    serverSocket.close();
  }
}
