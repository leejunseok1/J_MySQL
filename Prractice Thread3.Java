class TestVolatile {
	static volatile int i = 0;
	
	static void increment() {
		i++;
	}
}

public class Main {
	public static void main(String[] args) throws InterruptedException {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; ; i++) {
					TestVolatile.increment();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		
		for (int i = 0; ; i++) {
			System.out.println(TestVolatile.i);
		}
		
		
	}
}
//----------------------------------------------------------
class TestDeadLock {
	private final Object left = new Object();
	private final Object right = new Object();
	
	public void leftToRight() {
		synchronized (left) {
			synchronized (right) {
				something();
			}
		}
	}
	
	public void rightToLeft() {
		synchronized (right) {
			synchronized (left) {
				something();
			}
		}
	}
	
	private void something() {
		System.out.println("Hello");
	}
}

public class Main2 {
	public static void main(String[] args) throws InterruptedException {
		TestDeadLock d = new TestDeadLock();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					d.leftToRight();
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					d.rightToLeft();
				}
			}
		});
		
		t.start();
		t2.start();
		
		t.join();
		t2.join();
		System.out.println("프로그램 종료");
	}
}
//---------------------------------------------------------
import java.util.ArrayList;
import java.util.List;
//   여러개의 thread를 만들어서 수행할 일들을 처리하게 하는 용도 
class MyThreadPool {
   private MyBlockingQueue taskQueue;
   private List<MyThread> myThreadList;
   private boolean isStopped;      //    isStopped을 하게 되면 모든 동작들이 멈춘다 (doStop 메소드랑 조금 다름)

   public MyThreadPool() {   
      taskQueue = new MyBlockingQueue();
      myThreadList = new ArrayList<>();   //    thread의 list를 생성해서 5개의 thread을 만듦
      for (int i = 0; i < 5; i++) {            
         myThreadList.add(new MyThread(taskQueue));
      }
      for (MyThread myThread : myThreadList) {
         myThread.start();
      }      //    각각의 특정적으로 생성된 thread가 동시에 자기의 일을 수행하도록 함 
   }         //    1. 할 일이 있다면, Runnable한 구현체(thread)를 1:1 로 만들어서 그 안에 수행할 문장을 넣음(OK, 비효율적)
            //    2. 해야 할 일이 예를 들어 100개가 넘거나 너무 많은 개수를 생성해야될 경우, priority에 의해 실행이 안되는 thread가 발생 가능
            //   3. 해야할 일의 개수만큼의 목록을 만들어두고, thread들이 제각기 자신이 할 수 있는 일을 수행하도록 만들기(효율적)
            //   예) 100개의 일이 있더라도 5개의 thread들을 만들어 두고 계속 사용하고 돌리기(LinkedList, ArrayList... etc)

   public synchronized void execute(Runnable task) {   
      if (this.isStopped) {
         throw new IllegalStateException("스레드 풀이 중단됐습니다.");
      } // BlockingQueue에서 enqueue 메서드 사용
      taskQueue.enqueue(task);
   }
//   synchronized하게 만든 이유 : 여러 thread가 접근할 가능성이 있기 때문에, 문제가 생기지 않게 하기 위함
   public synchronized void stop() {      //   pool에 있는 모든 Thread를 stop하게 하기 위한 메서드
      this.isStopped = true;
      for (MyThread myThread : myThreadList) {
         myThread.doStop();
      }
   }

}

class MyThread extends Thread {      
   private MyBlockingQueue taskQueue;
   private boolean isStopped;

   public MyThread(MyBlockingQueue taskQueue) {
      this.taskQueue = taskQueue;
   }

   // Runnable 한 객체를 실행만 하고 있음
   @Override
   public void run() {
      while (!isStopped) {
         try { // 만들어 놓은 BlockingQueue에서 dequeue뽑아서 사용
            Runnable r = (Runnable) taskQueue.dequeue();
            r.run();
         } catch (Exception e) {

         }

      }
   }

   public synchronized void doStop() {      //   interrupt를 사용해서 프로그램을 종료시킴 : Thread가 멈추진 않지만, 현재 하고 있는 동작을 멈추고 기다리게 함 
      isStopped = true;
      this.interrupt();

   }

   public synchronized boolean isStopped() {
      return isStopped;
   }

}

public class Main3 {
   // Main Thread가 Pool Thread에게 1-100을 뽑아내는 행동 + 100-200을 뽑아내는 행동 + 200-300을 뽑아내는
   // 행동을 명령
   // Pool Thread는 첫 번째 작업 시작 -> 두 번째 작업 시작 -> 마지막 작업 시작함
   // Thread Pool : Thread의 집단화 ->
   public static void main(String[] args) throws InterruptedException {
      MyThreadPool pool = new MyThreadPool();

      pool.execute(new Runnable() {
         // 집어넣은 순서대로 수를 뽑아내는 행동을 실행
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for (int i = 0; i < 100; i++) {
               System.out.println(i);
            }
         }
      });

      pool.execute(new Runnable() {
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for (int i = 100; i < 200; i++) {
               System.out.println(i);
            }
         }
      });

      pool.execute(new Runnable() {
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for (int i = 200; i < 300; i++) {
               System.out.println(i);
            }
         }
      });

      Thread.sleep(3000);
      pool.stop();

   }
}


