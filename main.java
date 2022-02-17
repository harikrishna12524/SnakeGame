import java.util.*;
import java.io.*;
import java.lang.Math;

public class main{
    static Scanner s = new Scanner(System.in);
    static int boardWidth = 20;
    static int boardLength = 20;
    static int timeSleep = 500;
    static int startLength = 5;
    // static thread1 t1 = new thread1();
    static String com = "d";
    static int[][] gameWindow = new int[boardWidth][boardLength];
    static int[] p = {5,5};
    static Snake python = new Snake(boardWidth/2, boardLength/2);
    static BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));
    static String border = "";
    static int score;
    static int topScore;
    static Snake.Coord food;
    static boolean pause = false;


    static{
        for(int i = 0; i < boardLength*1.2; i++){
            border += "%";
        }
    }
    
    public static void cls(){
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch(Exception e){
            System.out.println(e);
        }
	}

    public static void enterWait(){
        System.out.print("Press Enter to continue...");
        try{
            s.nextLine();
        }catch(Exception e){
            System.out.println("No Input");
        }
    }

    public static void displayGame() throws Exception{
        boolean head = true;
        output.write("Score : " + score + "\n");
        // output.write("Food at "+ food.x + " " + food.y + "\n");
        output.write(border + "\n");

		for(int i = 0; i < boardWidth; i++ ) {
            // System.out.print("/");
            output.write('%');
            for(int j = 0; j < boardLength; j++){
                if(gameWindow[i][j]==1){
                    output.write('#');
                    gameWindow[i][j] = 0;
                }else if(gameWindow[i][j] == 2){
                    output.write('*');
                }else if(gameWindow[i][j] == 3){
                    output.write('@');
                    gameWindow[i][j] = 0;
                }
                else{
                    output.write(' ');
                }
            }
            output.write("%\n");
        }

        // System.out.println("////////////");
        output.write(border + "\n");
        output.flush();
	}

    public static boolean move(){
        boolean is_valid = python.play(com);

        Snake.Coord temp = python.snakePoints.get(0);
        gameWindow[temp.x][temp.y] = 3;
        for(int i = 1; i < python.snakePoints.size(); i++){
            int x = python.snakePoints.get(i).x;
            int y = python.snakePoints.get(i).y;
            gameWindow[x][y] = 1;
        }

        return is_valid;

    }

    public static void bestScore(){
        cls();
        System.out.println("Top Score : " + topScore);
        enterWait();
    }

    public static void instruction(){
        cls();
        System.out.println("Direction Keys\nA - Left\nW - Up\nD - Right\nS - Down");
        System.out.println("Press the key [w,s,a,d] once and press enter to change snake direction");
        System.out.println("One Snack Adds One Score and also One Size to Snake");
        enterWait();
    }

    public static void difficulty(){
        cls();
        System.out.print("Enter Difficulty (1 - 5) : ");
        int diff = s.nextInt();
        diff = diff>5?5:diff;
        diff = diff<1?1:diff;

        timeSleep = 500 - (diff - 1)*100;
        startLength = diff*2;
    }

    public static void welcome(){

        int com1 = 1;
        while(com1 != 0){
            cls();
            System.out.println(" Snake Classic ");
            
            // System.out.println("Invalid Choise");
            System.out.print("1 - New Game\n2 - Best Score\n3 - Instruction\n4 - Difficulty\n0 - Quit\nChoise : ");

            com1 = s.nextInt();s.nextLine();
            if(com1 == 1){
                game();
            }else if(com1 == 2){
                bestScore();
            }else if(com1 == 3){
                instruction();
            }else if(com1==4){
                difficulty();
            }else if(com1 == 0){
                break;
            }
            else{
                com1 = -1;
            }
        }

    }

    public static void game(){
        python = new Snake(boardWidth/2, boardLength/2);
        thread1 t1 = new thread1();
		t1.start();

        for(int i = 0 ; i  < startLength; i++){
            python.extend(com);
        }
        python.initFood();
        
        cls();
        while(!com.equals("e")){
            if(pause){
                pause = false;
                enterWait();
                t1.resume();
            }
            if(move()){
                cls();
                try{displayGame();}catch(Exception e){}
            }else{
                System.out.println("Snake Bit itself");
                // t1.stop();
                break;
            }
            try{Thread.sleep(timeSleep);}catch(Exception e){}
        }

        t1.stop();
        System.out.println("Game Exit");

        if(score > topScore){topScore = score;}
        score = 0;
        com = "s";
        
        gameWindow = new int[boardWidth][boardLength];
        enterWait();

    }

	public static void main(String[] args) {
        
        welcome();
		
		
		
    }
}

