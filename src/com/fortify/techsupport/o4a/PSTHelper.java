package com.fortify.techsupport.o4a;

import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by soind on 11/21/2014.
 */
public class PSTHelper {
    private static Logger log = LoggerFactory.getLogger("com.fortify.techsupport.o4a.PSTHelper");

    private  static Client client = new TransportClient().
            addTransportAddress(new InetSocketTransportAddress("localhost", 9300));


    public static void main(String[] args) throws IOException, PSTException {
        Properties prop = new Properties();
        prop.load(new FileReader("o4a.properties"));
        String pstFilename = (String) prop.get("pstfile");




        PSTFile pstFile = new PSTFile(pstFilename);
        indexPST(pstFile.getRootFolder());
    }



    public static void indexPST(PSTFolder folder)
            throws PSTException, java.io.IOException {

        //delete index
        DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest("emails")).actionGet();

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
                sendEmail(email);
                email = (PSTMessage) folder.getNextChild();
            }
        }
    }

    private static void sendEmail(PSTMessage email) {
        Map<String,Object> emailJson= new HashMap<>();
        emailJson.put("topic",email.getConversationTopic());
        emailJson.put("body",email.getBody());
        emailJson.put("body_html",email.getBodyHTML());
        emailJson.put("submit_time",email.getClientSubmitTime());
        emailJson.put("sender",email.getSenderName());
        emailJson.put("sender_email",email.getSenderEmailAddress());
        emailJson.put("sent_to",email.getDisplayTo());

        IndexResponse response = client.prepareIndex("emails", "ssc")
                .setSource(emailJson
                )
                .execute()
                .actionGet();

    }

}
