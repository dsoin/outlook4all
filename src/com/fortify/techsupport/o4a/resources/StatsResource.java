package com.fortify.techsupport.o4a.resources;

import com.fortify.techsupport.o4a.beans.StatsBean;
import org.restlet.resource.Get;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class StatsResource extends O4AResource {

    @Get("json")
    public StatsBean getStats() {
        return esHelper.getStats();
    }
}
