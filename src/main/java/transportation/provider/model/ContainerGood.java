package transportation.provider.model;

public record ContainerGood(int id, int weight, double price) {

    @Override
    public String toString() {
        return "Good [id=" + id + ",weight=" + weight + ", price=" + price + "]";
    }
}
