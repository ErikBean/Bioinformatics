import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
/**I designed this to take a command line argument for n, when choosing the n most informative genes.
 * But It defaults to 50;
 * @author legumebo
 *
 */
class Gene implements Comparable<Gene>{
	double weight;
	double voteScore;
	double allMean;
	double amlMean;
	double allDev;
	double amlDev;
	String name;
	int index;
	public Gene(int index, String name){
		this.index=index;
		this.name=name;
		allMean=0.0;
		amlMean=0.0;
	}
	@Override
	public int compareTo(Gene that) {
		if(that.weight>this.weight)return -1;
		else if(this.weight>that.weight)return 1;
		return 0;
	}

	public void setVotingScore(double exp){
		
	}
	public void calcAndSetWeight() {//no absolute value, cna be + or -
		double meanDiff=(amlMean-allMean);
		weight=meanDiff/(allDev+amlDev);
	}
	
}

class Leukemia {
	/**gene names*/
	static String[] geneStrings;
	static Gene[] totalGenes;
	static Gene[] choiceGenes;
	/**array of patients. Each inner array starts with a 0.0(all) or a 1.0(aml)*/
	static double[][] data;
	static double[][] testData;
	/**the difference in means over the sum of standard deviations.
	 * For the weighted vote**/
//	static double[][] weights;
	static double[] expLvls;
//	static double ALLmeans[];
//	static double AMLmeans[];
//	static double ALLdevs[];
//	static double AMLdevs[];
	/**these are just counters for means and std devs.
	 * Total # of ALL and AML patients, e.g. 11 and 27**/
	static int totalAllPatients=0;
	static int totalAmlPatients=0;
	static double allVote=0;
	static double amlVote=0;
	
