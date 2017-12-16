package rmReplica1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ManagerHelperOne {


    public String forwardToReplica(String input, int portNumber) throws Exception{

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String sentence = input;
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        modifiedSentence = modifiedSentence.trim();
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();

        return modifiedSentence;
    }



}
