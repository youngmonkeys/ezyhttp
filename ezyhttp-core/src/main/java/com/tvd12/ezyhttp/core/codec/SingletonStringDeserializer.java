package com.tvd12.ezyhttp.core.codec;

public final class  SingletonStringDeserializer extends DefaultStringDeserializer {

    private static final SingletonStringDeserializer INSTANCE 
            = new SingletonStringDeserializer();
    
    private SingletonStringDeserializer() {}
    
    public static SingletonStringDeserializer getInstance() {
        return INSTANCE;
    }
    
}
