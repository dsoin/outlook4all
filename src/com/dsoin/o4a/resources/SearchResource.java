package com.dsoin.o4a.resources;

import com.dsoin.o4a.beans.SearchResultsBean;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class SearchResource extends O4AResource {
    private int from = 0;

    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        String fromStr = (String) getRequestAttributes().get("from");
        try {
            from = new Integer(fromStr);
        } catch (NumberFormatException ex) {
            log.error(ex);
        }

    }

    @Get("json")
    public SearchResultsBean searchEmails() {

        boolean phrase = query.startsWith("\"") && query.endsWith("\"");
        return esHelper.searchEmails(query, from,phrase);

    }
}
