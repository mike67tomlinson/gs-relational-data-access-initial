package hello;

public class Customer {
    private long id;
    private String make, model;
    private int engine;

    public Customer(long id, String make, String model, int engine) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.engine = engine;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, make='%s', model='%s', engine='%s']",
                id, make, model, engine);
    }

    
}