package si.fri.rso.middoc.lib;

public class PdfhelperResponse {

    private String itemId;
    private String compressedLocation;
    private Integer credit;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCompressedLocation() {
        return compressedLocation;
    }

    public void setCompressedLocation(String compressedLocation) {
        this.compressedLocation = compressedLocation;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }
}
