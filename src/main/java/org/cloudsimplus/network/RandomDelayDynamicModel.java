package org.cloudsimplus.network;

import org.cloudsimplus.core.SimEntity;

import java.util.Random;

public class RandomDelayDynamicModel implements DelayDynamicModel {
    @Override
    public double getDynamicDelay(SimEntity src, SimEntity dst, double delay, double time) {
        Random random = new Random((int) time);
        return Math.max(delay + random.nextGaussian() * 4, 0);
    }
}
