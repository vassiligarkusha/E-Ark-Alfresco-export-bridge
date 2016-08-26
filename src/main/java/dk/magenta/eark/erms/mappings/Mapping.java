package dk.magenta.eark.erms.mappings;

import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author lanre.
 */
public class Mapping {

    private String name, syspath, created, author, realFileName;
    boolean isEmpty;

    //<editor-fold desc="Getters and Setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSyspath() {
        return syspath;
    }

    public void setSyspath(String syspath) {
        this.syspath = syspath;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }
    //</editor-fold>


    public Mapping() {
        this.isEmpty = true;
    }

    public Mapping(String name, String syspath, String created, String realFileName) {
        this.name = name;
        this.syspath = syspath;
        this.created = created;
        this.realFileName = realFileName;
    }

    /**
     * Return a json object representation of this object instance
     * @return
     */
    public JsonObject toJsonObject(){
        JsonObjectBuilder tmp = Json.createObjectBuilder();
        tmp.add("name", this.getName());
        tmp.add("created", this.getCreated());
        tmp.add("realFileName", this.getRealFileName());
        if(StringUtils.isNotBlank(this.getAuthor()) )
            tmp.add("realFileName", this.getAuthor());

        return tmp.build();
    }

    /**
     * Used to return an empty map (to avoid null patterns)
     */
    public static final Mapping EMPTY_MAP = new Mapping();
}
