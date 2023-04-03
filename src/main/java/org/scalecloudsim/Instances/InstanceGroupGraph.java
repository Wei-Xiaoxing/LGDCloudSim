package org.scalecloudsim.Instances;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface InstanceGroupGraph extends RequestEntity{
    Logger LOGGER = LoggerFactory.getLogger(InstanceGroupGraph.class.getSimpleName());

    boolean getDirected();

     InstanceGroupGraph setDirected(boolean directed);

     InstanceGroupGraph addEdge(InstanceGroup src, InstanceGroup dst, double delay, long bw);

     InstanceGroupGraph addEdge(InstanceGroupEdge edge);

     int removeEdge(InstanceGroup src, InstanceGroup dst);

     InstanceGroupEdge getEdge(InstanceGroup src, InstanceGroup dst);

     List getGraph();

     List getDstList(InstanceGroup src);

}
