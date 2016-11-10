package com.dsoin.o4a.resources;

import com.dsoin.o4a.beans.SearchBean;
import org.restlet.resource.Get;

import java.util.List;

/**
 * Created by Dmitrii Soin on 28/11/14.
 */
public class ConversationResource extends O4AResource {

    @Get("json")
    public List<SearchBean> getConversation() {
        return esHelper.getConversation(query, searchTypes);

    }
}
