package com.game.shooting;

//UML으로 다이어그램 확인하기
//BREAK POINT [중단점] (ctrl+shift +B) debug에 활용
//Step Over(F6)->한줄 실행하고 멈춤
import java.awt.*;
import java.awt.event.*;	//KeyListener(Interface)를 사용하려면 필요함
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*; 		//JFrame 사용하려면 필요함

//GUI를 구현하려면 JFrame을 상속함
//KeyListener : 키보드에서 키 눌린거 처리할 때 사용함
//Runnable : 쓰레드를 사용하려면 implements 해야한다
public class Shoot extends JFrame implements Runnable, KeyListener {
	 private BufferedImage bi = null; 
	 private ArrayList msList = null;	//ArrayList 객체를 담을 변수 선언
	 private ArrayList enList = null;	//ArrayList 객체를 담을 변수 선언
	 //방향키랑 발사키 담는 변수??
	 private boolean left = false, right = false, up = false, down = false, fire = false;
	 private boolean start = false, end = false;
	 private int w = 300, h = 500, x = 150, y = 450, xw =20, xh =20;
	 											//w: 창 너비?, h:창 높이?, x: 플레이어 x좌표 ??, y: 플레이어y좌표?? xw:xh = 플레이어의 몸크기
	 
	 
	 public Shoot() { 						//Shoot 클래스 생성자 생성자 = 객체생성 초기화
		  bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		  msList = new ArrayList();	//ArrayList 객체를 한개 생성
		  enList = new ArrayList();	//ArrayList 객체를 한개 생성
		  this.addKeyListener(this);
		  this.setSize(w, h);				//창의 크기를 결정하는거같다.. 가로.세로같이?? --> 가로/세로 맞음
		  this.setTitle("Shooting Game By rryong"); //이건 창제목
		  this.setResizable(false);		//창크기 조절가능 false면 안바뀌고 true면 바꿀수있다
		  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		  										//프로그램을 안정적으로 종료 시켜줌 / 안하면 창만꺼지고 프로그램은 계속 실행중이다
		  this.setVisible(true);			//창 화면에 보여줄지 말지 결정(ture는 보여주고 false는 보여주지않는다(에러 = 예외처리발생)) 
	 }
 
 public void run() {
  try {
	   int msCnt = 0;
	   int enCnt = 0;
	   while(true) {				//무한루프 ㅡ_ㅡ
		    Thread.sleep(10);	//게임속도 총알나가는속도 숫자가 작아지면 빨라지고 커지면 느려진다
		    
		    if(start) {				//start 참이면 아래로
		     if(enCnt > 200) {  //적군들 나오는 숫자
		      enCreate();			//
		      enCnt = 0;
		     }
		     if(msCnt >= 100) {
		      fireMs();
		      msCnt = 0;
		     }
		     msCnt += 10;
		     enCnt += 10;
		     keyControl();
		     crashChk();
		    }
		    draw();
		   }
	  } catch(Exception e) { 
	   e.printStackTrace();
	  }
	 }
 public void fireMs() {
  if(fire) {
   if(msList.size() < 100) {
    Ms m = new Ms(this.x, this.y);
    msList.add(m);
   }
  }
 }
 public void enCreate() {
	  for(int i = 0; i < 9; i++) {	//9번 반복 
		  //지역변수&Scope
		   double rx = Math.random() * (w - xw);
		   double ry = Math.random() * 50;
		   Enemy en = new Enemy((int)rx, (int)ry);
		   enList.add(en);
	  }
 }
 public void crashChk() {
  Graphics g = this.getGraphics();
  Polygon p = null;
  for(int i = 0; i < msList.size(); i++) {
   Ms m = (Ms)msList.get(i);
   for(int j = 0; j < enList.size(); j++) {
    Enemy e = (Enemy)enList.get(j);
    int[] xpoints = {m.x, (m.x + m.w), (m.x + m.w), m.x};
    int[] ypoints = {m.y, m.y, (m.y + m.h), (m.y + m.h)};
    p = new Polygon(xpoints, ypoints, 4);
    if(p.intersects((double)e.x, (double)e.y, (double)e.w, (double)e.h)) {
     msList.remove(i);
     enList.remove(j);
    }
   }
  }
  for(int i = 0; i < enList.size(); i++) {
   Enemy e = (Enemy)enList.get(i);
   int[] xpoints = {x, (x + xw), (x + xw), x};
   int[] ypoints = {y, y, (y + xh), (y + xh)};
   p = new Polygon(xpoints, ypoints, 4);
   if(p.intersects((double)e.x, (double)e.y, (double)e.w, (double)e.h)) {
    enList.remove(i);
    start = false;
    end = true;
   }
  }
 }
 
