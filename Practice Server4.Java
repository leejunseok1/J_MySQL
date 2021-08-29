import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	private static List<DataOutputStream> list
	= new ArrayList<>();
//	private static Lock lock = new ReentrantLock();
	

	public static void main(String[] args) {
		System.out.println("-- 서버시작");
		try (ServerSocket server = new ServerSocket(4885)) { //서버 자신의포트를 설정해준다
			while (true) {
				System.out.println("-- 사용자 접속 대기"); 
				Socket client = server.accept(); // 클라이언트와 쓰레드가 1:1로
	// 새로운 소켓을 생성 클라이언트가 들어왔을때, 접속했을때 실행하는 구문
				System.out.println("-- 사용자 접속");
				System.out.println("Client ip :" + client.getInetAddress());
				ChatThread chatThread = new ChatThread(client);
				chatThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 사용자 list에 등록
	public static void add(DataOutputStream dos) {
		synchronized (list) {
			list.add(dos);
			
		}
	}
	// 사용자 list에서 제거
	public static void remove(DataOutputStream dis)	{
		synchronized (list) {
			list.remove(dis);
		}
	}
	// 모든 접속한 사용자에게 메세지보내기
	public static void broadcast(String message) throws IOException {
		synchronized (list) {
			for (DataOutputStream dos : list) {
				dos.writeUTF(message);
				dos.flush();
			}
		}
	}
	
}
	class ChatThread extends Thread {
		private Socket client;
		private DataOutputStream dos;
		private DataInputStream dis;

		public ChatThread(Socket client) {
			this.client = client;
			try {
				dos = new DataOutputStream(client.getOutputStream());
				dis = new DataInputStream(client.getInputStream());
				
				Server.add(dos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String line = null;
			try {
				while ((line = dis.readUTF()) != null) {
					dos.writeUTF(line);
					dos.flush();
			
					if (line.equals("/quit")) {
						Server.remove(dos);
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (client != null) {

					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

//클라이언트

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		try(Socket socket = new Socket("127.0.0.1", 4885)) {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			
			Thread t = new Thread(new Runnable() {
				Scanner scan = new Scanner(System.in);
				@Override
				public void run() {
					while(true) {
					String fromKey = scan.nextLine();
					try {
						dos.writeUTF(fromKey);
						dos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					}
				}	
				
			});
			t.setDaemon(true);
			t.start();
			
			String fromServer = null;
			while((fromServer = dis.readUTF()) != null) {
				System.out.println(fromServer);
				
				if (fromServer.equals("/quit")) {
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

