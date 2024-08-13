package org.lgdcloudsim.interscheduler;

import org.lgdcloudsim.core.Simulation;
import org.lgdcloudsim.datacenter.Datacenter;
import org.lgdcloudsim.network.NetworkTopology;
import org.lgdcloudsim.request.InstanceGroup;
import org.lgdcloudsim.request.InstanceGroupGraph;
import org.lgdcloudsim.request.UserRequest;

import java.util.List;

/**
 * The round-robin inter-scheduler.
 * It is extended from the {@link InterSchedulerSimple}.
 * It changes the scheduleToDatacenter function to implement the round-robin scheduling strategy.
 * The scheduled data center is the next of last scheduled data center.
 *
 * @author Anonymous
 * @since LGDCloudSim 1.0
 */
public class InterSchedulerRound extends InterSchedulerSimple {
    /**
     * The index of the last scheduled data center.
     */
    private int lastSendDCIndex = 0;

    /**
     * The constructor of the round-robin inter-scheduler.
     *
     * @param id               the id of the inter-scheduler
     * @param simulation       the simulation
     * @param collaborationId  the collaboration id
     * @param target           the target of the inter-scheduler
     * @param isSupportForward whether the scheduled instance group results support forward again
     */
    public InterSchedulerRound(int id, Simulation simulation, int collaborationId, int target, boolean isSupportForward) {
        super(id, simulation, collaborationId, target, isSupportForward);
    }

    /**
     * Schedule the instance groups to the data center.
     * It implements the round-robin scheduling strategy.
     * The scheduled data center is the next of last scheduled data center.
     *
     * @param instanceGroups the instance groups to be scheduled
     * @return the result of the scheduling
     */
    @Override
    protected InterSchedulerResult scheduleToDatacenter(List<InstanceGroup> instanceGroups) {
        List<Datacenter> allDatacenters = simulation.getCollaborationManager().getDatacenters(collaborationId);
        InterSchedulerResult interSchedulerResult = new InterSchedulerResult(this, allDatacenters);
        for (InstanceGroup instanceGroup : instanceGroups) {
            lastSendDCIndex = (lastSendDCIndex + 1) % allDatacenters.size();
            Datacenter targetDC = allDatacenters.get(lastSendDCIndex);
            interSchedulerResult.addDcResult(instanceGroup, targetDC);
        }

        NetworkTopology networkTopology = simulation.getNetworkTopology();
        System.out.println("run!");

        InterSchedulerResult interSchedulerResult2 = new InterSchedulerResult(this, allDatacenters);
        for (InstanceGroup instanceGroup: instanceGroups) {

            Datacenter datacenter1 = interSchedulerResult.getScheduledDatacenter(instanceGroup);
            if (datacenter1.getId() == -1){
                interSchedulerResult2.addFailedInstanceGroup(instanceGroup);
                instanceGroup.setState(UserRequest.FAILED);
                instanceGroup.getUserRequest().setState(UserRequest.FAILED);
                System.out.println("fail 1");
                continue;
            }
            UserRequest userRequest = instanceGroup.getUserRequest();
            InstanceGroupGraph instanceGroupGraph = userRequest.getInstanceGroupGraph();
            List<InstanceGroup> instanceGroups1=userRequest.getInstanceGroups();
            boolean fail = false;
            for (InstanceGroup instanceGroup1: instanceGroups1) {
                Datacenter datacenter2 = interSchedulerResult.getScheduledDatacenter(instanceGroup1);
                if (datacenter2.getId() == -1) {
                    interSchedulerResult2.addFailedInstanceGroup(instanceGroup);
                    System.out.println("fail 2");
                    fail = true;
                    break;
                }
                if (instanceGroupGraph.getDelay(instanceGroup, instanceGroup1) < networkTopology.getDelay(datacenter1, datacenter2) ||
                        instanceGroupGraph.getBw(instanceGroup, instanceGroup1) > networkTopology.getBw(datacenter1, datacenter2)) {
                    interSchedulerResult2.addFailedInstanceGroup(instanceGroup);
                    System.out.println("fail 3");
                    fail = true;
                    break;
                }
            }
            if (!fail) {
                interSchedulerResult2.addDcResult(instanceGroup, interSchedulerResult.getScheduledDatacenter(instanceGroup));
            } else {
//                instanceGroup.setState(UserRequest.FAILED);
//                userRequest.setState(UserRequest.FAILED);
            }
        }

        return interSchedulerResult2;
    }
}