//---------------------------------------------------------------------
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main4 {
   public static void main(String[] args) {
      ExecutorService pool  =  Executors.newCachedThreadPool();
      pool.execute(new Runnable() {
         
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for(int i = 0; i < 100; i++) {
               System.out.println(i);
            }
         }
      });
      pool.execute(new Runnable() {
         
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for(int i = 0; i < 100; i++) {
               System.out.println(i);
            }
         }
      });
      
      pool.execute(new Runnable() {
         
         @Override
         public void run() {
            System.out.println(Thread.currentThread().getName());
            for(int i = 0; i < 100; i++) {
               System.out.println(i);
            }
         }
      });
      
      try {
         Thread.sleep(1000);
      } catch(InterruptedException e) {
         e.printStackTrace();
      }
      pool.shutdownNow();
   }

}
//-------------------------------------------------------------------------------

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class TaskWithResult implements Callable<Integer> {
// Runnable이랑 비슷한 개념으로, call()이란 메서드로 thread가 해야 할 일을 정의
// 다른 점 :리턴값을 줄 수 있고(제너릭 < >에 리턴할 값 타입 주기), 예외를 알 수 있고 줄 수 있음 

   private int sum;

   @Override
   public Integer call() throws Exception {
      int sum = 0;
      for (int i = 0; i < 100; i++) {
         sum += 0;
      }
      return null;
   }

}

public class Main5 {
   public static void main(String[] args) {
      ExecutorService pool = Executors.newCachedThreadPool();

//      pool.submit(new TaskWithResult());   //   리턴값 반환을 그 즉시 함 : 말이 안됨 
      Future<Integer> futureResult = pool.submit(new TaskWithResult());   // Future를 사용해서 미래의 겨로가 리턴값을 반환하도록 해야 함 
      // 값이 미래에 있음 TaskWithResult의 메서드 결과 값을 마지막에 계산이 다 되고 난 뒤에 리턴값으로 되돌려줌 
      // Waits if necessary for the computation to complete, and then retrieves its result.
      // 계산이 끝날 때까지 기다렸다가, 그 뒤에 결과 값 돌려주기
      //
      try {
//         Integer sum = futureResult.get();
         Integer sum;
         try {
            sum = futureResult.get(1, TimeUnit.MILLISECONDS);// - 1초기다리고 리턴값 돌려줌을 의미
            System.out.println(sum);
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      }
      } catch (TimeoutException e) {
         System.out.println("계산이 아직 안끝남");
      } 
      
      pool.shutdown();
 //   메인 스레드는 pool 스레드에게 일을 시키고 자신의 일을 함
      // pool 스레드는 메인이 시킨 일을 하고 그 결과를 반환해줌 -> 메인은 그 결과를 받아 보기만 하면 됨
      // ******* 결국 일 시켜두고 결과를 받아보고 싶을 때 사용하는 객체 ******
      
   // Runnable이랑 비슷한 개념으로, call()이란 메서드로 thread가 해야 할 일을 정의
   // Run이라는 추상메서드는 작업을 수행한 후에 결과를 볼 수 있는 리턴값을 주지 않음, 원하는 리턴값이 있다면 Callable<>을 사용 
   // 다른 점 : 리턴값을 줄 수 있고(제너릭 < >에 리턴할 값 타입 주기), 예외를 알 수 있고 줄 수 있음 
   }

}
//-------------------------------------------------------------

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main6 {
	public static void main(String[] args) {
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
		Future<Integer> f = pool.schedule(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return 1;
			}
		}, 5, TimeUnit.SECONDS); // 5초뒤에 정수값으로 1을 리턴하라
		
		pool.scheduleAtFixedRate(new Runnable() { // 고정된 시간마다 해야할일이 있으면.  
			@Override
			public void run() {
				System.out.println("1초마다");
			}
		}, 0, 1, TimeUnit.SECONDS); // 0초 뒤에 1초마다 반복하라 
		
		try {
			System.out.println(f.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		pool.shutdown();
	}
}
//---------------------------------------------------

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Locky{
	private int count = 0;
//	private Object lock = new Object(); 
	private Lock myLock = new ReentrantLock(); // 실제로 락을 걸수있는
	// 같은 락을 걸게끔 메서드를 공유
	
	public void increment() {
		myLock.lock();
		try {
			count++;
		}finally { 
			myLock.unlock(); // 락이 무조건 풀리게 만들려고 finally
		} // 가독성이 좋으므로 하는것을 추천.
	}
	public void decrement() {
		myLock.lock();
		try {
			count--;
		}finally { 
			myLock.unlock(); // 락이 무조건 풀리게 만들려고 finally
		}
	}
	public synchronized int getCount() {
		myLock.lock();
		try {
			return count;
		}finally { 
			myLock.unlock(); // 락이 무조건 풀리게 만들려고 finally
		}
	}
}
public class Main7 {

	public static void main(String[] args) {
		Locky l = new Locky();
		ExecutorService pool = Executors.newCachedThreadPool();
		
		pool.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					l.increment();
				}
			}
		});
		pool.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					l.increment();
				}
			}
		});
		pool.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					l.increment();
				}
			}
		});
		
		try {
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("최종값 : " + l.getCount());
	}

}
//-------------------------------------------------------------
import java.util.LinkedList;

class MyBlockingQueue {

   private final LinkedList<Object> list = new LinkedList<>();

   public synchronized void enqueue(Object o) {
      list.add(o);
      notifyAll();
   }

   // queue : 선입선출 FIFO <-> stack : 선입후출 FILO
   public synchronized Object dequeue() {
      while (list.size() == 0) {
         try {
            wait();
         } catch (InterruptedException e) {
         }
      }
      if (list.size() == 0) {
         return null;
      }
      return list.removeFirst();

   }
}
