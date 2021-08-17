
public class Main {

	public static void main(String[] args) {
		System.out.println("작업시작");
		for (int i = 0; i < 1000; i++) {
			System.out.println(i);
		}
		System.out.println("작업종료");

		System.out.println("작업2시작");
		for (int i = 1000; i <= 2000; i++) {
			System.out.println(i);
		}
		System.out.println("작업2종료");
	}
	

}
//----------------------------------------------------



public class Main2 {
	public static void main(String[] args) {
		Thread t = Thread.currentThread();
		
		// 동시작업을 하기위함
		// 필요한 이유 스윙 타이머하면서 느꼈을것
		// 프로그램이 
		System.out.println("Name :" + t.getName()); // 쓰레드의 이름을 알아낼수있음.
		System.out.println("ID : " + t.getId()); // 1번이라는 아이디값이 부여되어있음.
		System.out.println("Alive: " + t.isAlive()); // 일을 하고있는지
		System.out.println("State :" + t.getState()); // 현재상태를 알아보는것 
		System.out.println("Priority :" + t.getPriority()); // 1 Min_Priority ~ 10 Max_Priority (중요도) 
															
		System.out.println("Daemon :" + t.isDaemon()); // 주 스레드의 작업을 돕는 보조적인 역할을 수행하는 스레드
	}

}
//--------------------------------------------------
class MyTask extends Thread {

	@Override
	public void run() {
		System.out.println("My-Task started");
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
		}
		System.out.println("My-Task Done");
	} // 이 쓰레드가 해야하는 일을 오버라이드해서 정의를 시켜놓음
	// 
	
}
public class Main3 {
	public static void main(String[] args) {
		// 메인쓰레드가 시작을 해서 
		
		MyTask task = new MyTask();
		task.setPriority(1);
		task.start(); // 시작

		Thread task2 = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Another-Task started");
				for (int i = 0; i < 10; i++) {
					System.out.println("Another : " + i);
				}
				System.out.println("Another-Task Done");
			}
		});
		task2.start();
		
		System.out.println("Main-Task started"); // 메인작업 시작
		for (int i = 0; i < 10; i++) { 
			System.out.println(i);
		}
		System.out.println("Main-Task Done"); // 실행결과는 매번 달라짐  / 속도의 차이
	}

}
//--------------------------------------------------------class MyClass {
	private int myInt = 0;

	public int getMyInt() {
		return myInt;
	}

	public void increment() {
		myInt++;
	}

	public void decrement() {
		myInt--;
	}
}

public class Main4 {
	public static void main(String[] args) throws InterruptedException {
		MyClass my = new MyClass();
		Thread incre1 = new Thread(new Runnable() {
			@Override
			public void run() { // 2000증가
				for (int i = 0; i < 2000; i++) {
					my.increment();
				}
			}
		});
		Thread incre2 = new Thread(new Runnable() {

			@Override
			public void run() { // 3000증가
				for (int i = 0; i < 3000; i++) {
					my.increment();
				}
			}
		});
		Thread decre1 = new Thread(new Runnable() {
			@Override
			public void run() { // 2000 감소
				for (int i = 0; i < 2000; i++) {
					my.decrement();
				}
			}
		});
		Thread decre2 = new Thread(new Runnable() {
			@Override
			public void run() { // 3000 감소
				for (int i = 0; i < 3000; i++) {
					my.decrement();
				}
			}
		});
		incre1.start();
		incre2.start();
		decre1.start();
		decre2.start();

		incre1.join();
		incre2.join();
		decre1.join();
		decre2.join();

		System.out.println(my.getMyInt());
	}

}

//----------------------------------------------------
import java.util.concurrent.TimeUnit;

public class Main5 {
	public static void main(String[] args) {
		System.out.println("프로그램 시작");
		for (int i = 0; i < 3; i++) {
			try {
//			Thread.sleep(2000);
				TimeUnit.SECONDS.sleep(2); // 위와 같음

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("2초 후에 출력됩니다.");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("출력됩니다. 3초후에");

		}
	}

}
//------------------------------------------------
class MyRunnable implements Runnable{

	@Override
	public void run() {
		String[] message = {"내가", "이준석", "인데?"};
		
			try {
				for (int i = 0; i < message.length; i++) {
					Thread.sleep(4000);
					Thread t = Thread.currentThread();
					
					System.out.println(t.getName() + ":" + message[i]);
				}
			} catch (InterruptedException e) {
				System.out.println("안끝났어");
			}
		}
	}
	

public class Main6 { // 

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new MyRunnable());  
		t.setDaemon(false);

		t.start(); // initialized 
		
		
		int i =0;
		while(t.isAlive()) {
			System.out.println("기다리는중"); // 현재 스레드가 해당 스레드의 작업이 끝날때까지 기다려줌(main이 t를 기다림)
			t.join();
//			if (i == 3 && t.isAlive()) {
//				System.out.println("땡뻘떙뻘");
//				
//				t.interrupt();
//				t.join();
//			}
		}
		System.out.println("종료");
	}

}

