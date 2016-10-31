package samples.bridgemethod;

public class SubClass extends SuperClass<SomeInterface>{

    public String sayYes(SomeInterface s) {
        return "Yes, " + s;
    }

    @Override
    public Integer calculate(Integer i) {
        return i + 10;
    }
}
