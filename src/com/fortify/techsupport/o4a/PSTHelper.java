package com.fortify.techsupport.o4a;

import com.pff.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.impl.SimpleLog;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexMissingException;

import java.io.*;
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
        prepareIndexesAndMappings();


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
            PSTMessage email = (PSTMessage) folder.getNextChild();
            while (email != null) {

               String id = pushEmail(email);
                if (email.hasAttachments()) {
                    pushAttachments(email, id);

                }
                email = (PSTMessage) folder.getNextChild();
                //break;
            }
        }
    }

    private static void pushAttachments(PSTMessage email, String id) throws PSTException, IOException {
        for (int i=0;i<email.getNumberOfAttachments();i++) {
            PSTAttachment attach = email.getAttachment(i);
            Map<String, Object> emailJson = new HashMap<>();
            String filename = attach.getLongFilename().isEmpty()?attach.getFilename():attach.getLongFilename();
            byte[] encodedAttach = getAttachment(attach);
            emailJson.put("filename",filename);
            emailJson.put("email_id",id);
            emailJson.put("mime",attach.getMimeTag());
            emailJson.put("size",attach.getAttachSize());
            emailJson.put("attachment",new String(encodedAttach));

            IndexResponse response = client.prepareIndex("attachments", "ssc")
                    .setSource(emailJson
                    )
                    .execute()
                    .actionGet();

        }
    }

    private static byte[] getAttachment(PSTAttachment attach) throws IOException, PSTException {
        InputStream attachmentStream = attach.getFileInputStream();
        byte[] buffer = new byte[attach.getAttachSize()];
        int count = attachmentStream.read(buffer);
        byte[] endBuffer = new byte[count];
        System.arraycopy(buffer, 0, endBuffer, 0, count);
        attachmentStream.close();
        return Base64.encodeBase64(endBuffer);

    }

    private static String  pushEmail(PSTMessage email) {
        Map<String, Object> emailJson = new HashMap<>();
        emailJson.put("topic", email.getConversationTopic());
        emailJson.put("body", email.getBody());
        emailJson.put("body_html", email.getBodyHTML());
        emailJson.put("submit_time", email.getClientSubmitTime());
        emailJson.put("sender", email.getSenderName());
        emailJson.put("sender_email", email.getSenderEmailAddress());
        emailJson.put("sent_to", email.getDisplayTo());
        if (email.hasAttachments())
            emailJson.put("has_attachment",true);



        IndexResponse response = client.prepareIndex("emails", "ssc")
                .setSource(emailJson
                )
                .execute()
                .actionGet();
        return response.getId();

    }

    private static void prepareIndexesAndMappings() throws IOException {

        try {

            client.admin().indices().delete(new DeleteIndexRequest("emails"));
            client.admin().indices().delete(new DeleteIndexRequest("attachments"));
            client.admin().indices().deleteMapping(new DeleteMappingRequest("emails").types("*")).actionGet();
            client.admin().indices().deleteMapping(new DeleteMappingRequest("attachments").types("*")).actionGet();
        } catch (IndexMissingException ex) {
        }
        client.admin().indices().preparePutTemplate("emails").
                setSource(new String(Files.readAllBytes(Paths.get("emails-template.json")))).

                execute().actionGet();
        client.admin().indices().preparePutTemplate("attachments").
                setSource(new String(Files.readAllBytes(Paths.get("attachments-template.json")))).

                execute().actionGet();

        client.admin().indices().create(Requests.createIndexRequest("emails")).actionGet();
    }


}
