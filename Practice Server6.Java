import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
	private JTextField textField;
	
	
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
		
		JList list = new JList();
		list.setBounds(17, 51, 96, 91);
		contentPane.add(list);
		
		JButton button = new JButton("귓속말");
		button.setBounds(17, 159, 112, 29);
		contentPane.add(button);
		
		JLabel lblNewLabel_1 = new JLabel("채팅방목록");
		lblNewLabel_1.setBounds(17, 203, 112, 21);
		contentPane.add(lblNewLabel_1);
		
		JList list_1 = new JList();
		list_1.setBounds(17, 239, 98, 114);
		contentPane.add(list_1);
		
		JButton btnNewButton = new JButton("채팅방참여");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(17, 368, 129, 29);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("방 만들기");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setBounds(17, 412, 129, 29);
		contentPane.add(btnNewButton_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(157, 16, 420, 372);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnNewButton_2 = new JButton("전송");
		btnNewButton_2.setBounds(508, 412, 98, 29);
		contentPane.add(btnNewButton_2);
		
		textField = new JTextField();
		textField.setBounds(163, 413, 315, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		this.setVisible(true);
	}
	
	private void start() {
		login_btn.addActionListener(this);
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

	public static void main(String[] args) {
		new Client();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// login_btn 로그인 버튼
		if(e.getSource() == login_btn) {
			System.out.println("로그인 버튼 클릭");
		}
		
	}

}

// Server

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	Server(){ // 생성자
		init(); // 화면 구성 메소드
		start(); // 리스너 설정 메소드
	}
	private void start() {
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
		
	}
	
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
	
	public static void main(String[] args) {
		new Server();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == start_btn) {
			System.out.println("서버 스타트 버튼 클릭");
		}else if(e.getSource()==stop_btn) {
			System.out.println("서버 스탑 버튼 클릭");
		}
	}

}

