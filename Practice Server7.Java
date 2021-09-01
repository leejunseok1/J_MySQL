import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField port_tf;
	private JTextArea textArea = new JTextArea();
	JButton start_btn = new JButton("서버 실행");
	JButton stop_btn = new JButton("서버 중지"); 
	
	
	// network 자원
	
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	private Vector user_vc = new Vector();
	
	private StringTokenizer st;
	
//	private InputStream is;
//	private OutputStream os;
//	private DataInputStream dis;
//	private DataOutputStream dos;
	
	
	Server(){ // 생성자
		init(); // 화면 구성 메소드
		start(); // 리스너 설정 메소드
	}
	private void start() {
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
		
	}
	//화면구성
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 490, 622);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(17, 15, 434, 183);
		contentPane.add(scrollPane);
		
		
		scrollPane.setViewportView(textArea);
		
		JLabel label = new JLabel("포트번호");
		label.setBounds(17, 232, 82, 21);
		contentPane.add(label);
		
		port_tf = new JTextField();
		port_tf.setBounds(116, 229, 215, 27);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		
		start_btn.setBounds(17, 289, 186, 29);
		contentPane.add(start_btn);
		
		
		stop_btn.setBounds(263, 289, 169, 29);
		contentPane.add(stop_btn);
		
		this.setVisible(true);// 화면 출력
	}
	
	private void Server_start() {
		try {
			server_socket = new ServerSocket(port);// 12345포트 사용 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (server_socket != null) { //서버 소켓이 정상적으로 열렸을 경우에만 실행
			Connection();
		}
	} // 서버 스타트
	
	private void Connection() {
		
		
		//한가지의 스레드에서는 한가지의 일만 처리할 수 있음.
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() { // 스레드에서 처리할 일을 기재합니다.
			
				while(true) {
				try {
					
					textArea.append("사용자 접속 대기중\n");
					socket = server_socket.accept(); // 사용자 접속대기 - 무한대기 // 대기상태에 들어가있기 때문에 다른 버튼들이 죽어있음 스레드로 문제해결
					textArea.append("사용자 접속\n");
					
					UserInfo user = new UserInfo(socket);
					
					user.start(); // 객체의 스레드 실행 개별 스레드 실행하기 위해 유저 각각의 개별적으로 스레드 돌려줌
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
				} // while문 끝
			}
		});
		
		th.start();
		
	}
	public static void main(String[] args) {
		new Server();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == start_btn) {
			
			System.out.println("서버 스타트 버튼 클릭");
			
			port = Integer.parseInt(port_tf.getText().trim());
			
			Server_start(); // 소켓 생성및 사용자접속 대기
			
		}else if(e.getSource()==stop_btn) {
			System.out.println("서버 스탑 버튼 클릭");
		}
	} // 액샨이벤트 끝

	class UserInfo extends Thread{
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;
		
		private Socket user_socket;
		private String Nickname = "";
	
		
		UserInfo(Socket soc){ // 생성자 메소드
			 this.user_socket = soc;
			 
			 UserNetwork();
		}
		private void UserNetwork() { // 네트워크 자원 설정
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				
				Nickname = dis.readUTF();
				textArea.append(Nickname + ": 사용자 접속! \n");
				
				//기존 사용자들에게 새로운 사용자 알림
				
				System.out.println("현재 접속된 사용자:" + user_vc.size());
				
				BroadCast("NewUser/"+Nickname); // 기존 사용자에게 자신을 알림
				
				//자신에게 기존 사용자를 알림
				
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					
					send_Message("OldUser/" + u.Nickname);
				}
				
				
				user_vc.add(this); // 사용자에게 알린 후 vector에 자신을 추가
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void run() { // Thread 에서 처리할 내용
			while(true) {
				try {
					String msg = dis.readUTF();
					
					textArea.append(Nickname + ": 사용자로부터 들어온 메세지 :" + msg + "\n");
					
					InMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		} // run메소드 끝
		
		private void InMessage(String str) { // 클라이언트로 부터 들어오는 메세지 처리
			
			st = new StringTokenizer(str, "/");
			
			String protocol = st.nextToken(); // 프로토콜과 메세지 분리
			String message = st.nextToken();
			
			System.out.println("프로토콜 :" + protocol);
			System.out.println("메세지 :" + message);
			
			if(protocol.equals("Note")) {
				//protocol = Note
				//message = user2@~~~
				
				st = new StringTokenizer(message,"@");
				
				String user = st.nextToken();
				String note = st.nextToken();
				
				System.out.println("받는 사람 :" + user);
				System.out.println("보낼 내용 : " + note);
				
				//벡터에서 해당 사용자를 찾아서 메세지 전송
				
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					
					if (u.Nickname.equals(user)) {
						u.send_Message("Note/" + Nickname+"@" + note);
						//Note/User1/@~~~~
					}
				}
			}
		}
		
		private void BroadCast(String str) { // 전체 사용자에게 메세지 보내는 부분
			
			for (int i = 0; i < user_vc.size(); i++) { //현재 접속된 사용자에게 새로운 사용자 알림
				UserInfo u = (UserInfo)user_vc.elementAt(i);
				// 벡터에서 오브젝트형태로 꺼냄 유저인포 형태로 형변환
				u.send_Message(str); 
			} 
		}
		
		private void send_Message(String str) { // 문자열을 받아서 전송
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}


//try {
//	is = socket.getInputStream();
//	dis = new DataInputStream(is);
//	
//	os = socket.getOutputStream();
//	dos = new DataOutputStream(os);
//} catch (IOException e1) {
//}
//
//String msg = "";
//
//msg = dis.readUTF(); // 사용자로부터 들어오는 메세지
//textArea.append(msg);
//
//dos.writeUTF("접속확인");


// 클라이언트

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame implements ActionListener{
	//로그인 GUI 변수
	private JFrame Login_GUI = new JFrame();
	private JPanel Login_Pane;
	private JTextField ip_tf; // id 받는 텍스트필드
	private JTextField port_tf; // port 받는 텍스트필드
	private JTextField id_tf; // id 받는 텍스트필드
	private JButton login_btn = new JButton("접속");
	
	//main GUI 변수
	private JPanel contentPane;
	private JTextField message_tf; // 메세지 적는 칸
	private JButton notesend_btn = new JButton("귓속말");
	private JButton joinroom_btn = new JButton("채팅방참여");
	private JButton createroom_btn = new JButton("방 만들기");
	private JButton send_btn = new JButton("전송");
	
	private JList User_list = new JList(); // 전체 접속자 List
	private JList Room_list = new JList(); // 전체 방 목록 List

	private JTextArea Chat_area = new JTextArea(); // 채팅창 변수

	//네트워크를 위한 자원 변수
	
	private Socket socket; 
	private String ip;
	private int port;
	private String id = "";
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	//그외 변수들
	
	Vector user_list = new Vector();
	Vector room_list = new Vector();
	StringTokenizer st;
	
	
	
	Client(){ // 생성자 메소드
		Login_init(); // 로그인창 화면 구성 메소드
		Main_init(); // 메인창 화면 구성 메소드
		start();
	}
	
	private void Main_init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 628, 511);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("전체접속자");
		lblNewLabel.setBounds(17, 15, 98, 21);
		contentPane.add(lblNewLabel);
		
		User_list.setBounds(17, 51, 96, 91);
		contentPane.add(User_list);
		
		
		notesend_btn.setBounds(17, 159, 112, 29);
		contentPane.add(notesend_btn);
		
		JLabel lblNewLabel_1 = new JLabel("채팅방목록");
		lblNewLabel_1.setBounds(17, 203, 112, 21);
		contentPane.add(lblNewLabel_1);
		
		Room_list.setBounds(17, 239, 98, 114);
		contentPane.add(Room_list);
		
		
		joinroom_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		joinroom_btn.setBounds(17, 368, 129, 29);
		contentPane.add(joinroom_btn);
		
		createroom_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		createroom_btn.setBounds(17, 412, 129, 29);
		contentPane.add(createroom_btn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(157, 16, 420, 372);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(Chat_area);
		
		send_btn.setBounds(508, 412, 98, 29);
		contentPane.add(send_btn);
		
		message_tf = new JTextField();
		message_tf.setBounds(163, 413, 315, 27);
		contentPane.add(message_tf);
		message_tf.setColumns(10);
		this.setVisible(true);
	}
	
	private void start() {
		login_btn.addActionListener(this); // 로그인 버튼 리스너
		notesend_btn.addActionListener(this); // 귓속말 버튼 리스너
		joinroom_btn.addActionListener(this); // 채팅방 참여 버튼 리스너
		createroom_btn.addActionListener(this); // 채팅방 만들기 버튼 리스너
		send_btn.addActionListener(this); // 채팅 전송 버튼 리스너
	}
	
	private void Login_init() {
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // this 생략
		Login_GUI.setBounds(100, 100, 404, 533);
		Login_Pane = new JPanel();
		Login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_Pane);
		Login_Pane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(17, 224, 82, 21);
		Login_Pane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(17, 288, 92, 21);
		Login_Pane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("ID");
		lblNewLabel_2.setBounds(17, 368, 82, 21);
		Login_Pane.add(lblNewLabel_2);
		
		ip_tf = new JTextField();
		ip_tf.setBounds(145, 221, 166, 27);
		Login_Pane.add(ip_tf);
		ip_tf.setColumns(10);
		
		port_tf = new JTextField();
		port_tf.setBounds(145, 285, 166, 27);
		Login_Pane.add(port_tf);
		port_tf.setColumns(10);
		
		id_tf = new JTextField();
		id_tf.setBounds(145, 365, 166, 27);
		Login_Pane.add(id_tf);
		id_tf.setColumns(10);
		
		
		login_btn.setBounds(108, 433, 129, 29);
		Login_Pane.add(login_btn);
		
		Login_GUI.setVisible(true);
	}

	private void Network() {
		try {
			socket = new Socket(ip,port);
			
			if (socket != null) { // 정상적으로 소켈이 연결 되었을 경우
				Connection();
			}
			
		} catch (UnknownHostException e) { // 해당 호스트를 찾을수 없는
			e.printStackTrace();
		} catch (IOException e) { // 스트림에서 에러가 발생할때 
			e.printStackTrace();
		}
	}
	
	private void Connection() {
		try {
			
			is = socket.getInputStream();
			dis  = new DataInputStream(is);
			
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
		} catch (IOException e) {
			
		} // Stream 설정 끝
		
		// 처음 접속시에 id전송
		send_message(id);
		
		//User_list에 사용자 추가
		user_list.add(id);
		User_list.setListData(user_list);
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
			
				while(true) {
					try {
						String msg = dis.readUTF(); // 메세지 수신
						
						System.out.println("서버로부터 수신된 메세지 :" + msg);
						
						inmessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				
			}
		});
		th.start();
		
	}
	
	private void inmessage(String str) { // 서버로부터 들어오는 모든 메세지
		
		st = new StringTokenizer(str,"/");
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("프로토콜 : " + protocol);
		System.out.println("내용 : " + Message);
		
		if (protocol.equals("NewUser"))  { // 새로운 접속자  갱신 부분
			user_list.add(Message);
			User_list.setListData(user_list);
			
		}else if(protocol.equals("OldUser")) {
			user_list.add(Message);
			User_list.setListData(user_list);
		}else if(protocol.equals("Note")){
			st = new StringTokenizer(Message,"@");
			String user = st.nextToken();
			String note = st.nextToken();
			
			System.out.println(user + "사용자로부터 온 귓속말" + note);
			
			JOptionPane.showMessageDialog(null, note, user + "님에게 온 귓속말", JOptionPane.CLOSED_OPTION);
												
		}
	}
	
	private void send_message(String str) { // 서버에게 메세지를 보내는 부분
		try {
			dos.writeUTF(str);
		} catch (IOException e) { // 에러 처리 부분
			
		}
	}
	
	public static void main(String[] args) {
		new Client();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// login_btn 로그인 버튼
		if(e.getSource() == login_btn) {
			System.out.println("로그인 버튼 클릭");
			
			ip = ip_tf.getText().trim(); // ip를 받아오는 부분
			
			port = Integer.parseInt(port_tf.getText().trim()); 
			
			id = id_tf.getText().trim(); // id 받아오기
					
			Network();
		} else if(e.getSource() == notesend_btn) {
			System.out.println("귓속말 버튼 클릭");
			
			String user = (String)User_list.getSelectedValue();
			
			String note = JOptionPane.showInputDialog("보낼메세지");
			if (note != null) { // 사용자가 메세지를 입력했을때
				send_message("Note/"+user+"@"+note);
				
				//ex = Note/User@나는 User1이야
			}
			System.out.println("받는사람 : "+ user + "| 보낼내용 :" + note);
			
			
			
		} else if(e.getSource() == joinroom_btn) {
			System.out.println("방 참여 버튼 클릭");
		} else if(e.getSource() == createroom_btn) {
			System.out.println("방 만들기 버튼 클릭");
		} else if(e.getSource() == send_btn) {
			
			send_message("임시테스트입니다");
			
			System.out.println("채팅 전송 버튼 클릭");
		}
		
	}

}
