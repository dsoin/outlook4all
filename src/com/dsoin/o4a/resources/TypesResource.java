package com.dsoin.o4a.resources;

import com.dsoin.o4a.beans.TypeBean;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.List;

/**
 * Created by dsoin on 09/11/16.
 */
public class TypesResource extends O4AResource {
    @Get("json")
    public List<TypeBean> getTypes() {
        return typeBeans;
    }
}
