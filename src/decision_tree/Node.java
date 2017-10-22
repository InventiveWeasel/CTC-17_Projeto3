package decision_tree;

import java.util.ArrayList;

public class Node {
	final static int N_ATTR = 5;
	final static int N_ATTR_WITHOUT_TARGET = 4;
	final static int GENDER = 0;
	final static int AGE = 1;
	final static int OCCUPATION = 2;
	final static int GENRE = 3;
	final static int STARS = 4;
	
	public static int noAttrCounter = 0, noExamCounter = 0, sameClassCounter = 0;
	
	private int nodeAttr;
	private ArrayList<Node> children; 
	private int value;
	private ArrayList<int[]> examples;
	ArrayList<ArrayList<Integer>> attributes;
	private boolean[] attrMark;
	private double[] bestAttr;
	private int[] starCounter;
	private boolean isTerminal = false;
	private int returnValue;
	
	public Node(boolean[] attrMark, double[] bestAttr){
		children = new ArrayList<Node>();
		value = -1;
		this.attrMark = attrMark;
		this.bestAttr = bestAttr;
	}
	
	public void setAttr(int attr){
		this.nodeAttr = attr;
		this.attrMark[attr] = true;
	}
	
	public boolean hasAttr(){
		for(int i = 0; i < attrMark.length; i++){
			if(attrMark[i] == false)
				return true;
		}
		return false;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public int build(ArrayList<int[]> examples, ArrayList<ArrayList<Integer>> attributes, int standardValue){
		this.examples = examples;
		this.attributes = attributes;
		//Retorna valor padrao (de cima)
		if(examples.size() == 0){
			System.out.println("Conjunto vazio de exemplos");
			isTerminal = true;
			noExamCounter++;
			returnValue = standardValue;
			return standardValue;
		}
			
		//Retorna valor da classificacao
		int classification = hasSameClassification();
		if(classification != -1){
			System.out.println("Conjunto com mesma classificacao");
			isTerminal = true;
			sameClassCounter++;
			returnValue = classification;
			return classification;
		}
		//Na falta de atributos
		if(!hasAttr()){
			System.out.println("Sem atributos");
			isTerminal = true;
			noAttrCounter++;
			returnValue = getMaxTargetValue(starCounter);
			return returnValue;
		}
			
		this.examples = examples;
		this.attributes = attributes;
		ArrayList<ArrayList<int[]>> childrenSplits = new ArrayList<ArrayList<int[]>>();
		
		for(int i = 0; i < attributes.get(nodeAttr).size(); i++){
			childrenSplits.add(new ArrayList<int[]>());
		}
		
		//Construindo os splits
		for(int i = 0; i < examples.size(); i++){
			int[] example = examples.get(i);
			childrenSplits.get(example[nodeAttr]).add(example);
		}
		
		int best = chooseBestAttr();
		//attributes.remove(best);
		for(int i = 0; i < attributes.get(nodeAttr).size(); i++){
			boolean[] mark = new boolean[N_ATTR-1];
			double[] bestAtt = new double[N_ATTR-1];
			System.arraycopy( attrMark, 0, mark, 0, attrMark.length );
			System.arraycopy( bestAttr, 0, bestAtt, 0, bestAttr.length );
			Node child = new Node(mark, bestAtt);
			int standard = getMaxTargetValue(starCounter);
			child.setAttr(best);
			child.setValue(i);
			ArrayList<int[]> childrenSplit = childrenSplits.get(i);
			children.add(child);
			children.get(i).build(childrenSplit, attributes,standard);
		}
		return 10;
	}
	
	public int evaluate(int[] example){
		int ret = -10;
		if(isTerminal){
			return returnValue;
		}
		int exampleVal = example[nodeAttr];
		for(int i = 0; i < children.size(); i++){
			Node child = children.get(i);
			if(child.getValue() == exampleVal){
				ret = child.evaluate(example);
				break;
			}
		}
		return ret;
		
	}
	
	
	
	private int chooseBestAttr(){
		double bestGain = 0;
		int bestA = 0;
		for(int attr = GENDER; attr <= GENRE; attr++){
			if(bestAttr[attr] > bestGain && !attrMark[attr]){
				bestA = attr;
				bestGain = bestAttr[attr];
			}
		}
		attrMark[bestA] = true;
		return bestA;
	}
	
	
	private int[] accountExamples(){
		int[] counter = new int[] {0,0,0,0,0};
		
		//Percorrendo os exemplos
		int[] example;
		int aux;
		for(int i = 0; i < examples.size(); i++){
			example = examples.get(i);
			//Avaliacao
			counter[example[STARS]]++;
		}
		return counter;
	}
	
	private int hasSameClassification(){
		starCounter = accountExamples();
		int n = 0, pos=-1;
		for(int i = 0; i < starCounter.length; i++){
			if(starCounter[i] != 0){
				n++;
				pos = i;
			}
		}
		if(n == 1)
			return pos;
		else
			return -1;
	}
	
	private int getMaxTargetValue(int[] counter){
		int max = 0;
		int maxIndex = -1;
		for(int i = 0; i < 5; i++){
			if(counter[i] > max){
				max = counter[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
