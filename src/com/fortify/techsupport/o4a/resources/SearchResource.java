package com.fortify.techsupport.o4a.resources;

import com.fortify.techsupport.o4a.ESHelper;
import com.fortify.techsupport.o4a.beans.SearchBean;
import com.fortify.techsupport.o4a.beans.SearchResultsBean;
import org.apache.commons.logging.impl.SimpleLog;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class SearchResource extends O4AResource {
    private int from=0;
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        String fromStr = (String) getRequestAttributes().get("from");
        try {
            from = new Integer(fromStr);
        } catch (NumberFormatException ex) {log.error(ex);}

    }

    @Get("json")
    public SearchResultsBean searchEmails() {

        return esHelper.searchEmails(query,from);

    }
}
