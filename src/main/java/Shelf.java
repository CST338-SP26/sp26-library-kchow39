import Utilities.Code;

import java.util.HashMap;
import java.util.Objects;

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
        books = new HashMap<>();
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

    /**
     * gets a book count
     * @param book as Book
     * @return the book count otherwise returns -1 if not found
     */
    public int getBookCount(Book book){
        return books.getOrDefault(book, -1);
    }

    /**
     * adds the parameter 'book' to the HashMap of books stored on the shelf
     * @param book as Book
     * @return a success code if added and a subject mismatch if the book does not match the subject
     */
    public Code addBook(Book book){
        if(!books.containsKey(book) && !book.getSubject().equals(subject)){
            return Code.SHELF_SUBJECT_MISMATCH_ERROR;
        }
        if(books.containsKey(book)){
            books.put(book, books.get(book)+1);
        } else {
            books.put(book, 1);
        }
        System.out.println(book + " added to shelf " + this);
        return Code.SUCCESS;
    }

    /**
     * removes a book from the shelf
     * @param book as Book
     * @return a "not in inventory error" if the book is not on the shelf
     */
    public Code removeBook(Book book){
        if(!books.containsKey(book)){
            System.out.println(book.getTitle() + "is not on shelf " + subject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        if(books.containsKey(book) && books.get(book) == 0){
            System.out.println("No copies of " + book.getTitle() + " remain on the shelf " + subject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        books.put(book, books.get(book)-1);
        System.out.println(book.getTitle() + " successfully removed from shelf " + subject);
        return Code.SUCCESS;
    }

    public String listBooks(){
        StringBuilder sb = new StringBuilder();
        if(books.size() == 1){
            sb.append(books.size()).append(" book on shelf: ").append(shelfNumber).append(" : ").append(subject).append("\n");
        } else {
            sb.append(books.size()).append(" books on shelf: ").append(shelfNumber).append(" : ").append(subject).append("\n");
        }
        for(Book b : books.keySet()){
            sb.append(books.get(b)).append(" ").append(b).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Shelf shelf = (Shelf) o;
        return getShelfNumber() == shelf.getShelfNumber() && Objects.equals(getSubject(), shelf.getSubject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShelfNumber(), getSubject());
    }

    @Override
    public String toString() {
        return shelfNumber + " : " + subject;
    }


}
