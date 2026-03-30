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

}
