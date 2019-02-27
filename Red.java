/**
*  UDP Client Program
*  Connects to a UDP Server
*  Receives a line of input from the keyboard and sends it to the server
*  Receives a message from the server and displays it.
*
*  @author: Michael Fahy
*  email: fahy@chapman.edu
*  date: 2/4/2018
*  @version: 3.0
*
*  Modified by: Joshua Anderson
*  Partners: Matt Greenberg, Samir Kamnani
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

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    String message = "Hello Red";
    String response = "";
    boolean connected = false;

    while(state < 3) {
      switch(state) {
        case 0: //state 0
          sendData = message.getBytes();

          System.out.println();
          //System.out.println("FROM SERVER: " + response);
          //System.out.println("State: " + state);

          clientSocket.send(sendPacket);
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

          //change state based on response message
          if(response.substring(0,3).equals("100")) state = 1;
          else if(response.substring(0,3).equals("200")) state = 2;
          break;
        case 1: //state 1
          System.out.println();
          if(connected) System.out.println("FROM SERVER: " + response);
          //System.out.println("State: " + state);

          //wait for other client to join
          if(!connected) {
            System.out.println("Waiting for other user to join...");
            clientSocket.receive(receivePacket);
            response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            connected = true;
            System.out.println("Other user has joined!");
            System.out.println();
          }

          //send user message
          System.out.print("Enter a message: ");
          message = inFromUser.readLine();
          sendData = message.getBytes();

          sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
          clientSocket.send(sendPacket);
          System.out.println("Message sent.");

          //wait for other client response message
          System.out.println("Waiting for message...");
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

          //check if response is Goodbye
          if(response.length() >= 7 && response.substring(0,7).equals("Goodbye")) state = 3;

          break;
        case 2: //state 2
          //wait for message from other client
          System.out.println("Waiting for message...");
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

          //check if response is Goodbye
          if(response.length() >= 7 && response.substring(0,7).equals("Goodbye")) state = 3;

          System.out.println();
          System.out.println("FROM SERVER: " + response);
          //System.out.println("State: " + state);

          //send user message
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
