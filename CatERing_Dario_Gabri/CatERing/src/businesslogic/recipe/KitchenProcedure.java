package businesslogic.recipe;

public abstract class KitchenProcedure {
    protected int id;
    protected String name;

    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String toString() {
        return name;
    }
}
