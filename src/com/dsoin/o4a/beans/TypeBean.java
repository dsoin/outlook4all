package com.dsoin.o4a.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsoin on 09/11/16.
 */
public class TypeBean {
    private String type;
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type+":"+enabled;
    }
}
