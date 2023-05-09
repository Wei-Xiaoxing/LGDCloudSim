package org.scalecloudsim.innerscheduler;

import lombok.Getter;
import lombok.Setter;
import org.scalecloudsim.request.Instance;
import org.scalecloudsim.datacenter.Datacenter;
import org.scalecloudsim.datacenter.InstanceQueue;
import org.scalecloudsim.datacenter.InstanceQueueFifo;
import org.scalecloudsim.statemanager.DelayState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InnerSchedulerSimple implements InnerScheduler {
    @Getter
    Datacenter datacenter;
    @Getter
    @Setter
    Map<Integer, Double> partitionDelay;
    List<Integer> partitionTraverseList;
    @Getter
    int id;
    @Getter
    @Setter
    String name;
    InstanceQueue instanceQueue;

    @Getter
    @Setter
    double scheduleCostTime = 0.2;

    @Getter
    @Setter
    int firstPartitionId = -1;

    int partitionNum = 0;

    @Getter
    double lastScheduleTime = 0;

    public InnerSchedulerSimple(Map<Integer, Double> partitionDelay) {
        this.partitionDelay = partitionDelay;
        //对于partitionDelay这个map，按照value从小到大排序，得到partitionTraverseList
        this.partitionTraverseList = partitionDelay.entrySet().
                stream().sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        instanceQueue = new InstanceQueueFifo(1000);
    }

    public InnerSchedulerSimple(int id, Map<Integer, Double> partitionDelay) {
        this(partitionDelay);
        setId(id);
    }

    public InnerSchedulerSimple(int id, int firstPartitionId, int partitionNum) {
        instanceQueue = new InstanceQueueFifo(1000);
        this.firstPartitionId = firstPartitionId;
        this.partitionNum = partitionNum;
        setId(id);
    }

    public void setId(int id) {
        this.id = id;
        this.name = "InScheduler" + id;
    }

    @Override
    public InnerScheduler addInstance(List<Instance> instances) {
        for (Instance instance : instances) {
            instanceQueue.add(instance);
        }
        return this;
    }

    @Override
    public InnerScheduler addInstance(Instance instance) {
        instanceQueue.add(instance);
        return this;
    }

    @Override
    public boolean isQueueEmpty() {
        return instanceQueue.size() == 0;
    }

    @Override
    public int getQueueSize() {
        return instanceQueue.size();
    }

    @Override
    public Map<Integer, List<Instance>> schedule() {
        //TODO 域内调度
        List<Instance> instances = instanceQueue.getBatchItem();
        DelayState delayState = datacenter.getStateManager().getNewDelayState(this);
        double startTime = System.currentTimeMillis();
        Map<Integer, List<Instance>> res = scheduleInstances(instances, delayState);
        double endTime = System.currentTimeMillis();
        lastScheduleTime = datacenter.getSimulation().clock();
        this.scheduleCostTime = 0.2;//(endTime-startTime)/10;
        LOGGER.info("{}: {}'s {} starts scheduling {} instances,cost {} ms", datacenter.getSimulation().clockStr(), datacenter.getName(), getName(), instances.size(), scheduleCostTime);
        return res;
    }

    private Map<Integer, List<Instance>> scheduleInstances(List<Instance> instances, DelayState delayState) {
        //TODO 域内调度
        Map<Integer, List<Instance>> res = new HashMap<>();
        for (Instance instance : instances) {
            int suitId = -1;
            for (int partitionId = firstPartitionId; partitionId < firstPartitionId + partitionNum; partitionId++) {
                int[] range = datacenter.getStateManager().getPartitionRangesManager().getRange(partitionId);
                for (int i = range[0]; i <= range[1]; i++) {
                    if (delayState.isSuitable(i, instance)) {
                        suitId = i;
                        break;
                    }
                }
                if (suitId != -1) {
                    break;
                }
            }
            if (suitId != -1) {
                delayState.allocateTmpResource(suitId, instance);
            }
            res.putIfAbsent(suitId, new ArrayList<>());
            res.get(suitId).add(instance);
        }
        return res;
    }

    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
