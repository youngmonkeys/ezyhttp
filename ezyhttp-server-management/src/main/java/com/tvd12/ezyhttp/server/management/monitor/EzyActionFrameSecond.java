package com.tvd12.ezyhttp.server.management.monitor;

public class EzyActionFrameSecond extends EzyActionFrame {

    public EzyActionFrameSecond() {
        this(Integer.MAX_VALUE);
    }
    
    public EzyActionFrameSecond(long maxActions) {
        super(maxActions);
    }
    
    public EzyActionFrameSecond(long maxActions, long startTime) {
        super(maxActions, startTime);
    }
    
    @Override
    protected int getExistsTime() {
        return 1000;
    }

    @Override
    public EzyActionFrame nextFrame() {
        return new EzyActionFrameSecond(maxActions, endTime);
    }

}
