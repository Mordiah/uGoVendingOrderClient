package com.ugosmoothie.androidorderclient;

/**
 * Created by Michelle on 3/19/2016.
 */
public final class EventTypes {

    private EventTypes() {

    }

    public static enum SmoothieEvent {
        Purchase,
        Complete;

        public String getSmoothieEvent() {
            return this.name();
        }
    }
}
