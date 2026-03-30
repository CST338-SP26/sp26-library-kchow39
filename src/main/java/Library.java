import java.util.HashMap;
import java.util.List;

/**
 * @author Kaitlyn Chow
 * This is a POJO that represents the whole Library
 * used in the larger Library project
 */
public class Library {
    public static final int LENDING_LIMIT = 5;

    private HashMap<Book, Integer> books;
    private static int libraryCard;
    private String name;
    private List<Reader> readers;
    private HashMap<String, Shelf> shelves;


}
