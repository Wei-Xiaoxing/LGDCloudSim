package org.cpnsim.datacenter;

import org.cloudsimplus.core.CloudSim;
import org.cloudsimplus.core.Simulation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//还有一些测试没有写完，但是这个类的功能已经实现了
public class CollaborationManagerSimpleTest {
    @Test
    public void testAddDatacenter() {
        Simulation scaleCloudSim = new CloudSim();
        Datacenter dc1 = new DatacenterSimple(scaleCloudSim);
        CollaborationManager collaborationManager = new CollaborationManagerSimple();
        collaborationManager.addDatacenter(dc1, 0);
        Map<Integer, Set<Datacenter>> excepted = Map.of(0, Set.of(dc1));
        assertEquals(excepted, collaborationManager.getCollaborationMap());
        Set<Integer> exceptedSet = Set.of(0);
        assertEquals(exceptedSet, dc1.getCollaborationIds());
    }

    @Test
    public void testAddDatacenter2() {
        Simulation scaleCloudSim = new CloudSim();
        CollaborationManager collaborationManager = new CollaborationManagerSimple();
        Datacenter dc0 = new DatacenterSimple(scaleCloudSim);
        collaborationManager.addDatacenter(dc0, 0);
        Datacenter dc1 = new DatacenterSimple(scaleCloudSim);
        Datacenter dc2 = new DatacenterSimple(scaleCloudSim);
        Map<Integer, Set<Datacenter>> collaborationMap = Map.of(0, Set.of(dc1), 1, Set.of(dc0, dc2));
        collaborationManager.addDatacenter(collaborationMap);
        Map<Integer, Set<Datacenter>> excepted = Map.of(0, Set.of(dc0, dc1), 1, Set.of(dc0, dc2));
        assertEquals(excepted, collaborationManager.getCollaborationMap());
        Set<Integer> exceptedSet = Set.of(0, 1);
        assertEquals(exceptedSet, dc0.getCollaborationIds());
        Set<Integer> exceptedSet1 = Set.of(0);
        assertEquals(exceptedSet1, dc1.getCollaborationIds());
        Set<Integer> exceptedSet2 = Set.of(1);
        assertEquals(exceptedSet2, dc2.getCollaborationIds());
    }

    @Test
    public void testGetOtherDatacenters() {
        Simulation scaleCloudSim = new CloudSim();
        CollaborationManager collaborationManager = new CollaborationManagerSimple();
        Datacenter dc0 = new DatacenterSimple(scaleCloudSim);
        collaborationManager.addDatacenter(dc0, 0);
        Datacenter dc1 = new DatacenterSimple(scaleCloudSim);
        Datacenter dc2 = new DatacenterSimple(scaleCloudSim);
        Map<Integer, Set<Datacenter>> collaborationMap = Map.of(0, Set.of(dc1), 1, Set.of(dc0, dc2));
        collaborationManager.addDatacenter(collaborationMap);
        List<Datacenter> excepted = List.of(dc1);
        assertEquals(excepted, collaborationManager.getOtherDatacenters(dc0, 0));
        Set<Datacenter> excepted1 = Set.of(dc1, dc2);
        assertEquals(excepted1, new HashSet<>(collaborationManager.getOtherDatacenters(dc0)));
    }
}
