import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class Main extends JFrame {
	public Main() {
		
		JPanel btnpnl = new JPanel();
		JButton signIn = new JButton("로그인");
		JButton signUp = new JButton("회원가입");
		
		
		signIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SignIn();
			}
		});
		
		signUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					new SignUp();
			}
		});
		
		btnpnl.add(signIn);
		btnpnl.add(signUp);
		
		add(btnpnl, "South");
		
		showGUI();
		
		
		
	}
	private void showGUI() {
		setSize(500,500);
		setLocation(650, 350);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	public static void main(String[] args) {
		new Main();
	}

}
//----main

public class Stranger {

	private String id;
	private String pw;
	public Stranger() {}
	public Stranger(String id, String pw) {
		this.id = id;
		this.pw = pw;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
		
	}
	@Override
	public String toString() {
		return "Stranger [id=" + id + ", pw=" + pw + "]";
	}
	
	


}
// ---- Stranger

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StrangerDAO {
	public int save(String id, String pw) {
		String query = "INSERT INTO leejun (id, pw) VALUES (?, ?)";
		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);){
			stmt.setString(1, id);
			stmt.setString(2, pw);
			
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public List<Stranger> load(){
		String query = "SELECT * FROM leejun";
		List<Stranger> list = new ArrayList<>();
		
		try(Connection conn = ConnectionProvider.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()){
			while (rs.next()) { //하나씩 읽어오기
				
				Stranger st = new Stranger();
				st.setId(rs.getString("id"));
				st.setPw(rs.getString("pw"));
//				String id = rs.getString("id");
//				String pw = rs.getString("pw");
//				
//				Stranger strg = new Stranger();
//				strg.setId("id");
//				strg.setPw("pw");
				
				list.add(st);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
//	public String load(int id) {
//		String query = "SELECT id From junseok WHERE id = ?";
//		
//		try(Connection conn = DriverManager.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(query)){
//			stmt.setInt(1, id);
//			try(ResultSet rs = stmt.executeQuery()){
//				if (rs.next()) {
//					return rs.getString("id");
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	

}
// ----- StrangerDAO
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {
	private static Properties props = new Properties();

	static {
		FileInputStream fis = null;
		
			
			try {
				fis = new FileInputStream(".\\dbConn.properties");
				props.load(fis);
				Class.forName(props.getProperty("DRIVER_NAME"));
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(props.getProperty("URL")
				,props.getProperty("ID")
				,props.getProperty("PASSWORD"));
	}

}
//----- ConnectionProvider
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignIn extends JDialog{
	private static StrangerDAO dao = new StrangerDAO();
	
	public SignIn() {
		//메인 패널
		JPanel mainpnl = new JPanel();
		mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS));
	
		
		//아이디
		JPanel pnlID = new JPanel();
		JLabel lblID = new JLabel("아이디");
		JTextField tfID = new JTextField(15);
		
		pnlID.add(lblID);
		pnlID.add(tfID);
		
		//비밀번호
		JPanel pnlPW = new JPanel();
		JLabel lblPW = new JLabel("비밀번호");
		JPasswordField tfPW = new JPasswordField(15);
		pnlPW.add(lblPW);
		pnlPW.add(tfPW);
		
		//로그인 버튼
		JPanel pnljoin = new JPanel();
		JButton btnjoin = new JButton("로그인");
		JButton btnchk = new JButton("취소");
		
		
		btnchk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		btnjoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Stranger> list = dao.load();
				for (Stranger st : list) {
					if (tfID.getText().trim().equals(st.getId()) && tfPW.getText().trim().equals(st.getPw())) {
						JOptionPane.showMessageDialog(SignIn.this, "로그인되었습니다.");
					} 
				}
			}
		});
		
		mainpnl.add(pnlID);
		mainpnl.add(pnlPW);
		
		pnljoin.add(btnjoin);
		pnljoin.add(btnchk);
		
		add(mainpnl, "North");
		add(pnljoin, "South");
		
		showGUI();
	}
	private void showGUI() {	
		pack();
		setLocation(650,350);
		setVisible(true);
		
	}

}
//----------------SignIn
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignUp extends JDialog{
	private static StrangerDAO dao = new StrangerDAO();
	private boolean chk;
	
	public SignUp() {
		setTitle("회원가입");
		
		//타이틀
		JLabel title = new JLabel("회원가입", JLabel.CENTER);
		title.setForeground(Color.GRAY);
		title.setFont(new Font("휴먼편지체", Font.BOLD, 30));
		
		// 메인 패널
		JPanel mainpnl = new JPanel();
		mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS));
		mainpnl.setOpaque(true);
		mainpnl.setBackground(Color.magenta);
		
		//아이디
		JPanel pnlID = new JPanel();
		JLabel lblID = new JLabel("아이디");
		JTextField tfID = new JTextField(15);
		pnlID.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlID.add(lblID);
		pnlID.add(tfID);
		
		//비밀번호
		JPanel pnlPW = new JPanel();
		JLabel lblPW = new JLabel("비밀번호");
		JPasswordField pfPW = new JPasswordField(15);
		pnlPW.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlPW.add(lblPW);
		pnlPW.add(pfPW);
		
		//비밀번호 확인
		JPanel pnlPH = new JPanel();
		JLabel lblPH = new JLabel("비밀번호 확인");
		JPasswordField pfPH = new JPasswordField(15);
		pnlPH.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlPH.add(lblPH);
		pnlPH.add(pfPH);
		
		JPanel joinpnl = new JPanel();
		JButton joinbtn = new JButton("가입");
		JButton joincel = new JButton("취소");
		
		chk = true;
		joinbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			List<Stranger> list = dao.load();
			for (Stranger st : list) {
				if (tfID.getText().trim().equals(st.getId())||tfID.getText().equals("")||pfPW.getText().equals("")) {
					chk = false;
				}
			}
			if (chk == true) {
				dao.save(tfID.getText(),pfPW.getText());
				dispose();
			}
			}
		});
		
		
		joincel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Main();
			}
		});
		
		mainpnl.add(pnlID);
		mainpnl.add(pnlPW);
		mainpnl.add(pnlPH);
		
		
		joinpnl.add(joinbtn);
		joinpnl.add(joincel);
//		joinbtn.setEnabled(false);
		
		add(title, BorderLayout.NORTH);
		add(mainpnl, BorderLayout.CENTER);
		add(joinpnl, BorderLayout.SOUTH);
		
		showGUI();
		
		
	}

	private void showGUI() {
		pack();
		setVisible(true);
		setLocation(650, 350);
	}
	
	public static Connection getConnection() {
		String url = "jdbc:mysql://localhost:3306/junseok";
		String id = "root";
		String password = "root";
		
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url,id,password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static int addStranger(String id, String pw) {
		Connection conn = getConnection();
		
		return 0;
	}


}
//----- SignUp
DRIVER_NAME=com.mysql.cj.jdbc.Driver
URL=jdbc:mysql://localhost:3306/junseok
ID=root
PASSWORD=root

//------ dbConn.properties
