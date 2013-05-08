import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

class State{
	public String name;
	public int id;//index in dists[][]
	public boolean cpg;//is this + or - 
	public double dists[];
	public State(int id, String info[]){
		this.id=id;
		name=info[0]+info[1];
		cpg=(info[1].equals("+"));
		dists=new double[info.length-2];
		for (int i = 2; i < info.length; i++) {
			dists[i-2]=Double.parseDouble(info[i]);
		}
	}
	public String toString(){
		return ("me:"+name+" "+Arrays.toString(dists));
	}
	public void print(){
		System.out.println(toString());
	}
}


class HMMViterbi {

	/**
	 * @param args
	 */
	public double[][] distances=new double[10][10];
	public static State[] states=new State[10];
	public static String sequences[];
	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("Please specify cpgs.hmm file and sequence file. Exiting...");
			System.exit(1);
		}
		readCpgFile(args[0]);
		//for (State s:states) {s.print();}
		readDataFile(args[1]);
		
		
		
		
		
		
		System.exit(0);
	}
	private static void readDataFile(String fname) {
		Scanner inFile;
		File readIn=new File(fname);
		try {
			inFile = new Scanner(readIn);	
			int i=0;
			while(inFile.hasNextLine()){
				inFile.nextLine();
				i++;//just counting number of lines in file
			}
			sequences=new String[i];
			i=0;
			inFile.close();
			inFile = new Scanner(readIn);	
			while(inFile.hasNextLine()){
				sequences[i]=inFile.nextLine();
			
				i++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("E:File not found. Exiting...");
			System.exit(1);
		}
		
	}
	public static void readCpgFile(String fname){
		Scanner inFile;
		File readIn=new File(fname);
		try {
			inFile = new Scanner(readIn);				
			int i=0;
			inFile = new Scanner(readIn);
			while(inFile.hasNextLine()){
				String split[]=inFile.nextLine().split("	");
				states[i]=new State(i, split);
				i++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("E:File not found. Exiting...");
			System.exit(1);
		}
	}
}