	public static void main(String[] args) {
		if(args.length<2){
			System.out.println("Please specify train and test file arguments");
			System.exit(1);
		}
		else{
			readFile(args[0]);
		}
		int threshold=50;
		if(args.length==3){
			threshold=Integer.parseInt(args[2]);
			if(threshold%2!=0){
				System.out.println("please enter an even number, for equally sized all and aml groups");
				System.exit(1);
			}
		}
		chooseGenes(threshold);

		readTestFile(args[1]);
		double p=0.0;
		double wrong=0.0;
		double allDiff=0.0;
		double amlDiff=0.0;
		for(int i=0;i<testData.length;i++){
//			expLvls=votingScores(i);
//			for(int j=0;j<choiceGenes.length;j++){
//				allDiff=Math.abs(expLvls[j]-ALLmeans[(int) weights[j][1]]);
//				amlDiff=Math.abs(expLvls[j]-AMLmeans[(int) weights[j][1]]);
//				if(allDiff>amlDiff){
//					amlVote+=weights[j][0]*weights[j][2];
//				}
//				else{
//					allVote+=weights[j][0]*weights[j][2];
//				}
//			}
			p=Math.abs(allVote-amlVote);
			p/=(amlVote+allVote);
			//System.out.println("allVote: "+allVote+" amlVote: "+amlVote);
			if(amlVote>allVote && p>0.3){
				System.out.println("Diagnosis for patient "+i+" is AML");
				if(testData[i][0]!=1.0){
					System.out.println("(WRONG)");
					wrong+=1.0;
				}
			}
			else if(p>0.3){
				System.out.println("Diagnosis for patient "+i+" is ALL");
				if(testData[i][0]!=0.0){
					System.out.println("(WRONG)");
					wrong+=1.0;
				}
			}
			else{
				System.out.println("patient "+i+" is No-Call. Confidence interval to low.");
			}
			System.out.println("Prediction strength for patient "+i+" = "+p);
		}
		System.out.println("The prediction accuracy rate was "+(wrong/testData.length*100)+"%");
		
		System.exit(0);

	}
	/**choose n (from command line arg) most informative genes.
	 * based on best weight scores. Also fills Nx3 weights array**/
	static void chooseGenes(int threshold){

//		weights=new double[threshold][3];
//		//each index is for its own gene
//		ALLmeans=new double[geneStrings.length-1];
//		ALLdevs=new double[geneStrings.length-1];
//		AMLmeans=new double[geneStrings.length-1];
//		AMLdevs=new double[geneStrings.length-1];

		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length-1;j++){//-1 because starts w/ AML/ALL
				//j+1 because have to ignore initial 0.0 or 1.0 indicator for AML/ALL
				if(data[i][0]==0.0){//ALL
					totalGenes[j].allMean+=(data[i][j+1]/totalAllPatients);
				}
				else if(data[i][0]==1.0){//AML
					totalGenes[j].amlMean+=(data[i][j+1]/totalAmlPatients);
				}
				else{
					System.out.println("Critical Fuckup!!!");
					System.exit(1);
				}
			}
		}
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length-1;j++){//-1 bc initial AML/ALL 1.0/0.0
				if(data[i][0]==0.0){
					//squared diff from mean divided by number of ALL patients
					totalGenes[j].allDev+=Math.pow((totalGenes[j].allMean-data[i][j+1]),2.0)/totalAllPatients;
				}
				else if(data[i][0]==1.0){//AML
					totalGenes[j].amlDev+=Math.pow((totalGenes[j].amlMean-data[i][j+1]),2.0)/totalAmlPatients;
					//if(j==0)System.out.println((AMLmeans[j])+"-"+(data[i][j+1])+"!!");

				}
			}
		}
		//System.out.println(Arrays.toString(AMLdevs));
		for(int i=0;i<totalGenes.length;i++){
			totalGenes[i].allDev=Math.sqrt(totalGenes[i].allDev);
			totalGenes[i].amlDev=Math.sqrt(totalGenes[i].amlDev);
			totalGenes[i].calcAndSetWeight();
		}
		Arrays.sort(totalGenes);
		choiceGenes=new Gene[threshold];
		int halfSet=threshold/2;
		for (int i = 0; i < halfSet; i++) {
			//this should fill from both ends at once
			choiceGenes[i]=totalGenes[i];
			choiceGenes[choiceGenes.length-i-1]=totalGenes[totalGenes.length-1-i];
		}
		for (int i = 0; i < choiceGenes.length; i++) {
			System.out.println(choiceGenes[i].name);
		}
		//split int two groups, all and aml, before sorting
		
//		System.out.println("ALL means: "+Arrays.toString(ALLmeans)+"\nAML means: "
//		+Arrays.toString(AMLmeans)+"\nALL std devs: "+Arrays.toString(ALLdevs)+
//		"\nAML std devs: "+Arrays.toString(AMLdevs));
//		double meanDiff;
//		double weightsToSort[][]=new double[data[0].length-1][2];
//		for(int j=0;j<data[0].length-1;j++){
//			meanDiff=Math.abs(ALLmeans[j]-AMLmeans[j]);
//			weightsToSort[j][0]=meanDiff/(ALLdevs[j]+AMLdevs[j]);
//			weightsToSort[j][1]=j;//need to keep track of the index for later
//
//		}
//		//System.out.println("temp:"+Arrays.deepToString(temp));
//		Arrays.sort(weightsToSort, new Comparator<double[]>(){
//			public int compare(double[]a,double[]b){
//				if (a[0]>b[0]){
//					return 1;
//				}
//				else return -1;
//			}
//		});
//		//
//		for(int i=0;i<weights.length;i++){
//			if(i==weightsToSort.length)break;//for tiny test files
//			weights[i][0]=weightsToSort[weightsToSort.length-i-1][0];
//			weights[i][1]=weightsToSort[weightsToSort.length-i-1][1];
//			
//		}
//		//System.out.println("weights: "+Arrays.deepToString(weights));
//		System.out.println("Selected the following as the "+threshold+" most informative genes:");
//		String inf[]=new String[weights.length];//informative genes, sorted alpha
//		for (int i = 0; i < weights.length; i++) {
//			inf[i]=geneStrings[(int) weights[i][1]];
//		}
//		Arrays.sort(inf);
//		for (int i = 0; i < weights.length; i++) {
//			System.out.println(inf[i]);
//		}
	}
	/**calculates the voting scores, puts them in weights[n][2]
	 * and returns an array of the n informative gene expression levels for the patient
	 */
