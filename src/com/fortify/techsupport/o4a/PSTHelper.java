package com.fortify.techsupport.o4a;

import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import org.apache.commons.logging.impl.SimpleLog;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexMissingException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by soind on 11/21/2014.
 */
public class PSTHelper {
    final static SimpleLog log = new SimpleLog(PSTHelper.class.getName());

    private static Client client = new TransportClient().
            addTransportAddress(new InetSocketTransportAddress("localhost", 9300));


    public static void main(String[] args) throws IOException, PSTException {
        Properties prop = new Properties();
        prop.load(new FileReader("o4a.properties"));
        String pstFilename = (String) prop.get("pstfile");
//        prepareIndexesAndMappings();


        PSTFile pstFile = new PSTFile(pstFilename);
        indexPST(pstFile.getRootFolder());
    }


    public static void indexPST(PSTFolder folder)
            throws PSTException, java.io.IOException {


        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                indexPST(childFolder);
            }
        }

        // and now the emails for this folder
        int topics = 0;
        if (folder.getContentCount() > 0) {
            log.info("" + folder.getContentCount());
            PSTMessage email = (PSTMessage) folder.getNextChild();
            while (email != null) {

//                db.insertItem(email.getConversationTopic(),email.getBody(),email.getBodyHTML(),email.getSenderName(),email.getClientSubmitTime());
                pushEmail(email);
                email = (PSTMessage) folder.getNextChild();
            }
        }
    }

    private static void pushEmail(PSTMessage email) {
        Map<String, Object> emailJson = new HashMap<>();
        emailJson.put("topic", email.getConversationTopic());
        emailJson.put("body", email.getBody());
        emailJson.put("body_html", email.getBodyHTML());
        emailJson.put("submit_time", email.getClientSubmitTime());
        emailJson.put("sender", email.getSenderName());
        emailJson.put("sender_email", email.getSenderEmailAddress());
        emailJson.put("sent_to", email.getDisplayTo());



        IndexResponse response = client.prepareIndex("emails", "qq")
                .setSource(emailJson
                )
                .execute()
                .actionGet();

    }

    private static void prepareIndexesAndMappings() throws IOException {

        try {

            client.admin().indices().delete(new DeleteIndexRequest("emails"));
            client.admin().indices().deleteMapping(new DeleteMappingRequest("emails").types("*")).actionGet();
        } catch (IndexMissingException ex) {
        }
        client.admin().indices().preparePutTemplate("*").
                setSource(new String(Files.readAllBytes(Paths.get("elasticsearch-template.json")))).

                execute().actionGet();

        client.admin().indices().create(Requests.createIndexRequest("emails")).actionGet();
    }


}
