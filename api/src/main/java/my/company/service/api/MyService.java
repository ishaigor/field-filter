package my.company.service.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import my.company.service.api.model.TransferObject;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(MyService.ROOT_PATH)
public interface MyService {

    String ROOT_PATH = "/my/service";

    @GET
    @Path("/object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    TransferObject getTransferObject();
}
