package com.fortify.techsupport.o4a;

import com.fortify.techsupport.o4a.beans.StatsBean;
import com.fortify.techsupport.o4a.resources.ConversationResource;
import com.fortify.techsupport.o4a.resources.SearchResource;
import com.fortify.techsupport.o4a.resources.StatsResource;
import com.pff.PSTException;
import com.pff.PSTFile;
import org.apache.commons.logging.impl.SimpleLog;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by soind on 11/20/2014.
 */
public class Outlook4All {
    private static SimpleLog log = new SimpleLog("Outlook4All");
    private static Properties props = new Properties();

    public static void main(String[] args) throws Exception {

        props.load(new FileReader("o4a.properties"));
        initREST();
    }


    private static void initREST() throws Exception {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, new Integer(props.getProperty("port")));
        component.getClients().add(Protocol.FILE);
        log.info(Reference.decode(new File("").toURI().toString() + File.separator + "web"));
// Create an static contect app
        Application staticapp = new Application() {
            @Override
            public Restlet createInboundRoot() {

                return new Directory(getContext(), Reference.decode(new File("").toURI().toString() + File.separator + "web"));
            }
        };
        component.getDefaultHost().attach(staticapp);

        component.getDefaultHost().attach("/search/{q}", createApp( SearchResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/search/{q}/{from}", createApp( SearchResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/stats", createApp( StatsResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/getconv/{q}", createApp( ConversationResource.class, staticapp.getContext()));

        component.start();
    }

    private static Application createApp(final Class resource, final Context context) {
        Application app = new Application() {
            @Override
            public Restlet createInboundRoot() {

                Router router = new Router(getContext());

                router.attachDefault(resource);


                return router;
            }
        };
        app.setContext(context);
        return app;
    }


}
