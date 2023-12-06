import jdk.swing.interop.SwingInterOpUtils;

import java.io.DataOutput;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatCodePointException;
import java.util.Scanner;
import java.lang.*;
public class FunctionApproximation {
    public static double[][] IO;
    static int numOfIO;
    static int numOfFirstPopulation;
    static double probabilityOfMutation;
    static int numOfFitnesCounter = 0;
   // static ArrayList<Node> parents = new ArrayList<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of first population : ");
        numOfFirstPopulation = scanner.nextInt();
        System.out.println("Enter Mutation probability : ");
        probabilityOfMutation = scanner.nextDouble();
        System.out.println("Enter num of inputs : ");
        numOfIO = scanner.nextInt();
        System.out.println("Please enter the inputs and results in this format \" input result\" : ");
        IO = new double[2][numOfIO];
        IOhandler(scanner, numOfIO);
        //begin Time
        LocalTime startTime =java.time.LocalTime.now();
        ArrayList<Node> firstPopulation = firstPopulationGenerator();
        ArrayList<Node> parents = new ArrayList<>();
        int i = 0;
        for (Node n : firstPopulation) {
            Double d = n.fitness;
            if (!d.isNaN()) {
                parents.add(n);
            }
        }
        boolean flag = true;
        int numOfGenerate = 0;
        double max = 0;
        Node maxFitness = new Node('s');
        while(max < 100 && numOfGenerate < 10){
            ArrayList<Node> newParents = new ArrayList<>();
            for (int counter = 0; counter < parents.size(); counter++){
                inorder(parents.get(counter));
                fitnessCalculate(parents.get(counter), IO);
                Double d = parents.get(counter).fitness;
                if (!d.isNaN()) {
                    newParents.add(parents.get(counter));
                }
            }
            Collections.sort(newParents);

            if (max < newParents.get(newParents.size() -1).fitness){
                //maxFitness = newParents.get(newParents.size() -1);
                System.out.println( "  fitness " + newParents.get(newParents.size() -1).fitness);
                max = newParents.get(newParents.size() -1).fitness;
                ArrayList<Chain> ch = new ArrayList<>();
                inorder2(newParents.get(newParents.size() -1), ch);
                printer(ch);
                System.out.println("");
            }
            parents = crossOver(newParents);
            double mutationProbability = Math.random();
            if (mutationProbability < probabilityOfMutation){
                //System.out.println("mutation occur!!!");
                parents  = mutation(newParents);
            }
            //System.out.println(numOfGenerate);
            numOfGenerate += 1;


        }
        System.out.println("");
        System.out.println( " number of generation  "+ numOfGenerate + "  max fitness " + max);
        LocalTime endTime = java.time.LocalTime.now();
        int diff = endTime.getNano() - startTime.getNano();
        System.out.println("number of fitness counting : " + numOfFitnesCounter);
        System.out.println("the execution time is : " + diff + " ns");
    }

    private static ArrayList<Node> mutation(ArrayList<Node> newParents) {
        for (int counter = 0; counter < newParents.size(); counter +=100){
                Node temp = newParents.get(counter);
                boolean flag = true;
                while (flag){
                    temp = temp.rightChild;
                    if ( temp.rightChild.operator != 's' || temp.rightChild.operator != 'c'){
                        flag = false;
                    }

                }
                if (!flag){
                    Node root = new Node(specificOperatorGenerator());
                    Node rootl = new Node(randomGenerator(-5, 5));
                    Node rootr = new Node('x');
                    root.leftChild = rootl;
                    root.rightChild = rootr;
                    temp.rightChild = root;
                }

        }
        for (int counter = 1; counter < newParents.size(); counter += 100){
            Node temp = newParents.get(counter);
            boolean flag = true;
            while (flag){
                temp = temp.rightChild;
                if ( temp.rightChild.rightChild == null){
                    flag = false;
                }

            }
            if (!flag){
                Node root = new Node(specificOperatorGenerator());
                Node rootl = new Node(randomGenerator(-5, 5));
                Node rootr = new Node('x');
                root.leftChild = rootl;
                root.rightChild = rootr;
                temp.rightChild = root;

            }
        }
        return newParents;
    }

    private static ArrayList<Node> crossOver(ArrayList<Node> parents) {
        double fatherProbaility = randomGenerator(0.5, 1);
        double motherProbability = randomGenerator(0.5, 1);
        double sumOfFitness = 0;
        for (int counter = 0; counter < parents.size(); counter++) {
            sumOfFitness += parents.get(counter).fitness;
        }
        for (int counter = 0; counter < parents.size(); counter++) {
            parents.get(counter).probability = parents.get(counter).fitness / sumOfFitness;
        }
        for (int counter1 = 0; counter1 < parents.size()/2; counter1++){
            double sumOfProbability = 0;
            int fatherIndex = 0, motherIndex = 0;
            boolean fatherFlag = true, motherFlag = true;
            for (int counter = 0; counter < parents.size(); counter++) {
                sumOfProbability += parents.get(counter).probability;
                if (fatherProbaility <= sumOfProbability && fatherFlag) {
                    fatherFlag = false;
                    fatherIndex = counter;
                }
                if (motherProbability <= sumOfProbability && motherFlag) {
                    motherFlag = false;
                    motherIndex = counter;
                }
                if (!motherFlag && !fatherFlag) {
                    break;
                }
            }
            Node father = parents.get(fatherIndex);
            Node mother = parents.get(motherIndex);
            if (father.leftChild.leftChild.isValue && mother.leftChild.leftChild.isValue){
                father.leftChild.leftChild.value = mother.leftChild.leftChild.value = ((father.leftChild.leftChild.value + mother.leftChild.leftChild.value) / 2);
            }
            if (father.leftChild.rightChild.rightChild.isValue && mother.leftChild.rightChild.rightChild.isValue){
                father.leftChild.rightChild.rightChild.value = mother.leftChild.rightChild.rightChild.value = ((mother.leftChild.rightChild.rightChild.value + father.leftChild.rightChild.rightChild.value)/2);
            }
            if(father.rightChild.leftChild.rightChild.isValue && mother.rightChild.leftChild.rightChild.isValue){
                father.rightChild.leftChild.rightChild.value= mother.rightChild.leftChild.rightChild.value=((father.rightChild.leftChild.rightChild.value+mother.rightChild.leftChild.rightChild.value)/2);
            }

            while (father.rightChild != null || father.leftChild != null || mother.rightChild != null || mother.rightChild != null) {
//                if (father.isValue && mother.isValue) {
//                    System.out.println("chang value in crossOver !!!");
//                    father.value = mother.value = ((father.value + mother.value) / 2);
//                } else
                  if (father.isOperator && mother.isOperator) {
                   // System.out.println("chang operator in crossOver !!!");
                    char c = father.operator;
                    father.operator = mother.operator;
                    mother.operator = c;
                }
                if (father.rightChild != null) {
                    father = father.rightChild;
                } else if (father.leftChild != null) {
                    father = father.leftChild;
                }
                if (mother.rightChild != null) {
                    mother = mother.rightChild;
                } else if (mother.leftChild != null) {
                    mother = mother.leftChild;
                }
            }

    }
        return parents;
    }

    private static boolean maxFitness() {
        return true;
    }

    // this method is for getting x, y
    public static void IOhandler( Scanner scanner, int numOfIO){
        for (int counter = 0; counter < numOfIO; counter++){
            IO[0][counter] = scanner.nextDouble();
            IO[1][counter] = scanner.nextDouble();
        }
    }

    public static ArrayList<Node> firstPopulationGenerator(){
        ArrayList<Node> nodes = new ArrayList<>();
        for (int counter = 0; counter < numOfFirstPopulation; counter++){
            Node root = new Node(specificOperatorGenerator());
            Node rootl = new Node(specificOperatorGenerator());
            Node rootll = new Node(randomGenerator(-30, 30));
            Node rootlr = new Node(specificOperatorGenerator());
            Node rootlrl = new Node('x');
            Node rootlrr = new Node(randomGenerator(-5, 5));
            Node rootr = new Node(specificOperatorGenerator());
            Node rootrl = new Node(specificOperatorGenerator());
            Node rootrll = new Node('x');
            Node rootrlr = new Node(randomGenerator(-30, 30));
            Node rootrr = new Node(specificOperatorGenerator());
            Node rootrrl = new Node(randomGenerator(-30, 30));
            Node rootrrr = new Node(operatorGenerator());
            Node rootrrrr = new Node('x');
            root.leftChild = rootl;
            rootl.leftChild = rootll;
            rootl.rightChild = rootlr;
            rootlr.leftChild = rootlrl;
            rootlr.rightChild = rootlrr;
            root.rightChild = rootr;
            rootr.leftChild = rootrl;
            rootrl.leftChild = rootrll;
            rootrl.rightChild = rootrlr;
            rootr.rightChild = rootrr;
            rootrr.leftChild = rootrrl;
            rootrr.rightChild = rootrrr;
            rootrrr.rightChild = rootrrrr;
            fitnessCalculate(root, IO);
                nodes.add(root);

        }
        return nodes;
    }
    public static void fitnessCalculate(Node root, double[][] IO){
        numOfFitnesCounter += 1;
            int lenght = numOfIO;
            double errors = 0;
            for (int counter = 0 ; counter < numOfIO; counter++){
                inorder(root);
                errors += Math.abs(IO[1][counter] - converter(chains, IO[0][counter]));
            }
            root.fitness = (1/(errors/lenght));
    }

    public static char operatorGenerator(){
        double random = Math.random();
        char[] specificOperator = { 's', 'c'};
        int numOfOperator = specificOperator.length;
        int index = 0;
        if (random <= 0.5)
            return specificOperator[0];
        return specificOperator[1];

    }
    public static double randomGenerator(double a, double b){
        double random = Math.random();
        return (a + (b-a)*random);
    }
    public static char specificOperatorGenerator(){
        double random = Math.random();
        char[] specificOperator = {'-', '+', '^', '*', '/'};
        int numOfOperator = specificOperator.length;
        int index = 0;
        for (int counter = 0; counter < numOfOperator; counter++){
            if((counter * 0.2)<= random && ((counter + 1) *0.2) > random){
                index = counter;
                break;
            }
        }
        return specificOperator[index];
    }

    static ArrayList<Chain> chains = new ArrayList<>();
    public static void inorder(Node root){

        if (root.leftChild != null)
            inorder(root.leftChild);
        if (root.isValue)
            //System.out.print(root.value);
            chains.add(new Chain(root.value));
        else
            //System.out.print(root.operator);
            chains.add(new Chain(root.operator));
        if (root.rightChild != null)
            inorder(root.rightChild);
    }
