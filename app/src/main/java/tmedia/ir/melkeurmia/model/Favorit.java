package tmedia.ir.melkeurmia.model;

public class Favorit {

    private String name;
    private String path;
    private String id;

    public Favorit (String _name,String _path,String _id){
        this.name = _name;
        this.path = _path;
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
