package com.dsoin.o4a.resources;

import com.dsoin.o4a.beans.AttachmentBean;
import org.apache.tika.Tika;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import java.io.IOException;

/**
 * Created by Dmitrii Soin on 05/12/14.
 */
public class DownloadResource extends O4AResource {
    private String id;

    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        id = (String) getRequestAttributes().get("id");
    }

    @Get
    public Representation download() throws IOException {
        AttachmentBean ab = esHelper.getAttachment(id);

        log.error(new MediaType(new Tika().detect(ab.getData())));
        ByteArrayRepresentation ba = new ByteArrayRepresentation(ab.getData(), new MediaType(new Tika().detect(ab.getData())));
        Disposition disp = new Disposition(Disposition.NAME_FILENAME);
        disp.setFilename(ab.getFilename());
        ba.setDisposition(disp);


        return ba;
    }
}
