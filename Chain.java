public class Chain {
    double value;
    char operator;
    boolean iSValue = false, isOperator = false;
    Chain(double value){
        this.value = value;
        iSValue = true;
    }
    Chain(char operator){
        this.operator = operator;
        isOperator = true;
    }

}
