package samples.bridgemethod;

public class SuperClass<T> {
    public String sayYes(T o){
        return "Yes!";
    }

    public Number calculate(Integer i){
        return i + 10;
    }
}
