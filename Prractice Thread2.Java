import java.util.Scanner;

public class Main {
	static long startTime = 0;
	static char ch = 'A';
	
	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				startTime = System.currentTimeMillis();
				ch = (char) (65 + (int) (Math.random() * 27));
				System.out.println(ch);
			
				try {
					Thread.sleep(((long) (Math.random() * 5000)) + 1000);
				} catch (InterruptedException e) {
				e.printStackTrace();
				}
				//출력만
			}
		});
		
		System.out.println("화면에 출력되는 문자를 입력하세요~.");
		t.start();
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.next();
			if (str.charAt(0) == ch) {
				System.out.println(System.currentTimeMillis() - startTime);
				 // 입력만
			}
		}
	}

}
//---------------------------------------------------
class Infinite extends Thread{

	@Override
	public void run() {
		try {
			while (!isInterrupted()) { // 인터럽트를 받았나?  받지 않았다면
 				System.out.println("반복합니다."); // 이것을 실행
				sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("인터럽트 받음. 현재 isInterrupted 값 :" + isInterrupted());
		}
	}
	
}


public class Main2 {
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Infinite();
		t.start();
		
		Thread.sleep(5000);
		t.interrupt();
	}

}
