package org.cpnsim.datacenter;

import lombok.Getter;
import lombok.Setter;
import org.cpnsim.intrascheduler.IntraScheduler;
import org.cpnsim.request.Instance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to represent a load balancer.
 * This load balancer performs load balancing through cyclic allocation
 * This class implements the interface {@link LoadBalance}.
 *
 * @author Jiawen Liu
 * @since CPNSim 1.0
 */
public class LoadBalanceBatch implements LoadBalance {
    /**
     * the data center that the load balancer belongs to.
     **/
    @Getter
    Datacenter datacenter;

    /**
     * the load balance cost time.
     **/
    @Getter
    @Setter
    double loadBalanceCostTime = 0.1;

    /**
     * the last intra-scheduler id.
     **/
    int lastInnerSchedulerId = 0;

    /**
     * Overrides the method to send instances to intra schedulers.
     * This method distributes instances to each intra-scheduler in batches according to the batch size
     * until all instances have been issued.
     *
     * @param instances List of instances to be sent to intra-schedulers.
     * @return Set of intra schedulers to which instances were sent.
     */
    @Override
    public Set<IntraScheduler> sendInstances(List<Instance> instances) {
        Set<IntraScheduler> sentIntraSchedulers = new HashSet<>();
        int batchSize = 100;
        int size = instances.size();
        int startIndex = 0;
        int endIndex = 0;
        while (endIndex < size) {
            endIndex = Math.min(startIndex + batchSize, size);
            List<Instance> batchInstances = instances.subList(startIndex, endIndex);
            IntraScheduler intraScheduler = datacenter.getIntraSchedulers().get(lastInnerSchedulerId);
            lastInnerSchedulerId = (lastInnerSchedulerId + 1) % datacenter.getIntraSchedulers().size();
            intraScheduler.addInstance(batchInstances, false);
            sentIntraSchedulers.add(intraScheduler);
            startIndex = endIndex;
        }

        LOGGER.info("{}: {}'s LoadBalanceRound send {} instances to {} intraScheduler,On average, each scheduler receives around {} instances",
                datacenter.getSimulation().clockStr(), datacenter.getName(), instances.size(),
                sentIntraSchedulers.size(), instances.size() / sentIntraSchedulers.size());
        return sentIntraSchedulers;
    }

    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
