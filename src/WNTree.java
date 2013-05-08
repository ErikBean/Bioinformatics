import java.io.File;
import java.io.FileNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Scanner;

class WNTree {
	public static double dist[][];
	//public static String speciesNames[];
	public static Taxa nodes[];
	public static HashMap<String,Integer> included=new HashMap<String, Integer>();
	public static HashMap<String,Integer> excluded=new HashMap<String, Integer>();

	public static int num;//current number of nodes. increment this when a node is added
	public static void main(String[] args) {
		readFile();

		Taxa toMerge[]=new Taxa[2];
		toMerge=getLowBiasedDistance();

		System.out.println("Picked "+toMerge[0].name+" and "+toMerge[1].name+" as candidates for merge");
		included.remove(toMerge[0].name);
		included.remove(toMerge[1].name);
		excluded.put(toMerge[0].name, toMerge[0].id);
		excluded.put(toMerge[1].name, toMerge[1].id);
		merge(toMerge);
		num--;


	}
	private static void merge(Taxa[] toMerge) {
		
		
	}
	public static Taxa[] getLowBiasedDistance(){
		double u[]=getUScores();//u 
		double biasDist;
		double min=Integer.MAX_VALUE;
		Taxa a = null; Taxa b = null;
		for(int i=0;i<num;i++){
			for(int j=0;j<i;j++){//triangular! No need to compare both A>B AND B>A
				biasDist=getTableDistance(i, j)-u[i]-u[j];//=(table dist A>B)-(A's u-score)-(B's u-score)
				//System.out.println("i,j: "+i+" "+j+" biasdist="+biasDist);
				if(biasDist<min && i!=j){//need tie case too
					min=biasDist;
					a=nodes[i];
					b=nodes[j];
				}
				else if(biasDist==min && i!=j){//TIE DIST CASE
					int l1=a.name.length()+b.name.length();//old best length
					int l2=nodes[i].name.length()+nodes[j].name.length();
					if(l2<l1){//This is wrong. Base on time order of creation not length
						a=nodes[i];
						b=nodes[j];
					}
					else if(l2==l1){//also equally "leafy"
						int alphaNum1=0;int alphaNum2=0;
						for(int k=0;k<a.name.length();k++){alphaNum1+=a.name.charAt(k);}
						for(int k=0;k<b.name.length();k++){alphaNum1+=b.name.charAt(k);}
						for(int k=0;k<nodes[i].name.length();k++){alphaNum2+=nodes[i].name.charAt(k);}
						for(int k=0;k<nodes[j].name.length();k++){alphaNum2+=nodes[j].name.charAt(k);}
						alphaNum1/=a.name.length()+b.name.length();//avg old
						alphaNum2/=nodes[i].name.length()+nodes[j].name.length();//avg new
						if(alphaNum2<alphaNum1){//pick (avg)lower letters
							a=nodes[i];
							b=nodes[j];
						}
					}
				}
				
			}
		}
		Taxa result[]=new Taxa[2];
		result[0]=a;result[1]=b;
		return result;
	}
	public static double[] getUScores(){
		double u[]=new double[num];
		int k=0;
		for(int n=0;n<num;n++){
			for(int i=0;i<num;i++){//go down row of taxa'a id
				u[n]+=getTableDistance(n,k);
				//System.out.println("dist btw"+n+", "+i+" adding "+getTableDistance(n,k));
				k=(i+1)%num;
			}
			u[n]/=num-2;
		}
				
		return u;
	}
	public static double getTableDistance(int a, int b){//table indices
		if(a==b){
			return 0.0;
		}
		else if(b>a){ return dist[b][a];}//col must be <=row
		else{
			return dist[a][b];
		}
	}
	public static void readFile(){
		Scanner keyboard = new Scanner(System.in);
		Scanner inFile;
		System.out.print("Enter name of file containing species data:");
		String fname=keyboard.nextLine();
		File readIn=new File(fname);
		String speciesNames[];
		String temp[];
		try {
			inFile = new Scanner(readIn);
			speciesNames=inFile.nextLine().split("	");
			dist=new double[(speciesNames.length*2)-1][];//allocate rows
			nodes=new Taxa[(speciesNames.length*2)-1];//array for Taxa objects to reside in
			for(int i=1;i<=dist.length;i++){
				dist[i-1]=new double[i];//allocate ragged
			}
			int i=0;
			while(inFile.hasNextLine()){
				temp=inFile.nextLine().split("	");
				nodes[i]=new Taxa(Character.toString((char) (i+65)), i, speciesNames[i]);
				//nodes[i].print();
				for(int j=0; j<temp.length;j++){
				dist[i][j]=Double.valueOf(temp[j]);
				}
				i++;num++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("E:File not found. Is text file in parent folder of this java file? Is name spelled correctly?");
			readFile();
			//e.printStackTrace();
		}
		keyboard.close();
	}

}
class Taxa{
	public Taxa neighbor1;
	public Taxa neighbor2;	
	public Taxa neighbor3;	
	public String name;
	public String species;
	public int id;
	boolean isLeaf;
	public Taxa(String name, int id, String species){
		this.name=name;
		this.id=id;
		this.species=species;
		isLeaf=true;
		
	}
	public Taxa(int id, Taxa neighbor1, Taxa neighbor2){
		this.id=id;
		this.neighbor1=neighbor1;
		this.neighbor2=neighbor2;
		isLeaf=false;
		name=neighbor1.name+neighbor2.name;
		if(neighbor1.isLeaf){
			neighbor1.neighbor1=this;//my neighbors neighbor is myself
		}
		else{
			neighbor1.neighbor3=this;//intl node
		}
		if(neighbor2.isLeaf){
			neighbor2.neighbor1=this;//my neighbor 2 sees me as neighbor 1
		}
		else{
			neighbor2.neighbor3=this;
		}
	}
	public void  print(){//root
		int level=0;
		System.out.println("Taxa id "+id+" name="+name);
		System.out.println("species: "+species);
//		neighbor1.print(this, level+1);
//		neighbor2.print(this, level+1);
		
		
	}
	public void print(Taxa parent, int level){
		for(int i=0;i<level;i++){
			System.out.print("	");
		}
		System.out.println("Taxa id "+id+" name="+name);
		System.out.println("species: "+species);
		neighbor1.print(this, level+1);
		neighbor2.print(this, level+1);
	}
}
