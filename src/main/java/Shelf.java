import java.util.HashMap;

/**
 * @author Kaitlyn Chow
 * This is a POJO that represents a shelf
 * used in the larger Library project
 */
public class Shelf {
    public static final int SHELF_NUMBER_ = 0;
    public static final int SUBJECT_ = 1;

    private HashMap<Book, Integer> books;
    private int shelfNumber;
    private String subject;

    public Shelf(){

    }

    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.subject = subject;
        books = new HashMap<>();
    }

    public HashMap<Book, Integer> getBooks() {
        return books;
    }

    public void setBooks(HashMap<Book, Integer> books) {
        this.books = books;
    }

    public int getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
