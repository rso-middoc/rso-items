package src.main.java.si.fri.rso.middoc.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import src.main.java.si.fri.rso.middoc.models.converters.ItemsConverter;
import src.main.java.si.fri.rso.middoc.models.entities.ItemEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class ItemBean {

    private Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8081"; // only for demonstration
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

        return ItemsConverter.toDto(itemEntity);
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

}
