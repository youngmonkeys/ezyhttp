package com.tvd12.ezyhttp.server.management.monitor;

import java.util.concurrent.atomic.AtomicLong;

import com.tvd12.ezyfox.io.EzyDates;

import lombok.Getter;

@Getter
public abstract class EzyActionFrame {

    protected volatile long actions;
    protected final long endTime;
    protected final long startTime;
    protected final long maxActions;
    protected final long id = COUNTER.incrementAndGet();
    
    private static final AtomicLong COUNTER  = new AtomicLong(0);
    
    public EzyActionFrame(long maxActions) {
        this(maxActions, System.currentTimeMillis());
    }
    
    public EzyActionFrame(long maxActions, long startTime) {
        this.maxActions = maxActions;
        this.startTime = startTime;
        this.endTime = startTime + getExistsTime();
    }
    
    protected abstract int getExistsTime();
    
    public boolean addActions(long actions) {
        return (this.actions += actions) > maxActions;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
    
    public boolean isInvalid() {
        return actions > maxActions;
    }
    
    public abstract EzyActionFrame nextFrame();
    
    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(EzyDates.format(startTime))
                .append(" -> ")
                .append(EzyDates.format(endTime))
                .toString();
    }
    
}
