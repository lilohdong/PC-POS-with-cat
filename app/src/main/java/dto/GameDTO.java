package dto;


public class GameDTO {
    private String g_id;
    private String title;
    private String publisher;
    public GameDTO() {}
    public GameDTO(String g_id, String title,String publisher) {
        this.g_id = g_id;
        this.title = title;
        this.publisher = publisher;
    }
    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

}
