package be.ugent.blog;

/**
 * Domain klasse die een blogpost voorstelt.
 * Bevat een id, titel en content.
 */
public class BlogPost {
    
    private Long id;
    private String titel;
    private String content;
    
    /**
     * Default constructor - vereist voor JSON deserialisatie.
     */
    public BlogPost() {
    }
    
    /**
     * Constructor met parameters voor eenvoudige initialisatie.
     */
    public BlogPost(Long id, String titel, String content) {
        this.id = id;
        this.titel = titel;
        this.content = content;
    }
    
    // Getters en Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitel() {
        return titel;
    }
    
    public void setTitel(String titel) {
        this.titel = titel;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", titel='" + titel + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
