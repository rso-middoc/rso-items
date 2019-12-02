package src.main.java.si.fri.rso.middoc.api.v1.resources;

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


}