 public void draw() {
  Graphics gs = bi.getGraphics();
  gs.setColor(Color.white);
  gs.fillRect(0, 0, w, h);
  gs.setColor(Color.black);
  gs.drawString("Enemy 객체수 : " + enList.size(), 180, 50);
  gs.drawString("Ms 객체수 : " + msList.size(), 180, 70);
  gs.drawString("게임시작 : Enter", 180, 90);
  
  if(end) {
   gs.drawString("G A M E     O V E R", 100, 250);
  }
  
  gs.fillRect(x, y, xw, xh);
   
  for(int i = 0; i < msList.size(); i++) {
   Ms m = (Ms)msList.get(i);
   gs.setColor(Color.blue);
   gs.drawOval(m.x, m.y, m.w, m.h);
   if(m.y < 0) msList.remove(i);
   m.moveMs();
  }
  gs.setColor(Color.black);
  for(int i = 0; i < enList.size(); i++) {
   Enemy e = (Enemy)enList.get(i);
   gs.fillRect(e.x, e.y, e.w, e.h);
   if(e.y > h) enList.remove(i);
   e.moveEn();
  }
  
  Graphics ge = this.getGraphics();
  		//예외처리함으로써 프로그램이 오류나도 종료가 되지않고 Catch문으로 계속 실행하게해서 오류확인
  try {
	  ge.drawImage(bi, 0, 0, w, h, this);
  }
  catch (java.lang.NullPointerException e) {
	  	//java.lang.NullPointerException 예외가 생기면 실행할 코드
	  	//프로그램이 오류나도 종료되지않고 계속실행중
	  System.out.println("java.lang.NullPointerException 이라는 예외 발생");
   }
}
 
 public void keyControl() {
  if(0 < x) {
   if(left) x -= 3;
  }
  if(w > x + xw) {
   if(right) x += 3;
  }
  if(25 < y) {
   if(up) y -= 3;
  }
  if(h > y + xh) {
   if(down) y += 3;
  }
 }
 
 public void keyPressed(KeyEvent ke) {
  switch(ke.getKeyCode()) {
  case KeyEvent.VK_LEFT:
   left = true;
   break;
  case KeyEvent.VK_RIGHT:
   right = true;
   break;
  case KeyEvent.VK_UP:
   up = true;
   break;
  case KeyEvent.VK_DOWN:
   down = true;
   break;
  case KeyEvent.VK_A:
   fire = true;
   break;
  case KeyEvent.VK_ENTER:
   start = true;
   end = false;
   break;
  }
 }
 
 public void keyReleased(KeyEvent ke) {
  switch(ke.getKeyCode()) {
  case KeyEvent.VK_LEFT:
   left = false;
   break;
  case KeyEvent.VK_RIGHT:
   right = false;
   break;
  case KeyEvent.VK_UP:
   up = false;
   break;
  case KeyEvent.VK_DOWN:
   down = false;
   break;
  case KeyEvent.VK_A:
   fire = false;
   break;
  }
 }
 
 public void keyTyped(KeyEvent ke) {}
 
 public static void main(String[] args) {
  Thread t = new Thread(new Shoot()); //쓰레드 하나 생성, shoot 클래스를 가지고 쓰레드를 만드는듯??
  t.start(); // 쓰레드를 시작하는것 같음
 }
}

class Ms {
 int x;
 int y;
 int w = 5;
 int h = 5;
 public Ms(int x, int y) {
  this.x = x;
  this.y = y;
 }
 public void moveMs() {
  y--;
 }
}

class Enemy {
 int x;
 int y;
 int w = 10;
 int h = 10;
 public Enemy(int x, int y) {
  this.x = x;
  this.y = y;
 }
 public void moveEn() {
  y++;
 } 
}
