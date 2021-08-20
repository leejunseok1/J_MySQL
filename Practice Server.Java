import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * 네트워크 상으로 정수를 입력받아서 사칙연산 서비스를 제공하는 서버 프로그램 작성
 */
public class MainServer {
	private static final int PORT = 7856;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static void main(String[] args) {
		 System.out.println("-- 서버 시작 --");
		try(ServerSocket server = new ServerSocket(PORT)){
			try (Socket client = server.accept()){
				dos = new DataOutputStream(client.getOutputStream());
				dis = new DataInputStream(client.getInputStream());
//  =========================================================== 1. 클라이언트에게 보낼 값 ========	
				dos.writeUTF("환영합니다. 네트워크 계산기입니다.");		
				dos.flush();
//  =========================================================== 2. 클라이언트에게 보낼 값 ========	
				dos.writeUTF("정수를 입력하세요");	
				dos.flush();
//	=========================================================== 클라이언트가 입력해서 보낸 값 읽어서 보여주기 ========			
				int read = dis.readInt();				
				System.out.println("사용자가 보낸 정수값 :" + read);
				
//  =========================================================== 3. 클라이언트에게 보낼 값 ========		
				dos.writeUTF("연산자를 입력하세요 + - * /" );	
				dos.flush();
				
//  =========================================================== 클라이언트가 입력해서 보낸 값 읽어서 보여주기 ========	
				String oper = dis.readUTF();
				System.out.println("사용자가 보낸 연산자 : " + oper);
				
			} finally {
				dos.close();
				dis.close();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}
//--------------------------------------------------------------



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class calClient {
	//	Main 서버에서 보낸 정보를 받아서 계산할 수 있는 클래스 
	private static final int PORT = 7856;
	private static final String SERVER_ADDRESS = "localhost";
	
	public static void main(String[] args) {
				System.out.println("--클라이언트 시작--");
		try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				// scanner불로오기
				Scanner scan = new Scanner(System.in)){
			// 정수를 입력하라는 콘솔창이 뜨기 떄문에 입력해조야함
			
			
//	=========================================================== 1. 서버에서 보낸 값 받아서 읽기 =======
			String line = dis.readUTF();
			System.out.println(line);
//	=========================================================== 2. 서버에서 보낸 값 받아서 읽기 =======
			String line2 = dis.readUTF();
			System.out.println(line2);
//	=========================================================== 서버에 정수값 입력한 거 보내기 =======
			int num = scan.nextInt(); //	정수 콘솔창에 입력한 거 받아오기 
			scan.nextLine();	//	 다음 줄을 무시하기 위함 : 공백 무시용
			dos.writeInt(num);
//	============================================================ 3. 서버에서 보낸 값 받아서 읽기 ======
			String line3  = dis.readUTF();
			System.out.println(line3);
//	============================================================ 서버에 연산자 값 입력한 거 보내기 ======			
			String oper = scan.nextLine();
			dos.writeUTF(oper);
			dos.flush();
//  ========================================================================================================		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-- 클라이언트 종료 --");
	}

}
