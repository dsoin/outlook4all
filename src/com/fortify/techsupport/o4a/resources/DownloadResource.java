package com.fortify.techsupport.o4a.resources;

import com.fortify.techsupport.o4a.beans.AttachmentBean;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.FileRepresentation;
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

        ByteArrayRepresentation ba = new ByteArrayRepresentation(ab.getData(), new MediaType(ab.getMime()));
        Disposition disp = new Disposition(Disposition.NAME_FILENAME);
        disp.setFilename(ab.getFilename());
        ba.setDisposition(disp);

        log.info(ba.getDisposition().getFilename());
        return ba;
    }
}
