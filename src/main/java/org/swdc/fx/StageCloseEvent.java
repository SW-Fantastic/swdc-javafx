package org.swdc.fx;

import org.swdc.dependency.event.AbstractEvent;

public class StageCloseEvent extends AbstractEvent {

    public StageCloseEvent(Object clazz) {
        super(clazz);
    }

}
