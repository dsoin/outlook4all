package com.dsoin.o4a.resources;

import com.dsoin.o4a.ESHelper;
import com.dsoin.o4a.beans.TypeBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class O4AResource extends ServerResource {
    protected final Logger log = LogManager.getLogger(this.getClass().getName());
    protected String query;
    protected List<TypeBean> typeBeans;
    protected ESHelper esHelper;
    protected String[] searchTypes;

    @Override
    protected void doInit() throws ResourceException {
        esHelper = new ESHelper((Client) getContext().getAttributes().get("esclient"));
        typeBeans = (List<TypeBean>) getContext().getAttributes().get("types");
        query = getRequestAttributes().get("q") != null ? (String) getRequestAttributes().get("q") : "";
        searchTypes = getSearchTypes((String) getRequestAttributes().get("types"));
        try {
            query = URLDecoder.decode(query.replace("+", "%2B"), "UTF-8");
            log.info(query);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        log.info(query);
    }

    private String[] getSearchTypes(String types) {
        String[] stringTypes;
        if ( types!=null )
            stringTypes = typeBeans.stream().
                filter(type -> types.contains(type.getType())).
                map(type -> type.getType()).toArray(String[]::new);
        else
            stringTypes = typeBeans.stream().map(type->type.getType()).toArray(String[]::new);
        return stringTypes;
    }

}
