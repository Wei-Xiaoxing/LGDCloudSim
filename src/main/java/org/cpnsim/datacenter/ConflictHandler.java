package org.cpnsim.datacenter;

import org.cpnsim.core.DatacenterEntity;
import org.cpnsim.intrascheduler.IntraSchedulerResult;
import org.cpnsim.request.Instance;
import org.cpnsim.request.InstanceGroup;

import java.util.List;
import java.util.Map;

/**
 * An interface to be implemented by each class that represents a resource allocator.
 * When multiple {@link Instance}s are assigned to the same host at the same time,
 * it is necessary to select which {@link Instance}s are placed on the host
 * and which are not placed on the host through it.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
public interface ConflictHandler extends DatacenterEntity {
    /**
     * Select the {@link Instance}s to be placed on the host.
     * It is processing the scheduling results of intra-scheduler.
     *
     * @param intraSchedulerResults the intra-schedule results.
     * @return the result of the resource allocation.
     */
    ConflictHandlerResult filterConflictedInstance(List<IntraSchedulerResult> intraSchedulerResults);

    /**
     * Get the number of conflicts when resource allocating of each partition.
     */
    Map<Integer, Integer> getPartitionConflicts();

    /**
     * Select the {@link InstanceGroup}s to be placed on the host.
     * It is processing the scheduling results of inter-scheduler.
     *
     * @param instanceGroups the instance groups which the instance of them are to be placed on the host.
     * @return the result of the resource allocation.
     */
    FailedOutdatedResult<InstanceGroup> filterConflictedInstanceGroup(List<InstanceGroup> instanceGroups);
}
