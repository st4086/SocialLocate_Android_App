package com.example.tcpserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerBoard extends JFrame {
	private JTextArea messagesArea;
	private JButton sendButton;
	private JTextField message;
	private JButton startServer;
	private TCPServer mServer;
	private TCPServer mServer2;
	public static final int SERVERPORT = 4444;
	private ServerSocket Serversocket;
	ArrayList<String> LIST;
	StringBuilder Alpha;

	public ServerBoard() {

		super("ServerBoard");

		JPanel panelFields = new JPanel();
		panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.X_AXIS));

		JPanel panelFields2 = new JPanel();
		panelFields2.setLayout(new BoxLayout(panelFields2, BoxLayout.X_AXIS));

		// here we will have the text messages screen
		messagesArea = new JTextArea();
		messagesArea.setColumns(30);
		messagesArea.setRows(10);
		messagesArea.setEditable(false);

		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get the message from the text view
				String messageText = message.getText();
				// add message to the message area
				messagesArea.append("\n" + messageText);
				// send the message to the client
				mServer.sendMessage(messageText);
				// clear text
				message.setText("");

				// boolean Check = Checkfile(messageText, false);
			}
		});

		startServer = new JButton("Start");
		startServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// disable the start button
				startServer.setEnabled(false);

				try {
					Serversocket = new ServerSocket(SERVERPORT);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// creates the object OnMessageReceived asked by the TCPServer
				// constructor
				mServer = new TCPServer(new TCPServer.OnMessageReceived() {
					@Override
					// this method declared in the interface from TCPServer
					// class is implemented here
					// this method is actually a callback method, because it
					// will run every time when it will be called from
					// TCPServer class (at while)
					public void messageReceived(String message) {
						messagesArea.append("\n " + message);
						String[] PopOrNot = message.split("%");
						boolean Check = Boolean.valueOf(PopOrNot[1]);
						Checkfile(message, Boolean.valueOf(PopOrNot[1]));
						System.out.println("Client1: " + Check);
						RefreshMap();
						if (Check == false) {
							mServer.sendMessage(Alpha.toString());
						}
						// for(int i=0;i<LIST.size();i++){
						// mServer.sendMessage(LIST.get(i));
						// }
					}
				}, Serversocket);
				mServer.start();

				mServer2 = new TCPServer(new TCPServer.OnMessageReceived() {
					@Override
					// this method declared in the interface from TCPServer
					// class is implemented here
					// this method is actually a callback method, because it
					// will run every time when it will be called from
					// TCPServer class (at while)
					public void messageReceived(String message) {
						messagesArea.append("\n " + message);
						String[] PopOrNot = message.split("%");
						boolean Check = Boolean.valueOf(PopOrNot[1]);
						Checkfile(message, Boolean.valueOf(PopOrNot[1]));
						System.out.println("Client2: " + Check);
						RefreshMap();
						if (Check == false) {
							mServer2.sendMessage(Alpha.toString());
						}
						// for(int i=0;i<LIST.size();i++){
						// mServer.sendMessage(LIST.get(i));
						// }
					}
				}, Serversocket);
				mServer2.start();
			}
		});

		// the box where the user enters the text (EditText is called in
		// Android)
		message = new JTextField();
		message.setSize(200, 20);

		// add the buttons and the text fields to the panel
		panelFields.add(messagesArea);
		panelFields.add(startServer);

		panelFields2.add(message);
		panelFields2.add(sendButton);

		getContentPane().add(panelFields);
		getContentPane().add(panelFields2);

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		setSize(300, 170);
		setVisible(true);

		Checkfile("ABC", false);
		RefreshMap();
	}

	public boolean Checkfile(String New, boolean UserOrPop) {
		LIST = new ArrayList<String>();
		try {
			FileReader fileIn = new FileReader("UserData.txt");
			BufferedReader reader = new BufferedReader(fileIn);
			String line = null;
			while ((line = reader.readLine()) != null) {
				LIST.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("Size: " + LIST.size());
		// for (int i = 0; i < LIST.size(); i++) {
		// System.out.println("CHECK:" + LIST.get(i));
		// }
		String[] New_Info = New.split(";");

		boolean Check = false;
		// System.out.println(LIST.get(0));
		if (UserOrPop == true) {
			for (int i = 0; i < LIST.size(); i++) {
				String User = LIST.get(i);
				String[] User_Info = User.split(";");
				// System.out.println(User_Info[0]);
				if (User_Info[0].compareTo(New_Info[0]) == 0) {
					LIST.set(i, New);
					System.out.println("Replace");
				}
			}
		} else {
			for (int i = 0; i < LIST.size(); i++) {
				String User = LIST.get(i);
				String[] User_Info = User.split(";");
				// System.out.println(User_Info[0]);
				if (User_Info[0].compareTo(New_Info[0]) == 0) {
					Check = true;
				}
			}
			if (Check == false) {
				LIST.add(New);
			}
		}

		System.out.println(LIST.size());
		for (int i = 0; i < LIST.size(); i++) {
			System.out.println(LIST.get(i));
		}

		try {
			if (Check == false) {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter("UserData.txt", false)));
				for (int i = 0; i < LIST.size(); i++) {
					out.write(LIST.get(i));
					out.write("\n");
				}
				// out.println(New);
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// RefreshMap();
		return Check;

	}

	public void RefreshMap() {
		Alpha = new StringBuilder();
		Alpha.append(String.valueOf(LIST.size()));
		Alpha.append(";");
		for (int i = 0; i < LIST.size(); i++) {
			String[] User_Info = LIST.get(i).split(";");
			Alpha.append(User_Info[0]);
			Alpha.append(",");
			Alpha.append(String.valueOf(i));
			Alpha.append(",");
			Alpha.append(String.valueOf(Math.random()));
			Alpha.append(",");
			Alpha.append(String.valueOf(Math.random()));
			Alpha.append(";");
		}
		for (int i = 0; i < LIST.size() - 1; i++) {
			for (int j = i + 1; j < LIST.size(); j++) {
				String[] A = LIST.get(i).split(";");
				String[] B = LIST.get(j).split(";");
				int Dis = 0;
				int Weight = 0;
				// hard code it
				for (int k = 1; k < 8; k++) {
					// System.out.println(k + ": " + A[k]);
					if (k <= 4) {
						Dis += Math.abs(A[k].compareTo(B[k]));
					} else {
						if (Integer.parseInt(A[k]) > 0
								|| Integer.parseInt(B[k]) > 0)
							Weight += Math.abs(Integer.parseInt(A[k])
									- Integer.parseInt(B[k]));
						else
							Weight += 5;
					}
				}
				if (Dis < 5) {
					Alpha.append(String.valueOf(i));
					Alpha.append(",");
					Alpha.append(String.valueOf(j));
					Alpha.append(",");
					Alpha.append(16 - Weight);
					Alpha.append(";");
				}
			}
		}
		System.out.println(Alpha.toString());

		// return Alpha.toString();
	}
}