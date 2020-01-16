package src.main.java.si.fri.rso.middoc.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.middoc.lib.PdfhelperRequest;
import si.fri.rso.middoc.lib.PdfhelperResponse;
import src.main.java.si.fri.rso.middoc.lib.Item;
import src.main.java.si.fri.rso.middoc.models.converters.ItemsConverter;
import src.main.java.si.fri.rso.middoc.models.entities.ItemEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import src.main.java.si.fri.rso.middoc.services.config.IntegrationProperties;

@RequestScoped
public class ItemBean {

    private Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    @Inject
    @DiscoverService("collections-service")
    private Optional<String> collectionsServiceBaseUrl;

    @Inject
    @DiscoverService("pdfhelper-service")
    private Optional<String> pdfhelperServiceBaseUrl;

    @Inject
    private IntegrationProperties integrationProperties;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //collectionsServiceBaseUrl = Optional.of("http://localhost:8081"); // demonstration on localhost
    }


    public List<src.main.java.si.fri.rso.middoc.lib.Item> getItems() {

        TypedQuery<ItemEntity> query = em.createNamedQuery("ItemEntity.getAll",
                ItemEntity.class);

        return query.getResultList().stream().map(ItemsConverter::toDto).collect(Collectors.toList());

    }

    public List<src.main.java.si.fri.rso.middoc.lib.Item> getItemsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
                .map(ItemsConverter::toDto).collect(Collectors.toList());
    }

    public src.main.java.si.fri.rso.middoc.lib.Item getItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);

        if (itemEntity == null) {
            throw new NotFoundException();
        }

        Item item = ItemsConverter.toDto(itemEntity);

        if (integrationProperties.isIntegrateWithCollectionsService()) {
            item.setCollectionTitle(getCollectionTitle(item.getCollectionId()));
            item.setSimilarCollections(getSimilarCollections(item.getCollectionId()));
        }

        return item;
    }

    public src.main.java.si.fri.rso.middoc.lib.Item createItem(src.main.java.si.fri.rso.middoc.lib.Item item) {

        ItemEntity itemEntity = ItemsConverter.toEntity(item);

        try {
            beginTx();
            em.persist(itemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (itemEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ItemsConverter.toDto(itemEntity);
    }

    public src.main.java.si.fri.rso.middoc.lib.Item putItem(Integer id, src.main.java.si.fri.rso.middoc.lib.Item item) {

        ItemEntity c = em.find(ItemEntity.class, id);

        if (c == null) {
            return null;
        }

        ItemEntity updatedItemEntity = ItemsConverter.toEntity(item);

        try {
            beginTx();
            updatedItemEntity.setId(c.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return ItemsConverter.toDto(updatedItemEntity);
    }

    public boolean deleteItem(Integer id) {

        ItemEntity item = em.find(ItemEntity.class, id);

        if (item != null) {
            try {
                beginTx();
                em.remove(item);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public String getCollectionTitle(Integer collectionId) {

        if (collectionsServiceBaseUrl.isPresent()) {

            log.info("Calling collections service: getting collection title.");

            try {
                return httpClient
                        .target(collectionsServiceBaseUrl.get() + "/v1/collections/" + collectionId + "/title")
                        .request().get(new GenericType<String>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public String getSimilarCollections(Integer collectionId) {

        if (collectionsServiceBaseUrl.isPresent()) {

            log.info("Calling collections service: getting similar collections.");

            try {
                return httpClient
                        .target(collectionsServiceBaseUrl.get() + "/v1/collections/" + collectionId + "/similar")
                        .request().get(new GenericType<String>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public Item compressItemPdf(Integer id) {

        Item item = getItem(id);

        if (pdfhelperServiceBaseUrl.isPresent()) {

            log.info("Calling pdfhelper service: compressing pdf.");

            JsonObject req = Json.createObjectBuilder()
                    .add("itemId", item.getItemId())
                    .add("itemLocation", item.getUri())
                    .build();

            PdfhelperRequest pdfhelperRequest = new PdfhelperRequest();
            pdfhelperRequest.setItemId(item.getItemId().toString());
            pdfhelperRequest.setItemLocation(item.getUri());

            try {
                PdfhelperResponse pdfhelperResponse = httpClient
                        .target(pdfhelperServiceBaseUrl.get() + "/v1/process")
                        .request().post(Entity.entity(pdfhelperRequest, MediaType.APPLICATION_JSON), PdfhelperResponse.class);
                item.setCompressedDownload(pdfhelperResponse.getCompressedLocation());
                return item;
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public boolean compressionReady() {
        return integrationProperties.isCompressionReady();
    }

}
