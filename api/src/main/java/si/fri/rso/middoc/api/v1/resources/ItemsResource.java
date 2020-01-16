package src.main.java.si.fri.rso.middoc.api.v1.resources;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import src.main.java.si.fri.rso.middoc.services.beans.ItemBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemsResource {

    @Inject
    private ItemBean itemBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getItems() {

        List<src.main.java.si.fri.rso.middoc.lib.Item> items = itemBean.getItemsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(items).build();
    }

    @GET
    @Counted(name = "solo_item_counter")
    @Path("/{itemId}")
    public Response getItem(@PathParam("itemId") Integer itemId) {

        src.main.java.si.fri.rso.middoc.lib.Item item = itemBean.getItem(itemId);

        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(item).build();
    }

    @POST
    public Response createItem(src.main.java.si.fri.rso.middoc.lib.Item item) {

        if ((item.getTitle() == null || item.getDescription() == null || item.getUri() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            item = itemBean.createItem(item);
        }

        return Response.status(Response.Status.OK).entity(item).build();

    }

    @PUT
    @Path("{itemId}")
    public Response putItem(@PathParam("itemId") Integer itemId, src.main.java.si.fri.rso.middoc.lib.Item item) {

        item = itemBean.putItem(itemId, item);

        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") Integer itemId) {

        boolean deleted = itemBean.deleteItem(itemId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /*@GET
    @Path("info")
    public Response info() {
        String message = "{\n" +
                "    \"clani\": [\"ir8617\"],\n" +
                "    \"opis_projekta\": \"Preprosta aplikacija za shranjevanje in obdelavo e-knjig in dokumentov.\",\n" +
                "    \"mikrostoritve\": [\"http://34.77.80.201:8080/v1/items\"],\n" +
                "    \"github\": [\"https://github.com/rso-middoc/rso-items\"],\n" +
                "    \"travis\": [\"https://travis-ci.org/rso-middoc/rso-items\"],\n" +
                "    \"dockerhub\": [\"https://hub.docker.com/repository/docker/iramovs/rso-items\"]\n" +
                "}";

        return Response
                .status(Response.Status.OK)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }*/

    @GET
    @Timed(name = "compression_method")
    @Path("{itemId}/compress")
    public Response compressPdf(@PathParam("itemId") Integer itemId) {
        return Response.ok(itemBean.compressItemPdf(itemId)).build();
    }

    @GET
    @Path("compression")
    public Response compression() {
        if (itemBean.compressionReady())
            return Response.ok().build();
        return Response.status(400).entity("Not ready").build();
    }


}
