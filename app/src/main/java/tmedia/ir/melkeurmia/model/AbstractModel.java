package tmedia.ir.melkeurmia.model;

public class AbstractModel {

    private String title;

    private int ID;

    private String path;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public AbstractModel(String title, String path, int id) {
        this.title = title;
        this.path = path;
        this.ID = id;
    }


    public AbstractModel() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
