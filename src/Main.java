import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;



public class Main implements ActionListener{
	private JFrame frame;
	private JPanel mainPanel;
	private JPanel inputPanel;
	private JTextArea output;
	private JTextField textInput;
	private JButton confirmInput;
	
	Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
	
	String address = "";
	int port = -1;
	
	String consoleText = "";
	
	public static void main(String [] args){
		new Main();
	}
	
	public Main(){
		setupGui();
		output("Enter host name:");
	}
	
	public void setupGui(){
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setVisible(true);
		
		mainPanel = new JPanel(new BorderLayout());
		inputPanel = new JPanel(new BorderLayout());
		
		output = new JTextArea();
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		output.setEditable(false);
		
		textInput = new JTextField();
		textInput.addActionListener(this);
		
		confirmInput = new JButton();
		confirmInput.setText("send");
		confirmInput.addActionListener(this);
		
		mainPanel.add(output, BorderLayout.CENTER);
		inputPanel.add(textInput, BorderLayout.CENTER);
		inputPanel.add(confirmInput, BorderLayout.EAST);
		mainPanel.add(inputPanel, BorderLayout.SOUTH);
		
		frame.add(mainPanel);
	}
	
	public void actionPerformed(ActionEvent e) {
		send(textInput.getText());
		textInput.setText("");
	}
	
	void setupNetwork(String host, int port) throws IOException{
		socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        Listener listener = new Listener();
        Thread p = new Thread(listener);
        p.start();
	}
	
	void output(String msg){
		consoleText += msg + "\n";
		output.setText(consoleText);
	}
	
	void send(String msg){
		if(address == ""){
			address = msg;
			output("enter host port #:");
			return;
		}else if(port == -1){
			port = Integer.parseInt(msg);
			output("Setting up socket...");
			try {
				setupNetwork(address, port);
			} catch (IOException e) {}
			return;
		}
		
		// send request
		out.println(msg);
		output(">" + msg);
	}
	
	class Listener implements Runnable{

		public void run() {
			
			while(true){
				try {
					output("<" + in.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}