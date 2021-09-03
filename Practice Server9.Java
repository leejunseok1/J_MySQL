package 채팅방예쩨1;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
 ServerSocket ss;
 Vector<Guest> v = new Vector<Guest>();
 HashMap<String, Vector> map = new HashMap<String, Vector>();

 public Server(){
  try {
   ss = new ServerSocket(9999);
   while(true){
    Socket s = ss.accept();
    Guest g = new Guest(s, this);
    addGuest(g);
    g.setDaemon(true);
    g.start();   
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 // 대기실 사용자 추가
 public void addGuest(Guest g){
  v.add(g);
 }
 // 대기실 사용자 제거
 public void removeGuest(Guest g){
  v.remove(g);
 }
 // 방 추가
 public void addRoom(String roomtitle){
  Vector<Guest> rv = new Vector<Guest>();
  map.put(roomtitle, rv);
 }
 // 방 제거
 public void removeRoom(String roomtitle){
  map.remove(roomtitle);
 }
 // 방 사용자 추가
 public void addRoomGuest(Guest g){
  Vector<Guest> rv = map.get(g.roomtitle);
  rv.add(g);
 }
 // 방 사용자 제거
 public void removeRoomGuest(Guest g){
  Vector<Guest> rv = map.get(g.roomtitle);
  rv.remove(g);
  if(rv.size()==0){
   removeRoom(g.roomtitle);
   broadcast("removeroom/");
  }
 }
 // 대기실 모두에게
 public void broadcast(String msg){
  for(Guest g : v){
   g.sendMsg(msg);
  }
 }
 // 방 사용자 모두에게
 public void broadcastRoom(String roomtitle, String msg){
  if(map.get(roomtitle)!=null){
   Vector<Guest> rv = map.get(roomtitle);
   for(Guest g : rv){
    g.sendMsg(msg);
   }
  }
 }
 // 귓속말
 void whisper(String roomtitle, String receiveName, String sendName, String msg){
  Vector<Guest> rv = map.get(roomtitle);
  for( Guest g : rv){
   if(g.id.equals(receiveName))
    g.sendMsg("whisper/"+sendName+"/"+msg);
  }
 }
 // 방 목록
 public void broadcastRoomList(){
  String roomlist="roomlist/";
  Set<String> set = map.keySet();
  for(String roomtitle : set){
   String name=roomtitle;
   roomlist+=roomtitle+"/";
   for(int i=0; i<map.size(); i++){
    Vector<Guest> rv = map.get(name);
    roomlist+=rv.size()+"/";    
   }
  }
  broadcast(roomlist);
 }
 // 대기실 사용자 목록
 public void broadcastGuestList(){
  String guestlist="guestlist/";
  for(Guest g : v){
   guestlist+=g.id+"/";
  }
  broadcast(guestlist);
 }
 // 방 사용자 목록
 public void broadcastRoomGuestList(String roomtitle){
  String roomguestlist="roomguestlist/";
  if(map.get(roomtitle)!=null){
   Vector<Guest> rv = map.get(roomtitle);
   if(rv.size()>0){
    for(Guest g : rv){
     roomguestlist+=g.id+"/";
    }
    broadcastRoom(roomtitle, roomguestlist);
   }
  }
 }

 public static void main(String args[]){
  Server server = new Server();
 }
}
// Guest  

package 채팅방예쩨1;

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class Guest extends Thread{
 Socket s;
 Server server;
 BufferedReader br;
 BufferedWriter bw;
 String id, roomtitle, say, receivename, sendname, whisper;

 public Guest(Socket s, Server server){
  this.s = s;
  this.server = server;
  try {
   br = new BufferedReader(new InputStreamReader(s.getInputStream()));
   bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

 public void run(){
  String line="";
  try {
   while((line=br.readLine())!=null){
    System.out.println(line);
    String[] arr = parseMsg(line.substring(line.indexOf("/")+1));
    if(line.startsWith("in/")){       // 대기실 입장
     id = arr[0];
     server.broadcastRoomList();
     server.broadcastGuestList();
    }else if(line.startsWith("out/")){     // 대기실 퇴장
     id = arr[0];
     server.removeGuest(this);
     server.broadcastRoomList();
     server.broadcastGuestList();
     terminate();     
    }else if(line.startsWith("makeroom/")){  // 대화방 만들기
     roomtitle = arr[0];
     id = arr[1]+"(king)";
     server.addRoom(roomtitle);
     server.addRoomGuest(this);
     server.removeGuest(this);
     server.broadcastRoomList();
     server.broadcastGuestList();
     server.broadcastRoomGuestList(roomtitle);
    }else if(line.startsWith("removeroom/")){  // 대화방 제거
     server.broadcastRoomList();
     server.broadcastGuestList();    
    }else if(line.startsWith("enterroom/")){   // 대화방 입장
     roomtitle = arr[0];
     id = arr[1];
     server.addRoomGuest(this);
     server.removeGuest(this);
     server.broadcastRoomList();
     server.broadcastGuestList();
     server.broadcastRoomGuestList(roomtitle);
     server.broadcastRoom(roomtitle, line);
    }else if(line.startsWith("exitroom/")){   // 대화방 퇴장
     roomtitle=arr[0];
     id = arr[1];
     server.addGuest(this);
     server.removeRoomGuest(this);
     server.broadcastRoomList();
     server.broadcastGuestList();
     server.broadcastRoomGuestList(roomtitle);
     server.broadcastRoom(roomtitle, line);
    }else if(line.startsWith("say/")){     // 대화방 대화
     roomtitle = arr[0];
     id = arr[1];
     say = arr[2];
     server.broadcastRoom(roomtitle, line);
    }else if(line.startsWith("whisper/")){   // 대화방 귓속말
     roomtitle = arr[0];
     receivename = arr[1];
     sendname = arr[2];
     whisper = arr[3];
     server.whisper(roomtitle, receivename, sendname, whisper);
    }
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 // 메시지 보내기
 public void sendMsg(String msg){
  try {
   bw.write(msg+"\n");
   bw.flush();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 // 메시지 파싱
 public String[] parseMsg(String msg){
  StringTokenizer st = new StringTokenizer(msg, "/");
  String[] arr = new String[st.countTokens()];
  for(int i=0; st.hasMoreTokens(); i++){
   arr[i] = st.nextToken();
  }
  return arr;
 }
 // 자원 정리
 public void terminate(){
  try {
   br.close();
   bw.close();
   s.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
}


