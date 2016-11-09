package com.dsoin.o4a.resources;

import com.dsoin.o4a.ESHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class O4AResource extends ServerResource {
    protected final Logger log = LogManager.getLogger(this.getClass().getName());
    protected String query;
    protected ESHelper esHelper;

    @Override
    protected void doInit() throws ResourceException {
        esHelper = new ESHelper((Client) getContext().getAttributes().get("esclient"));
        query = getRequestAttributes().get("q") != null ? (String) getRequestAttributes().get("q") : "";
        try {
            log.info(query);
            query = URLDecoder.decode(query.replace("+", "%2B"), "UTF-8");
            log.info(query);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        log.info(query);
    }

}