//    static ArrayList<Chain> chains2 = new ArrayList<>();
    public static void inorder2(Node root, ArrayList<Chain> ch){
        if (root.leftChild != null)
            inorder2(root.leftChild, ch);
        if (root.isValue)
           // System.out.print(root.value);
            ch.add(new Chain(root.value));
        else if (root.isOperator)
            //System.out.print(root.operator);
            ch.add(new Chain(root.operator));
        if (root.rightChild != null)
            inorder2(root.rightChild, ch);
    }
    public static void printer(ArrayList<Chain> chains){
        for (int counter = 0; counter < chains.size(); counter++){
            if(chains.get(counter).iSValue){
                System.out.print(chains.get(counter).value);
            }
            else if (chains.get(counter).isOperator){
                System.out.print(chains.get(counter).operator);
            }
        }
        System.out.println("");
    }
    public static double converter(ArrayList<Chain> chains , double x){

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == 'x'){
                    chains.get(counter).value = x;
                    chains.get(counter).iSValue = true;
                    chains.get(counter).isOperator = false;
                }
            }
        }

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == 's'){
                    chains.get(counter).value = Math.sin(chains.get(counter + 1).value);
                    chains.get(counter).iSValue = true;
                    chains.get(counter).isOperator = false;
                    chains.remove(counter + 1);
                   // counter -= 1;
                }
            }
        }

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == 'c'){
                    chains.get(counter).value = Math.cos(chains.get(counter + 1).value);
                    chains.get(counter).iSValue = true;
                    chains.get(counter).isOperator = false;
                    chains.remove(counter + 1);
                    //counter -= 1;
                }
            }
        }

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == '^'){
                    chains.get(counter-1).value = Math.pow(chains.get(counter-1).value, chains.get(counter+1).value);
                    chains.get(counter-1).iSValue = true;
                    chains.get(counter-1).isOperator = false;
                    chains.remove(counter + 1);
                    chains.remove(counter);
                     counter -= 1;
                }
            }
        }

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == '*' || chains.get(counter).operator == '/'){
                    if (chains.get(counter).operator == '*'){
                        chains.get(counter-1).value = chains.get(counter-1).value * chains.get(counter+1).value;
                        chains.get(counter-1).iSValue = true;
                        chains.get(counter-1).isOperator = false;
                        chains.remove(counter + 1);
                        chains.remove(counter);
                        counter -= 1;
                    }
                    else if (chains.get(counter).operator == '/'){
                        chains.get(counter-1).value = chains.get(counter-1).value / chains.get(counter+1).value;
                        chains.get(counter-1).iSValue = true;
                        chains.get(counter-1).isOperator = false;
                        chains.remove(counter + 1);
                        chains.remove(counter);
                        counter -= 1;
                    }
                }
            }
        }

        for (int counter = 0; counter < chains.size(); counter++){
            if (chains.get(counter).isOperator){
                if (chains.get(counter).operator == '+' || chains.get(counter).operator == '-'){
                    if (chains.get(counter).operator == '+'){
                        chains.get(counter-1).value = chains.get(counter-1).value + chains.get(counter+1).value;
                        chains.get(counter-1).iSValue = true;
                        chains.get(counter-1).isOperator = false;
                        chains.remove(counter + 1);
                        chains.remove(counter);
                        counter -= 1;
                    }
                    else if (chains.get(counter).operator == '-'){
                        chains.get(counter-1).value = chains.get(counter-1).value - chains.get(counter+1).value;
                        chains.get(counter-1).iSValue = true;
                        chains.get(counter-1).isOperator = false;
                        chains.remove(counter + 1);
                        chains.remove(counter);
                        counter -= 1;
                    }
                }
            }
        }

        double result = chains.get(0).value;
        chains.remove(0);
        return result;


    }

}
