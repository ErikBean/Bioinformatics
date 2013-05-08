import java.io.*;
import java.util.*;
/**Basically want to do the same as global, but provide option "0" in addition to left, right and caddy
 * 	-I think it makes sense to say that we end the sequence when it reaches the bottom of the matrix,
 *  because we want to globally align the gene on  the left and keep all its nucleotides in the final alignment
 *  but we don't care where the top sequence starts and ends b/c aligning w/ a subsequence of genome(s)
	Traceback then can't start at the last cell, it has to look down the whole LAST ROW to find cell w/maximal value.
**/

class SemiglobalSequenceAllignment {
	static String gene="";//gene we're looking for
	static String gen[] = new String[10];//max 10 genomes
	
	static PrintStream ermsg=System.err;
	static PrintStream msg=System.out;
	public static int table[][];
	public static void main(String[] args) {
		//first do global alignment bet gene and gen#0.
		readFile();
		int rows=gene.length()+1;//gene on left of table(rows).need +1 for start cell
		int cols=gen[0].length()+1;//genome on top. these values start@1
		table=new int[rows][cols];//allocates a large amt. of memory.
		for(int i=1;i<rows;i++){
			table[i][0]=i*-2;
		}
		for(int i=0;i<cols;i++){//iterate start cell w/ run
			table[0][i]=i*-2;
		}
		table[0][0]=0;//begin point
		int score=fillTable(rows,cols,0,cols-1);
		msg.println("score= "+score);
		String result=traceback(rows, cols, 0, 0);
//		printBoard(table);
		msg.println("Final Alignment:\n"+result);
		msg.println(gene);
		for(int i=0;i<5;i++){
			
			msg.println(gen[i]);
		}
	}
	public static String traceback(int rows, int cols,int colStart,int colEnd){//looks at table
		int up,left,caddy,bestChoice=0,val=0;//the ones to look at and the one to choose.
		char let1,let2;//left and top seq letters. gene left genome top
		
		int cri=rows-1;//current row index. begin calc at table [1][1]
		int cci=cols-1;//current col. index

		StringBuilder top=new StringBuilder();
		StringBuilder bottom=new StringBuilder();
		while(true){
			if(cri<=0&&cci<=0){break;}//@end
			if(cri<=0){//safety checks
				cri=0;
				up=Integer.MIN_VALUE;
				caddy=Integer.MIN_VALUE;
				left=table[cri][cci-1]-2;
				//charAt starts index@0
				let1='X';//gene on left
				let2=gen[0].charAt(cci-1);//genome on top
			}
			else if(cci<=0){
				cci=0;
				left=Integer.MIN_VALUE;
				caddy=Integer.MIN_VALUE;
				up=table[cri-1][cci]-2;
				//charAt starts index@0
				let1=gene.charAt(cri-1);//gene on left
				let2='X';//genome on top
			}
			else{//not on edge		
				up=table[cri-1][cci]-2;//subtract gap penalty
				left=table[cri][cci-1]-2;
				caddy=table[cri-1][cci-1];//up&left
				//charAt starts index@0
				let1=gene.charAt(cri-1);//gene on left
				let2=gen[0].charAt(cci-1);//genome on top
			}
			//System.out.print(" ("+cri+","+cci+")="+table[cri][cci]);
			if(let1==let2&&cci!=0&&cri!=0){
				caddy+=1;//match	
			}
			else if(cci!=0&&cri!=0){//can't subtract from min value w/o overflow
				caddy-=1;//mismatch
			}
			
			//decrementing cci/cri depends on traceback.
			if(caddy>up&&caddy>left){//diag add both letters
				top.insert(0,let1);
				bottom.insert(0,let2);
				cri-=1;
				cci-=1;
			}
			else if(left>up){//go left
				top.insert(0,'-');
				bottom.insert(0,let2);
				cci-=1;//decrement col
			}
			else{//high road for ties
				top.insert(0,let1);
				bottom.insert(0,'-');
				cri-=1;
			}
		}
		String result=top.toString()+"\n"+bottom.toString();
		return result;
		
	}
	public static int fillTable(int rows, int cols,int colStart,int colEnd){
		int cri=1;//current row index. begin calc at table [1][1]
		int cci=1;//current col. index
		int up,left,caddy,bestChoice=0,val=0;//the ones to look at and the one to choose.
		char let1,let2;//left and top seq letters. gene left genome top
		int endVal=(rows-1)*(cols-1);//table index starts@0.Exit at ending cell.
		while(cri*cci<=endVal){
			bestChoice=0;
			//start@table[1][1]
			up=table[cri-1][cci]-2;//does it make sense to sub constants here??? 
			left=table[cri][cci-1]-2;
			caddy=table[cri-1][cci-1];//up&left
			//charAt starts index@0
			let1=gene.charAt(cri-1);//gene on left
			let2=gen[0].charAt(cci-1);//genome on top
		
			bestChoice=Math.max(up,left);
			bestChoice=Math.max(bestChoice,caddy);//max of 3 neighbors
			if(let1==let2&&bestChoice==caddy){//Nucleotides match
				val=1;
			}
			else if(let1!=let2&&bestChoice==caddy){//only care about the match/mismatch if bestChoice=caddy, NO GAP
				val=-1;//mismatch penalty
			}
			else{
				val=0;//gap
			}
			
			table[cri][cci]=val+bestChoice;//val of match/mismatch + caddy, up-2 or left-2
			if(cri*cci>=endVal){break;}//@end
			if(cri==rows-1){//did last cell in row
				cri=1;//back to top of table
				cci++;//move over 1 column
			}
			else{
				cri++;//increment current row index;
			}
		}
		return val+bestChoice;//final allignment score
	}
	public static void printBoard(int table[][]){
		
		int rows=table.length;
		int cols=table[0].length;

		msg.print("     ");
		for(int i=0;i<cols;i++){
			if(i==0){
				msg.print("0   ");
			}
			else{
				
				if(i<10){
					msg.print(gen[0].charAt(i-1)+"   ");
				}
				
				else{
					msg.print(gen[0].charAt(i-1)+"  ");
				}
			}
		}
		msg.print("\n");
		for(int i=0;i<rows;i++){
			if(i==0){
				msg.print("0: ");
			}
			else{
			msg.print(gene.charAt(i-1)+": ");
			}
			for(int j=0;j<cols;j++){
				if(table[i][j]>=0){
					msg.print(" ");
				}
				msg.print(" "+table[i][j]);
				
				if(table[i][j]<10&&table[i][j]>-10){
					msg.print(" ");
				}
			}
			msg.println("");
			msg.println();
		}
	}
	public static void readFile(){
		Scanner keyboard = new Scanner(System.in);
		Scanner inFile;
		msg.print("Enter name of file containing sequence data:");
		String fname=keyboard.nextLine();
		File readIn=new File(fname);
		try {
			inFile = new Scanner(readIn);
			gene=inFile.nextLine().trim();
			int i=0;
			while(inFile.hasNextLine()){
				gen[i]=inFile.nextLine().trim();//load genomes from file into array
				i++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			ermsg.println("E:File not found. Is text file in parent folder of this java file? Is name spelled correctly? Exiting...");
			System.exit(1);//lazy solution: start program over
			//e.printStackTrace();
		}
		keyboard.close();
	}
	public static int fillTableVerbose(int rows, int cols,int colStart,int colEnd){
		int cri=1;//current row index. begin calc at table [1][1]
		int cci=1;//current col. index
		int up,left,caddy,bestChoice=0,val=0;//the ones to look at and the one to choose.
		char let1,let2;//left and top seq letters. gene left genome top
		int endVal=(rows-1)*(cols-1);//table index starts@0.Exit at ending cell.
		//msg.println("endVal="+endVal);
		while(cri*cci<=endVal){
			bestChoice=0;
			//msg.println("cri="+cri+" cci="+cci);
			//start@table[1][1]
			up=table[cri-1][cci]-2;//does it make sense to sub constants here??? 
			left=table[cri][cci-1]-2;
			caddy=table[cri-1][cci-1];//up&left
			//charAt starts index@0
			let1=gene.charAt(cri-1);//gene on left
			let2=gen[0].charAt(cci-1);//genome on top
		
			bestChoice=Math.max(up,left);
			bestChoice=Math.max(bestChoice,caddy);//max of 3 neighbors
			if(let1==let2&&bestChoice==caddy){//Nucleotides match
				//msg.println("letters did match and caddy was my choice");
				val=1;
			}
			else if(let1!=let2&&bestChoice==caddy){//only care about the match/mismatch if bestChoice=caddy, NO GAP
				//msg.println("letters did NOT match and bestChoice was caddy");
				val=-1;//mismatch penalty
			}
			else{
				val=0;//gap
			}
	
	//		msg.println("comparing "+let1+" and "+let2);
	//		msg.println("up="+up);
	//		msg.println("left="+left);
	//		msg.println("caddy="+caddy);
	//		msg.println("bestChoice was"+bestChoice);
	//		msg.println("val is "+val);
			table[cri][cci]=val+bestChoice;//val of match/mismatch + caddy, up-2 or left-2
			
			if(cri*cci>=endVal){break;}
			if(cri==rows-1){//last row
				cri=1;//back to top of table
				cci++;//move over 1 column
			}
			else{
				cri++;//increment current row index;
			}
			printBoard(table);
		}
		msg.println("score="+(val+bestChoice)+"\nFinal board: \n");
		printBoard(table);		
		return val+bestChoice;//final allignment score
	}
}