class Snake{
    class Coord{
        int x;
        int y;

        public Coord(int x, int y){
            this.x = x;
            this.y = y;
        }

        public boolean equals(Coord c){
            if(c.x == x && c.y == y){
                return true;
            }return false;
        }
    }

    ArrayList<Coord> snakePoints = new ArrayList<Coord>();
    Coord food;

    public Snake(int x, int y){
        Coord head = new Coord(x,y);
        snakePoints.add(head);
    }

    public boolean contains(Coord c){
        for(int i = 0; i < snakePoints.size(); i++){
            Coord temp = snakePoints.get(i);
            if(temp.x == c.x && temp.y == c.y){
                return true;
            }
        }
        return false;
    }

    public Coord randCoord(int w, int l){
            int randX = (int)(Math.random()*(l));
            int randY = (int)(Math.random()*(w));
            Coord rtn = new Coord(randX, randY);
            return rtn;
    }

    public void initFood(){
        int w = main.boardWidth, l = main.boardLength;
        Coord temp_food;
        do{
            temp_food = randCoord(l,w);
        }
        while(contains(temp_food));
        food = temp_food;
        main.food = temp_food;
        main.gameWindow[food.x][food.y] = 2;
    }

    public boolean play(String dir){
        Coord head = new Coord(0,0);
        Coord prevHead = snakePoints.get(0);
        int w = main.boardWidth, l = main.boardLength;


        if(dir.equals("s")){
            head.y = prevHead.y;
            if(prevHead.x == w-1){
                head.x = 0;
            }else{
                head.x = prevHead.x + 1;
            }
        }else if(dir.equals("w")){
            head.y = prevHead.y;
            if(prevHead.x == 0){
                head.x = w-1;
            }else{
                head.x = prevHead.x - 1;
            }
        }else if(dir.equals("d")){
            head.x = prevHead.x;
            if(prevHead.y == l-1){
                head.y = 0;
            }else{
                head.y = prevHead.y + 1;
            }
        }else if(dir.equals("a")){
            head.x = prevHead.x;
            if(prevHead.y == 0){
                head.y = l-1;
            }else{
                head.y = prevHead.y - 1;
            }
        }

        if(contains(head)){
            return false;
        }else if(head.equals(food)){
            snakePoints.add(0, head);
            main.score++;
            initFood();
        }else{
            snakePoints.add(0, head);
            snakePoints.remove(snakePoints.size()-1);
        }

        return true;


    }

    public boolean move(String dir){

        boolean is_valid = extend(dir);
        snakePoints.remove(snakePoints.size()-1);
        return is_valid;
    }

    public boolean extend(String dir){
        Coord head = new Coord(0,0);
        Coord prevHead = snakePoints.get(0);
        int w = main.boardWidth, l = main.boardLength;


        if(dir.equals("s")){
            head.y = prevHead.y;
            if(prevHead.x == w-1){
                head.x = 0;
            }else{
                head.x = prevHead.x + 1;
            }
        }else if(dir.equals("w")){
            head.y = prevHead.y;
            if(prevHead.x == 0){
                head.x = w-1;
            }else{
                head.x = prevHead.x - 1;
            }
        }else if(dir.equals("d")){
            head.x = prevHead.x;
            if(prevHead.y == l-1){
                head.y = 0;
            }else{
                head.y = prevHead.y + 1;
            }
        }else if(dir.equals("a")){
            head.x = prevHead.x;
            if(prevHead.y == 0){
                head.y = l-1;
            }else{
                head.y = prevHead.y - 1;
            }
        }

        if(!contains(head)){
            snakePoints.add(0, head);
            return true;    
        }else{
            return false;
        }

    }
}


class thread1 extends Thread{
    Scanner s = new Scanner(System.in);
	public void run() {
        
        String com = "d";
        while(!com.equals("e")){
            com = s.next();
            if((main.com.equals("s") || main.com.equals("w")) && (com.equals("a") || com.equals("d"))){
                main.com = com;
            }else if((main.com.equals("a") || main.com.equals("d")) && (com.equals("w") || com.equals("s"))){
                main.com = com;
            }else if(com.equals("e")){
                main.com = com;
                s.close();
                stop();
            }else if(com.equals("q")){
                main.pause = true;
                suspend();
            }
        }
	}

}