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
	private Vector<ClientInfo> vc; // 연결된 클라이언트 클래스(소켓, 콤포지션해서)을 담는 걸렉션

	public ChatTestServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);

			// 메인 스레드의 역할
			while (true) {
				System.out.println(TAG + "클라이언트 연결 대기중....");
				Socket socket = serverSocket.accept(); // 클라이언트 대기

				System.out.println(TAG + "클라이언트 연결");
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}
		} catch (IOException e) {
			System.out.println(TAG + "서버 연결 오류" + e.getMessage());
		}
	}// End of 생성자

	class ClientInfo extends Thread {

		Socket socket;
		String id;
		BufferedReader reader;
		// BufferedWriter와 다른 점은 내려쓰기 함수 지원
		PrintWriter writer;

		public ClientInfo(Socket socket) {
			this.socket = socket;
		}// end of field

		// 역할 : 클라이언트로 부터 받은 메세지를 모든 클라이언트 한테 재전송
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
				System.out.println(TAG + "연결 끊김" + e.getMessage());
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
						vc.get(i).writer.println("[" + id + "]님께서 입장을 하셨습니다.");
					}

				} else {
					writer.println("[서버] 아이디를 등록해 주십시오.");
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
							vc.get(i).writer.println("[귓속말:" + this.id + "] " + gubun[2]);
						}
					}
				} else {
					writer.println("ALL: 또는 TO:ID: 양식을 이용해 주십시오.");
					return;
				}
			}

		}
	}// End of ClientInfo

	public static void main(String[] args) {
		new ChatTestServer();
	}

}// End of ChatServer
