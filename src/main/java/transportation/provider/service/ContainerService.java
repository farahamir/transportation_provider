package transportation.provider.service;

import transportation.provider.model.ContainerGood;
import transportation.provider.repository.ContainerRepo;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {

    ContainerRepo containerRepo;

    public ContainerService(ContainerRepo containerRepo) {
        this.containerRepo = containerRepo;
    }

    /**
     * loading Good to the container when there is an available place and update the total weight
     *
     * @param containerGood to load
     * @return true when successfully loaded and false when there is no place to load
     */
    public synchronized boolean loadGood(ContainerGood containerGood) {
        if (isPlaceAvailable(containerGood)) {
            containerRepo.pushGood(containerGood);
            pushGoodToSecondaryContainer(containerGood);

            containerRepo.updateWeight(containerRepo.getCurrentWeight() + containerGood.weight());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add Good to the secondary container after comparing the price.
     *
     * @param containerGood to add
     */
    public void pushGoodToSecondaryContainer(ContainerGood containerGood) {
        if (emptyMaxStackOrGreaterEqualPrice(containerGood)) {
            containerRepo.pushMaxPrice(containerGood);
        } else {
            // Push the current max again to keep the size of maxStack in sync with mainStack
            containerRepo.pushMaxPrice(containerRepo.getMaxPriceLoadsPeek());
        }
    }

    private boolean emptyMaxStackOrGreaterEqualPrice(ContainerGood containerGood) {
        return containerRepo.isHighestPriceLoadEmpty() || containerGood.price() >= containerRepo.getMaxPriceLoadsPeek().price();
    }

    /**
     * unloading Good from the Container
     *
     * @return ContainerGood object
     */
    public synchronized ContainerGood unloadGood() {
        if (!containerRepo.isEmptyContainerGoods()) {
            var unloadedContainerGood = unloadContainerGood();
            containerRepo.updateWeight(containerRepo.getCurrentWeight() - unloadedContainerGood.weight());
            return unloadedContainerGood;
        } else {
            return null;
        }
    }

    /**
     * unloading good from the container and from the secondary container
     *
     * @return last good
     */
    private ContainerGood unloadContainerGood() {
        ContainerGood unloadedContainerGood = containerRepo.popContainerGoods();
        containerRepo.getPopMaxPrice();
        return unloadedContainerGood;
    }

    /**
     * Checking when is a place available in the container for the Good
     *
     * @param containerGood to check
     * @return true when there is a place and false when not
     */
    private boolean isPlaceAvailable(ContainerGood containerGood) {
        return containerRepo.getCurrentWeight() + containerGood.weight() <= containerRepo.getGrossLoadsLimit();
    }

    public ContainerGood getHighestPricedGood() {
        if (containerRepo.isEmptyContainerGoods()) {
            return null;
        }
        return containerRepo.getMaxPriceLoadsPeek();
    }

    public Integer getTotalWeight() {
        return containerRepo.getCurrentWeight();
    }
}
