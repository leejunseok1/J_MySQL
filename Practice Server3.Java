import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
	private static final int PORT = 7777; // port 번호로 클라이언트가 접속할 수 있도록 문을 열어 둬야 됨
//	well-kown port = 0 ~ 1023
//	registerd port = 1024 ~ 49151
//	dynamic port = 49151 ~ 51915

//	"/곱 10, 5" 문자열 자르기 : 제대로 조건에 맞게 입력을 해야 함 
	private static int regexMultipleNumber(String input) {
								//	\\s는 공백 하나 이상 
								//	\\s?는 공백이 있어도 되고 없어도 되는 것 
								//	\\d+ 숫자한개 이상을 의미 
		Pattern p = Pattern.compile("/곱 (\\d+),\\s?(\\d+)");
		Matcher m = p.matcher(input); 
		
		if( !m.find() ) {
			System.out.println("잘못된 입력이니 새로 조건에 맞게 입력해주세요.");
		}
		int first = Integer.valueOf(m.group(1));
		int second = Integer.valueOf(m.group(2));
		
		return first * second;
	}
	private static int multiply(String input) {
		String mul = input.substring(input.lastIndexOf(" ") + 1, 
							input.length()
							);
		
		return 0;
	}
	
	
	private static int subNum(String input) {
//		문자열을 끝에서 부터 공백이 있는 부분을 알아내서 subString으로 잘라내기
		// "/합 10" -> 10 int
		// 시작하는 인덱스 : 공백을 뒤에서 부터 찾아옴 -> 인덱스 1번부터 시작 / 끝나는 인덱스 : 문자열의 길이값
		String sub = input.substring(input.lastIndexOf(" ") + 1,
				input.length());
		return Integer.valueOf(sub);
	}

	private static int regexNum(String input) {
		Pattern p = Pattern.compile("/합 (\\d+)");
		Matcher m = p.matcher(input);
		
		m.find();
		return Integer.valueOf(m.group(1));
	}
	
	public static void main(String[] args) {
		System.out.println("--서버 시작--");
		try (ServerSocket server = new ServerSocket(PORT);) {// this port를 사용하는 클라이언트의 접속을 받을 수 있는
			try (Socket client = server.accept();
					DataOutputStream out = new DataOutputStream(client.getOutputStream());
					DataInputStream in = new DataInputStream(client.getInputStream());) {// ServerSocket을 생성
				// server랑 communication을 하기 위해서 사용하는 방법
				// 클라이언트가 접속할 때까지 기다림
				// 클라이언트의 입력 언어를 그대로 받아야 됨
				// DataInputSream - DataOutputStream

				System.out.println("--클라이언트가 접속함--");
				// 닫히는 시기가 다르기 때문에 따로 try - catch 사용

				// 클라이언트가 UTF 문자로 output으로 정보를 주면,
				// 서버는 in으로 받아들임
				// TCP의 특징 : 메세지를 보내면 순서대로 받을 수 있다.
//				for( int i = 0; i < 5; i++) {
//				String msg = in.readUTF();
//				System.out.println("Message : " +  msg);
//				}

				while (true) {
					String fromClient = in.readUTF();

					if (fromClient.startsWith("/합 10")) {
						int to = subNum(fromClient);
//						int to = regexNum(fromClient);
						int sum = 0;
						for (int i = 0; i <= to; i++) {
							sum += i;
						}
						out.writeUTF(String.valueOf(sum));
						out.flush();
					} else if(fromClient.startsWith("/곱")) {
						int result = regexMultipleNumber(fromClient);
						out.writeUTF(String.valueOf(result));
						out.flush();
					}
					out.writeUTF(fromClient);
					out.flush();
					
	// 한쪽에서 일방적으로 서버를 끊어버리면, 에러가 남
			// 같이 종료 되는 문장을 적어두기
					if (fromClient.equals("/exit")) {
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("--서버 종료--");
	}

}
// 클라이언트

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client { // 서비스를 받는 입장 : 서버로의 연결을 만들어야 됨 / Server가 어디있는 지 알아야 됨
	private static final int PORT = 7777;
	private static final String SERVER_ADDRESS = "localhost"; // = 127.0.01

	public static void main(String[] args) {
		System.out.println("--클라이언트 시작--");
		try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // socket에서출력을 하면, 서버에서 input으로
																						// 받음
				DataInputStream in = new DataInputStream(socket.getInputStream());) { // 입력을 하면, 서버에서는 output으로 받음
			// 서버 주소와 port가 있어야 함, 접속을 하기 위한 준비

			System.out.println("--서버에 접속함--");
			// 클라이언트가 적은 글을 서버에 던지기
			Scanner s = new Scanner(System.in);
			while (true) {
				String line = s.nextLine();
				out.writeUTF(line);
				out.flush();

				// /합 10;

				String fromServer = in.readUTF();
				System.out.println("from Server : " + fromServer);

//				 if(line.equals("/exit")) {
				if (fromServer.equals("/exit")) {
					break;
				}
			}
			// DataOutputStream으로 문자열 던져주기
//			 for(int i = 0; i < 5; i++) {
//				 out.writeUTF("LOL " + i);
//			 }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("--클라이언트 종료--");
	}

}
//메인
public class Main {
	public static void main(String[] args) {
		// Maven을 이용해서 library를 가져와 mysql과 로드하기 = dependencies
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("드라이버 로드 성공");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
