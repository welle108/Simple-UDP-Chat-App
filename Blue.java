/**
*  UDP Client Program
*  Connects to a UDP Server
*  Receives a line of input from the keyboard and sends it to the server
*  Receives a response from the server and displays it.
*
*  @author: Michael Fahy
*  email: fahy@chapman.edu
*  date: 2/4/2018
*  @version: 3.0
*
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Blue {
  public static void main(String[] args) throws Exception {

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress ipAddress = InetAddress.getByName("localhost");
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    int state = 0;

    DatagramPacket sendPacket = null;
    DatagramPacket reveivePacket = null;
    sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
    receivePacket = new DatagramPacket(receiveData, receiveData.length);
    String message = "Hello Blue";
    String response = "";
    boolean connected = false;

    while(state<3) { 
      recieveData = new byte[1024];
      sendData = new byte[1024];
      
      switch(state) {
          
        case 0: //state 0: wait for first connection
          sendData = message.getBytes();
          clientSocket.send(sendPacket);
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

          //transition to states
          if(response.substring(0,3).equals("100")) {
            state = 1;
          }
          else if(response.substring(0,3).equals("200")) {
            state = 2;
          }
          break;
          
        case 1: //state 1: wait for second client to connect
          if(connected = true) {
            System.out.println("FROM SERVER: " + response);
          }

          if(connected = false) {
            System.out.println("Waiting for other user to join");
            clientSocket.receive(receivePacket);
            response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            connected = true;
            System.out.println("Other user has joined!");
          }

          //reply to both clients
          System.out.print("Enter a message: ");
          message = inFromUser.readLine();
          sendData = message.getBytes();

          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
          clientSocket.send(sendPacket);
          System.out.println("Message sent.");

          break;
          
        case 2: //state 2: chat mode
          //receive message from one client
          System.out.println("Waiting for message");
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

          //check for Goodbye message
          if(response.length()>=7 && response.substring(0,7).equals("Goodbye")) {
            state = 3;
          }
          
          //if not a Goodbye message, relay message to the other client
          System.out.println();
          System.out.println("FROM SERVER: " + response);

          //send message to other client
          System.out.print("Enter a message: ");
          message = inFromUser.readLine();
          sendData = message.getBytes();

          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
          clientSocket.send(sendPacket);
          break;
      }
    }
    clientSocket.close();
  }
}
