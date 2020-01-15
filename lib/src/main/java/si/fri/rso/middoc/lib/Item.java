package src.main.java.si.fri.rso.middoc.lib;

import java.time.Instant;

public class Item {

    private Integer itemId;
    private String title;
    private String description;
    private String format;
    private Instant created;
    private String uri;
    private Integer collectionId;
    private String collectionTitle;
    private String similarCollections;
    private String compressedDownload;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public String getSimilarCollections() {
        return similarCollections;
    }

    public void setSimilarCollections(String similarCollections) {
        this.similarCollections = similarCollections;
    }

    public String getCompressedDownload() {
        return compressedDownload;
    }

    public void setCompressedDownload(String compressedDownload) {
        this.compressedDownload = compressedDownload;
    }
}
