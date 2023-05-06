package org.cloudsimplus.core;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.core.events.*;
import org.cloudsimplus.network.topologies.NetworkTopology;
import org.scalecloudsim.datacenter.CollaborationManager;
import org.scalecloudsim.record.CsvRecord;
import org.scalecloudsim.record.MemoryRecord;
import org.scalecloudsim.record.SqlRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CloudSim implements Simulation {
    public static final Logger LOGGER = LoggerFactory.getLogger(CloudSim.class.getSimpleName());
    public static final String VERSION = "ScaleCloudSim 0.0.0";
    double clock;
    @Getter
    private boolean running;
    /**
     * The queue of events that will be sent in a future simulation time.
     */
    private final FutureQueue future;

    /**
     * The deferred event queue.
     */
    private final DeferredQueue deferred;
    private final List<CloudSimEntity> entityList;

    @Getter
    private final CloudInformationService cis;
    @Getter
    private NetworkTopology networkTopology;
    @Getter
    CollaborationManager collaborationManager;
    @Getter
    SqlRecord sqlRecord;

    @Getter
    private int simulationAccuracy;

    private double terminationTime = -1;

    public CloudSim() {
        clock = 0;
        this.entityList = new ArrayList<>();
        this.future = new FutureQueue();
        this.deferred = new DeferredQueue();
        this.cis = new CloudInformationService(this);
        this.simulationAccuracy = 2;
        this.sqlRecord = new SqlRecord();
    }

    @Override
    public double clock() {
        return clock;
    }

    @Override
    public String clockStr() {
        return "%.2f ms".formatted(clock);
    }

    @Override
    public Simulation setClock(double time) {
        this.clock = time;
        return this;
    }

    @Override
    public void addEntity(@NonNull final CloudSimEntity entity) {
        if (running) {
            final var evt = new CloudSimEvent(0, entity, SimEntity.NULL, CloudSimTag.NONE, entity);
            future.addEvent(evt);
        }

        if (entity.getId() == -1) { // Only add once!
            entity.setId(entityList.size());
            entityList.add(entity);
        }
    }

    @Override
    public SimEvent select(final SimEntity dest, final Predicate<SimEvent> predicate) {
        final SimEvent evt = findFirstDeferred(dest, predicate);
        if (evt != SimEvent.NULL) {
            deferred.remove(evt);
        }

        return evt;
    }

    @Override
    public SimEvent findFirstDeferred(final SimEntity dest, final Predicate<SimEvent> predicate) {
        return filterEventsToDestinationEntity(deferred, predicate, dest).findFirst().orElse(SimEvent.NULL);
    }

    @Override
    public void send(@NonNull final SimEvent evt) {
        //Events with a negative tag have higher priority
        if (evt.getTag() < 0)
            future.addEventFirst(evt);
        else future.addEvent(evt);
    }

    @Override
    public boolean terminateAt(double time) {
        if (time <= clock) {
            return false;
        }

        terminationTime = time;
        return true;
    }

    private Stream<SimEvent> filterEventsToDestinationEntity(final EventQueue queue, final Predicate<SimEvent> predicate, final SimEntity dest) {
        return filterEvents(queue, predicate.and(evt -> evt.getDestination() == dest));
    }

    private Stream<SimEvent> filterEvents(final EventQueue queue, final Predicate<SimEvent> predicate) {
        return queue.stream().filter(predicate);
    }

    @Override
    public double start() {
//        aborted = false;
        startSync();
        MemoryRecord.recordMemory();
//
        while (processEvents(Double.MAX_VALUE)) {
            MemoryRecord.recordMemory();
            //All the processing happens inside the method called above
        }
//
        finish();
//        try {
//            getCsvRecord().getPrinter().close();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
        MemoryRecord.recordMemory();
        getSqlRecord().close();
        return clock;
    }

    protected boolean processEvents(final double until) {
        if (!runClockTickAndProcessFutureEvents(until)) {
            return false;
        }
        LOGGER.debug(this.deferred.toString());
//        if (!runClockTickAndProcessFutureEvents(until) && !isToWaitClockToReachTerminationTime()) {
//            return false;
//        }
//
//        notifyOnSimulationStartListeners(); //it's ensured to run just once.
//        if (logSimulationAborted()) {
//            return false;
//        }
//
//        /* If it's time to terminate the simulation, sets a new termination time
//         * so that events to finish Cloudlets with a negative length are received.
//         * Cloudlets with a negative length must keep running
//         * until a CLOUDLET_FINISH event is sent to the broker or the termination time is reached.
//         */
        if (isTimeToTerminateSimulationUnderRequest()) {
            return false;
        }
        return true;
//
//        checkIfSimulationPauseRequested();
    }

    private void finish() {
        LOGGER.info("Simulation finished at {}.", clockStr());
    }

    @Override
    public boolean isTimeToTerminateSimulationUnderRequest() {
        return isTerminationTimeSet() && clock >= terminationTime;
    }

    private boolean runClockTickAndProcessFutureEvents(final double until) {
        executeRunnableEntities(until);
        if (future.isEmpty()) {
            return false;
        }

        final SimEvent first = future.first();
        if (first.getTime() <= until) {
            processFutureEventsHappeningAtSameTimeOfTheFirstOne(first);
            return true;
        }

        return false;
    }

    private void executeRunnableEntities(final double until) {
        /* Uses an indexed loop instead of anything else to avoid
        ConcurrencyModificationException when a HostFaultInjection is created inside a DC. */
        for (int i = 0; i < entityList.size(); i++) {
            final CloudSimEntity ent = entityList.get(i);
            if (ent.getState() == SimEntity.State.RUNNABLE) {
                ent.run(until);
            }
        }
    }

    private void processFutureEventsHappeningAtSameTimeOfTheFirstOne(final SimEvent firstEvent) {
        processEvent(firstEvent);
        future.remove(firstEvent);

        while (!future.isEmpty()) {
            final SimEvent evt = future.first();
            if (evt.getTime() != firstEvent.getTime())
                break;
            processEvent(evt);
            future.remove(evt);
        }
    }

    protected void processEvent(final SimEvent evt) {
        if (evt.getTime() < clock) {
            final var msg = "Past event detected. Event time: %.2f Simulation clock: %.2f";
            throw new IllegalArgumentException(msg.formatted(evt.getTime(), clock));
        }

        setClock(evt.getTime());
        if (CloudSimTag.UNIQUE_TAG.contains(evt.getTag())) {
            if (deferred.isExistSameEvent(evt.getDestination(), evt.getTag(), evt.getData())) {
                return;
            }
        }
        deferred.addEvent(evt);
    }


    @Override
    public void startSync() {
//        if(alreadyRunOnce){
//            throw new UnsupportedOperationException(
//                    "You can't run a simulation that has already run previously. " +
//                            "If you've paused the simulation and want to resume it, call the resume() method.");
//        }

        LOGGER.info("{}================== Starting {} =================={}", System.lineSeparator(), VERSION, System.lineSeparator());
        startEntitiesIfNotRunning();
//        this.alreadyRunOnce = true;
    }

    @Override
    public int getNumEntities() {
        return entityList.size();
    }

    private void startEntitiesIfNotRunning() {
        if (running) {
            return;
        }

        running = true;
        entityList.forEach(SimEntity::start);
        LOGGER.info("Entities started.");
    }

    @Override
    public void setNetworkTopology(NetworkTopology networkTopology) {
        this.networkTopology = networkTopology;
    }

    @Override
    public void setCollaborationManager(CollaborationManager collaborationManager) {
        this.collaborationManager = collaborationManager;
    }

    @Override
    public void setSimulationAccuracy(int simulationAccuracy) {
        this.simulationAccuracy = simulationAccuracy;
    }

    @Override
    public boolean isTerminationTimeSet() {
        return terminationTime > 0.0;
    }
}