//	static double [] votingScores(int d){
//		double xi;
//		double meanSum;
//		double exp[]=new double[weights.length];//expression levels
//		for(int i=0;i<weights.length;i++){
//			meanSum=AMLmeans[(int)weights[i][1]]+ALLmeans[(int)weights[i][1]];
//			xi=testData[d][(int)weights[i][1]];
//			weights[i][2]=Math.abs(xi-(0.5*(meanSum)));//voting score
//			exp[i]=xi;
//		}
//		return exp;
//	}
	/**reads the test file into the array testData**/
	
	static void readTestFile(String fname){
		Scanner inFile;
		File readIn=new File(fname);
		try {
			inFile = new Scanner(readIn);
			int i=0;
			String[]temp;
			int count=0;
			while(inFile.hasNextLine()){
				inFile.nextLine();
				count++;
			}
			testData=new double[count-1][totalGenes.length];
			inFile.close();
			inFile = new Scanner(readIn);
			inFile.nextLine();
			while(inFile.hasNextLine()){
				temp=inFile.nextLine().split("	");//before converting to doubles
				
				if(temp[1].equals("ALL")){
					testData[i][0]=0.0;

				}
				else if(temp[1].equals("AML")){
					testData[i][0]=1.0;
				}
				for(int j=1;j<temp.length-1;j++){
//					System.out.println(Arrays.toString(temp)+temp.length);
					testData[i][j]=Double.parseDouble(temp[j+1]);
				}
				//System.out.println(data[i]);
				if(!inFile.hasNext()||!inFile.hasNextLine()){break;}
				i++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("E:File not found.");
		}
		//System.out.println("test last:"+Arrays.toString(testData[testData.length-1]));
		//System.out.println(Arrays.deepToString(data));
		return;

	}
	/**puts file data into fields**/
	static void readFile(String fname){
		Scanner inFile;
		File readIn=new File(fname);
		try {
			inFile = new Scanner(readIn);
			String names[]=inFile.nextLine().split("	");
			totalGenes=new Gene[names.length-2];//ignore "patient" and "type"
			for (int i = 0; i < totalGenes.length; i++) {
				totalGenes[i] = new Gene(i,names[i+2]);
			}
//			System.out.println(genes[0].index+" nad "+genes[genes.length-1].index);
			int i=0;
			String[]temp;
			int count=0;
			while(inFile.hasNextLine()){
				inFile.nextLine();
				count++;
			}
			data=new double[count][totalGenes.length+1];//+1 because also need an AML/ALL @start
			System.out.println("DATA: "+data.length+" by "+data[0].length);
			inFile.close();
			inFile = new Scanner(readIn);
			inFile.nextLine();
			totalAllPatients=0;
			totalAmlPatients=0;
			while(inFile.hasNextLine()){
				temp=inFile.nextLine().split("	");//before converting to doubles				
				if(i==0){
					//System.out.println(Arrays.toString(temp));
				}
				if(temp[1].equals("ALL")){
					data[i][0]=0.0;
					totalAllPatients++;
				}
				else if(temp[1].equals("AML")){
					data[i][0]=1.0;
					totalAmlPatients++;
				}
				for(int j=0;j<temp.length-2;j++){//-2 because temp has line number and AML/ALL
	
					data[i][j+1]=Double.parseDouble(temp[j+2]);//data[i][0]=AML/ALL
				}
				//System.out.println(data[i]);
				if(!inFile.hasNext()||!inFile.hasNextLine()){break;}
				i++;
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.err.println("E:File not found.");
		}
		return;

	}	

}
