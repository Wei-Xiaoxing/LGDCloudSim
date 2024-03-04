package org.cpnsim.datacenter;

import lombok.Getter;
import lombok.Setter;
import org.cpnsim.request.Instance;
import org.cpnsim.intrascheduler.IntraScheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to represent a load balancer.
 * This load balancer performs load balancing through cyclic allocation
 * This class implements the interface {@link LoadBalance}.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
public class LoadBalanceRound implements LoadBalance {
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
     * the last inner scheduler id.
     **/
    int lastInnerSchedulerId = 0;

    /**
     * Overrides the method to send instances to intra schedulers.
     * The method divides all instances into fractions of the number of intra-schedulers,
     * and then distributes them to each intra-scheduler.
     *
     * @param instances List of instances to be sent to intra schedulers.
     * @return Set of intra schedulers to which instances were sent.
     */
    @Override
    public Set<IntraScheduler> sendInstances(List<Instance> instances) {
        Set<IntraScheduler> sentIntraSchedulers = new HashSet<>();
        int size = instances.size();
        List<IntraScheduler> intraSchedulers = datacenter.getIntraSchedulers();
        int onceSendSize = size / intraSchedulers.size();
        int remainder = size % intraSchedulers.size();

        int start = 0;
        int end;
        for (int i = 0; i < intraSchedulers.size(); i++) {
            IntraScheduler intraScheduler = intraSchedulers.get((lastInnerSchedulerId + i) % intraSchedulers.size());
            end = start + onceSendSize + (i < remainder ? 1 : 0);
            if (end == start) {
                break;
            }
            intraScheduler.addInstance(instances.subList(start, end), false);
            sentIntraSchedulers.add(intraScheduler);
            start = end;
        }

        lastInnerSchedulerId = (lastInnerSchedulerId + 1) % datacenter.getIntraSchedulers().size();
        LOGGER.info("{}: {}'s LoadBalanceRound send {} instances to {} intraScheduler,On average, each scheduler receives around {} instances", datacenter.getSimulation().clockStr(), datacenter.getName(), instances.size(), sentIntraSchedulers.size(), onceSendSize);
        return sentIntraSchedulers;
    }

    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
