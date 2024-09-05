package transportation.provider.repository;

import transportation.provider.model.ContainerGood;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

//remove business logic to service and minimize the getters setters
@Repository
public class ContainerRepo {

    @Getter
    private final int grossLoadsLimit;
    private final Stack<ContainerGood> containerGoods;//main container stack LIFO
    private final Stack<ContainerGood> highestPriceLoads;//duplicated container stack LIFO
    private final AtomicInteger currentWeight = new AtomicInteger();

    public ContainerRepo(@Value("${container.gross.loads.limit}") int grossLoadsLimit) {
        this.containerGoods = new Stack<>();
        this.highestPriceLoads = new Stack<>();
        this.currentWeight.set(0);
        this.grossLoadsLimit = grossLoadsLimit;
    }

    public void pushGood(ContainerGood containerGood) {
        containerGoods.push(containerGood);
    }


    public void pushMaxPrice(ContainerGood containerGood) {
        highestPriceLoads.push(containerGood);
    }


    public void getPopMaxPrice() {
        highestPriceLoads.pop();
    }

    public ContainerGood popContainerGoods() {
        return containerGoods.pop();
    }

    public Integer getCurrentWeight() {
        return currentWeight.get();
    }


    public boolean isEmptyContainerGoods() {
        return containerGoods.isEmpty();
    }

    public ContainerGood getMaxPriceLoadsPeek() {
        return highestPriceLoads.peek();
    }


    public void updateWeight(int newWeight) {
        currentWeight.set(newWeight);
    }

    public boolean isHighestPriceLoadEmpty() {
        return highestPriceLoads.isEmpty();
    }
}
