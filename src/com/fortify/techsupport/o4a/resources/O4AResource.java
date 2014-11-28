package com.fortify.techsupport.o4a.resources;

import com.fortify.techsupport.o4a.ESHelper;
import org.apache.commons.logging.impl.SimpleLog;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class O4AResource extends ServerResource {
    protected String query;
    protected ESHelper esHelper = new ESHelper();
    protected SimpleLog log = new SimpleLog(this.getClass().getName());

    @Override
    protected void doInit() throws ResourceException {
        query = getRequestAttributes().get("q") != null ? (String) getRequestAttributes().get("q") : "";
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        log.info(query);
    }

}
