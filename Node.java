import java.util.Comparator;

public class Node implements Comparable<Node>  {
    public double value;
    public char operator;
    Node rightChild = null;
    Node leftChild = null;
    boolean isOperator = false;
    boolean isValue = false;
    public Node(double value){
        this.value = value;
        isValue = true;
        isOperator = false;
    }
    public Node(char operator){
        this.operator = operator;
        isValue = false;
        isOperator = true;
    }
    // THIS ATTRIBUTE IS FOR CALCULATING ERROR
    public double fitness = 0;
    double getFitness(){
        return this.fitness;
    }
    public boolean mark = false;
    public double probability = 0;
    @Override
    public int compareTo(Node o) {
        int xcomp = Double.compare(this.fitness, o.fitness);

        return xcomp ;

    }
}
