package chatTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public class ChatTestClient extends JFrame {

	private ChatTestClient chatClient = this;
	private final static String TAG = "ChatClient : ";

	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;
	
	private JPanel topPanel, bottomPanel, mainPanel;
	private JLabel noticeLabel;
	
	private static final int PORT = 10000;
	
	private Socket socket;
	
	private PrintWriter writer;
	private BufferedReader reader;

	public ChatTestClient() {
		init();
		setting();
		batch();
		listener();

		setVisible(true);
	}

	private void init() {
		btnConnect = new JButton("Connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30); //row(��), column(��)
		scrollPane = new ScrollPane();
		
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		mainPanel = new JPanel();
		
		noticeLabel = new JLabel("���̵𸸵�� > F1, ��ο��� > F2, �ӼӸ� > F3");
	}

	private void setting() {
		setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350, 500);
		setLocationRelativeTo(null);
		
		noticeLabel.setBackground(Color.LIGHT_GRAY);
		taChatList.setBackground(Color.ORANGE);
		taChatList.setForeground(Color.BLUE);
		topPanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		mainPanel.setLayout(new BorderLayout());
	}

	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		
		scrollPane.add(taChatList,BorderLayout.CENTER);
		
		mainPanel.add(noticeLabel,BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		add(topPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void listener() {
		
		tfChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send();
				}
				if(e.getKeyCode() == KeyEvent.VK_F1) {
					tfChat.setText("ID:");
				}
				if(e.getKeyCode() == KeyEvent.VK_F2) {
					tfChat.setText("ALL:");
				}
				if(e.getKeyCode() == KeyEvent.VK_F3) {
					tfChat.setText("TO:ID:");
				}
			}
		});
		
		
		// �������Ͽ� ����
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}//End of actionPerformed
		});
		
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
	}
	
	private void connect() {
		String host = tfHost.getText();
		try {
			socket = new Socket(host,PORT);
			
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			
			ReaderThread rt = new ReaderThread();
			rt.start();
			
		} catch (Exception e1) {
			System.out.println(TAG+ "���� ���� ����" + e1.getMessage());
		}
	}
	
	private void send() {
		String chat = tfChat.getText();
		
		String[] gubun = chat.split(":");
		
		// 1�� taChatList�� �Ѹ���
		if(gubun[0].equals(Protocol.ALL)) {
			taChatList.append("[�� �޼���] " + gubun[1] + "\n");
		}else if(gubun[0].equals(Protocol.TO)) {
			taChatList.append("["+ gubun[1 ]+">>] " + gubun[2] + "\n");
		}
		
		// 2�� ������ ����
		writer.println(chat);
		// 3�� tfChat ����
		tfChat.setText("");
	}

	class ReaderThread extends Thread{
		
		//while �� ���鼭 ������ ���� �޼����� �޾� taChatList�� �Ѹ���
		@Override
		public void run() {
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String input = null;
				
				while((input = reader.readLine()) != null) {
					taChatList.append(input + "\n");
				}
			} catch (IOException e) {
				System.out.println(TAG + "ReaderThread ����" + e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatTestClient();
	}
}