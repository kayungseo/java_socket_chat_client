package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*
 * 1. 서버 요청 
 * 2. 나가기 가능
 * */

public class SocketClient{
	static String SERVER_IP = "192.168.7.36";
	static int SERVER_PORT = 1225; 
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	private Scanner sc;
	 
	public void go() throws UnknownHostException, IOException {
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			System.out.println("**서버 접속**");
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(),true);
			sc = new Scanner(System.in);
			
			String nickname = getNickname(socket, sc);
			
			ReceiverWorker rw = new ReceiverWorker();
			Thread thread = new Thread(rw);
			thread.setDaemon(true);
			thread.start();
			
			System.out.println("**["+nickname+"]님 서버에 접속**");
			
			while (true) {  
				String message = sc.nextLine();//서버에 보낼 메세지
				pw.println("00x1;"+message);
				
				if (message.trim().equals("종료")) {
					System.out.println("**ChatClient 종료합니다**");
					break;
				}
			}
		}finally {
			closeAll();
		}
	}
	
	public String getNickname(Socket socket, Scanner sc) throws IOException {
		System.out.println("닉네임을 입력하세요: ");
		String nickname = sc.nextLine();
		PrintWriter writer;  
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.println("00x0;"+nickname);  //서버로 데이터를 전송한다. 
		writer.flush();   //버퍼 안에 있는 값들을 전부 비워준다. 
		System.out.println("닉네임 등록!");
		
		return nickname; 
	}
	
	
	class ReceiverWorker implements Runnable {
		public void run() {
			try {
				receiveMessage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//받은 메세지와 보내는 메세지 구분 필요 
		public void receiveMessage() throws IOException {
			while (true) {
				String message = br.readLine();

				if (message == null) {
					break;
				}  
				System.out.println("* "+message); 
			}
		}
	}
	
	public static void main(String[] args) {
		 
		SocketClient client = new SocketClient();
		try {
			client.go();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeAll() throws IOException {
		if (pw != null)
			pw.close();
		if (sc != null)
			sc.close();
		if (br != null)
			br.close();
		if (socket != null)
			socket.close();
	}
	 
}
