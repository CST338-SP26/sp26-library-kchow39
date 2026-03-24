import Utilities.Code;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kaitlyn Chow
 * This is a POJO that represents the Reader
 * used in the larger Library project
 */
public class Reader {
    public static final int CARD_NUMBER_ = 0;
    public static final int NAME_ = 1;
    public static final int PHONE_ = 2;
    public static final int BOOK_COUNT_ = 3;
    public static final int BOOK_START_ = 4;

    private int cardNumber;
    private String name;
    private String phone;
    private List<Book> books;

    public Reader(int cardNumber, String name, String phone) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.phone = phone;
        books = new ArrayList<>();
    }

    public Code addBook(Book book){
        if (hasBook(book)) {
            return Code.BOOK_ALREADY_CHECKED_OUT_ERROR;
        }
        books.add(book);
        return Code.SUCCESS;
    }

    public Code removeBook(Book book){
        if(!hasBook(book)){
            return Code.READER_DOESNT_HAVE_BOOK_ERROR;
        }
        if(books.remove(book)){
            return Code.SUCCESS;
        } else {
            return Code.READER_COULD_NOT_REMOVE_BOOK_ERROR;
        }
    }

    public boolean hasBook(Book book){
        return books.contains(book);
    }

    public int getBookCount(){
        return books.size();
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return getCardNumber() == reader.getCardNumber() && Objects.equals(getName(), reader.getName()) && Objects.equals(getPhone(), reader.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCardNumber(), getName(), getPhone());
    }

    @Override
    public String toString() {
        return name + " (#" + cardNumber + ") has checked out " + books.toString().replace("[", "{").replace("]", "}");
    }
}
