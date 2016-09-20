package com.dsoin.o4a;

import com.dsoin.o4a.resources.ConversationResource;
import com.dsoin.o4a.resources.DownloadResource;
import com.dsoin.o4a.resources.SearchResource;
import com.dsoin.o4a.resources.StatsResource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Created by soind on 11/20/2014.
 */
public class Outlook4All {
    private static final Logger log = LogManager.getLogger(Outlook4All.class);
    private static Properties props = new Properties();

    static {
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        PropertyConfigurator.configure("log4j.properties");
    }

    public static void main(String[] args) throws Exception {

        props.load(new FileReader("o4a.properties"));
        initREST();
    }


    private static void initREST() throws Exception {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, new Integer(props.getProperty("bindport")));
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
        Client client = TransportClient.builder().build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(props.getProperty("eshost")),
                                new Integer(props.getProperty("esport"))));

        staticapp.getContext().getAttributes().put("esclient", client);

        component.getDefaultHost().attach("/search/{q}", createApp(SearchResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/search/{q}/{from}", createApp(SearchResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/stats", createApp(StatsResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/getconv/{q}", createApp(ConversationResource.class, staticapp.getContext()));
        component.getDefaultHost().attach("/download/{id}", createApp(DownloadResource.class, staticapp.getContext()));

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
