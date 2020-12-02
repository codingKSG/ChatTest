package chatTest;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Vector;

public class ChatTestServer {
	private ChatTestServer chatserver = this;
	private final static String TAG = "ChatServer : ";

	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����, ���������ؼ�)�� ��� �ɷ���

	public ChatTestServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);

			// ���� �������� ����
			while (true) {
				System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����....");
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���

				System.out.println(TAG + "Ŭ���̾�Ʈ ����");
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}
		} catch (IOException e) {
			System.out.println(TAG + "���� ���� ����" + e.getMessage());
		}
	}// End of ������

	class ClientInfo extends Thread {

		Socket socket;
		String id;
		BufferedReader reader;
		// BufferedWriter�� �ٸ� ���� �������� �Լ� ����
		PrintWriter writer;

		public ClientInfo(Socket socket) {
			this.socket = socket;
		}// end of field

		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޼����� ��� Ŭ���̾�Ʈ ���� ������
		@Override
		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
				String input = null;

				while ((input = reader.readLine()) != null) {
					send(input);
				}
			} catch (Exception e) {
				System.out.println(TAG + "���� ����" + e.getMessage());
			}
			
		}// End of run

		private void save(String input) {
			FileWriter saveFile = null;
			String hour = LocalDateTime.now().getHour() + ":";
			String min = LocalDateTime.now().getMinute() + ":";
			String sec = LocalDateTime.now().getSecond() + "";

			try {
				saveFile = new FileWriter("D:\\Workspace\\javawork\\chatTest\\index.txt", true);

				String text = "[" + hour + min + sec + "] " + input;
				saveFile.write(text, 0, text.length());
				saveFile.write("\r\n", 0, 2);
				
				saveFile.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void send(String input) {
			String[] gubun = input.split(":");

			if (id == null) {
				if (gubun[0].equals(Protocol.ID)) {
					this.id = gubun[1];
					for (int i = 0; i < vc.size(); i++) {
						vc.get(i).writer.println("[" + id + "]�Բ��� ������ �ϼ̽��ϴ�.");
					}

				} else {
					writer.println("[����] ���̵� ����� �ֽʽÿ�.");
					return;
				}
			} else {
				if (gubun[0].equals(Protocol.ALL)) {
					save(gubun[1]);
					for (int i = 0; i < vc.size(); i++) {
						if (vc.get(i).id != this.id) {
							vc.get(i).writer.println("[" + this.id + "] " + gubun[1]);
						}
					}
				} else if (gubun[0].equals(Protocol.TO)) {
					for (int i = 0; i < vc.size(); i++) {
						if (vc.get(i).id.equals(gubun[1])) {
							vc.get(i).writer.println("[�ӼӸ�:" + this.id + "] " + gubun[2]);
						}
					}
				} else {
					writer.println("ALL: �Ǵ� TO:ID: ����� �̿��� �ֽʽÿ�.");
					return;
				}
			}

		}
	}// End of ClientInfo

	public static void main(String[] args) {
		new ChatTestServer();
	}

}// End of ChatServer
