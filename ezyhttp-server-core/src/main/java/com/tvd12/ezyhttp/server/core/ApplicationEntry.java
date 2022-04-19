package com.tvd12.ezyhttp.server.core;

import com.tvd12.ezyfox.util.EzyInitable;
import com.tvd12.ezyfox.util.EzyStartable;

public interface ApplicationEntry extends EzyInitable, EzyStartable {

    @Override
    default void init() {
    }

}
