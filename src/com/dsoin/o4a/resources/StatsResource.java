package com.dsoin.o4a.resources;

import com.dsoin.o4a.beans.StatsBean;
import org.restlet.resource.Get;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class StatsResource extends O4AResource {

    @Get("json")
    public StatsBean getStats() {
        return esHelper.getStats(searchTypes);
    }
}
