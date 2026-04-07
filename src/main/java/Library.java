import Utilities.Code;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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

    public Library(String name) {
        this.name = name;
        books = new HashMap<>();
        readers = new ArrayList<>();
        shelves = new HashMap<>();
    }

    /**
     * parses the entire csv file
     * @param filename as a String is the file
     * @return a code that gives errors
     */
    public Code init(String filename){
        try{
            File f = new File(filename);
            Scanner fs = new Scanner(f);
            int count = convertInt(fs.nextLine(), Code.BOOK_COUNT_ERROR);
            if(count < 0){
                return errorCode(count);
            }
            Code check = initBooks(count, fs);
            if(check != Code.SUCCESS){
                return check;
            }
            listBooks();

            count = convertInt(fs.nextLine(), Code.SHELF_COUNT_ERROR);
            if(count < 0){
                return errorCode(count);
            }
            check = initShelves(count, fs);
            if(check != Code.SUCCESS){
                return check;
            }
            listShelves();

            count = convertInt(fs.nextLine(), Code.READER_COUNT_ERROR);
            if(count < 0){
                return errorCode(count);
            }
            check = initReader(count, fs);
            if(check != Code.SUCCESS){
                return check;
            }
            listReaders();
            return Code.SUCCESS;

        }catch(FileNotFoundException e){
            return Code.FILE_NOT_FOUND_ERROR;
        }
    }

    /**
     * parses the books from a csv file
     * @param bookCount as an int is the number of books to parse
     * @param scan as a scanner that scans through the csv file
     * @return a Code which tells if it was successful or not
     */
    private Code initBooks(int bookCount, Scanner scan){
        if(bookCount < 1){
            return Code.LIBRARY_ERROR;
        }
        for(int i = 0; i < bookCount; i++){
            try {
                String[] bookArray = scan.nextLine().split(",");
                if(convertDate(bookArray[Book.DUE_DATE_]) == null){
                    return Code.DATE_CONVERSION_ERROR;
                }
                if(convertInt(bookArray[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR) <= 0){
                    return Code.PAGE_COUNT_ERROR;
                }
                Book book = new Book(bookArray[Book.ISBN_], bookArray[Book.TITLE_], bookArray[Book.SUBJECT_], convertInt(bookArray[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR), bookArray[Book.AUTHOR_], convertDate(bookArray[Book.DUE_DATE_]));
                addBook(book);
            }catch(IndexOutOfBoundsException e){
                return Code.BOOK_RECORD_COUNT_ERROR;
            }

        }
        return Code.SUCCESS;
    }

    /**
     * parses the shelves from the csv file
     * @param shelfCount as an integer is the number of shelves to parse
     * @param scan as a scanner to scan the shelf information from the csv file
     * @return a code on whether there was an error or not
     */
    private Code initShelves(int shelfCount, Scanner scan){
        if(shelfCount < 1){
            return Code.SHELF_COUNT_ERROR;
        }

        for(int i = 0; i < shelfCount; i++){
            try{
                String[] shelfArray = scan.nextLine().split(",");
                if(convertInt(shelfArray[Shelf.SHELF_NUMBER_], Code.SHELF_NUMBER_PARSE_ERROR) <= 0){
                    return Code.SHELF_NUMBER_PARSE_ERROR;
                }
                Shelf shelf = new Shelf(convertInt(shelfArray[Shelf.SHELF_NUMBER_], Code.SHELF_COUNT_ERROR), shelfArray[Shelf.SUBJECT_]);
                addShelf(shelf);

            } catch(IndexOutOfBoundsException e){
                return Code.SHELF_NUMBER_PARSE_ERROR;
            }
        }
        if(shelves.size() != shelfCount){
            System.out.println("Number of shelves doesn't match expected");
            return Code.SHELF_NUMBER_PARSE_ERROR;
        } else {
            return Code.SUCCESS;
        }

    }

    /**
     * parses the readers from the csv file
     * @param readerCount as an int is the number of readers being parsed
     * @param scan as a scanner to scan the information from the csv file
     * @return a code
     */
    private Code initReader(int readerCount, Scanner scan){
        if(readerCount < 1){
            return Code.READER_COUNT_ERROR;
        }
        for(int i = 0; i < readerCount; i++){
            String[] readerArray = scan.nextLine().split(",");
            Reader reader = new Reader(convertInt(readerArray[Reader.CARD_NUMBER_], Code.READER_CARD_NUMBER_ERROR), readerArray[Reader.NAME_], readerArray[Reader.PHONE_]);
            if(convertInt(readerArray[Reader.BOOK_COUNT_], Code.READER_DOESNT_HAVE_BOOK_ERROR) > 0){
                for(int k = Reader.BOOK_START_; k < readerArray.length-1; k = k+2){
                    Book book = getBookByISBN(readerArray[k]);
                    if(reader.addBook(book) != Code.SUCCESS){
                        System.out.println("ERROR");
                    }
                    book.setDueDate(convertDate(readerArray[k+1]));
                    checkOutBook(reader, book);
                }
            }
            addReader(reader);
        }
        return Code.SUCCESS;
    }

    /**
     * adds a book to the book list
     * @param newBook as a Book is the book being added
     * @return a code on whether it succeeded or not
     */
    public Code addBook(Book newBook){
        if(books.containsKey(newBook)){
            books.put(newBook, books.get(newBook) + 1);
            System.out.println(books.get(newBook) + " copies of " + newBook.getTitle() + " in the stacks");
        } else {
            books.put(newBook, 1);
            System.out.println(newBook.getTitle() + " added to the stacks.");
        }

        if(shelves.containsKey(newBook.getSubject())){
            shelves.put(newBook.getSubject(),getShelf(newBook.getSubject()));
            return Code.SUCCESS;
        } else {
            System.out.println("No shelf for " + newBook.getSubject() + " books");
            return Code.SHELF_EXISTS_ERROR;
        }
    }

    /**
     * Method to add shelf with only a subject
     * @param shelfSubject as a string is the subject
     * @return a code
     */
    public Code addShelf(String shelfSubject){
        Shelf shelf = new Shelf(-1, shelfSubject);
        return addShelf(shelf);
    }

    /**
     * method to add a shelf given a full shelf
     * @param shelf as a shelf that is added to shelves
     * @return a code
     */
    public Code addShelf(Shelf shelf){
        if(shelves.containsKey(shelf.getSubject())){
            System.out.println("ERROR: Shelf already exists " + shelf);
            return Code.SHELF_EXISTS_ERROR;
        }
        shelf.setShelfNumber(shelves.size() + 1);
        shelves.put(shelf.getSubject(), shelf);
        for(Book b : books.keySet()){
            if(b.getSubject().equals(shelf.getSubject())){
                shelf.addBook(b);
            }
        }
        return Code.SUCCESS;
    }

    /**
     * adds a Reader to readers
     * @param reader as a Reader to be added to the List
     * @return a code
     */
    public Code addReader(Reader reader){
        if(readers.contains(reader)){
            System.out.println(reader.getName() + " already has an account!");
            return Code.READER_ALREADY_EXISTS_ERROR;
        }
        for (Reader value : readers) {
            if (value.getCardNumber() == reader.getCardNumber()) {
                System.out.println(reader.getName() + " and " + value.getName() + " have the same card number!");
                return Code.READER_CARD_NUMBER_ERROR;
            }
        }
        readers.add(reader);
        if(reader.getCardNumber() > getLibraryCardNumber()){
            libraryCard = reader.getCardNumber();
        }
        return Code.SUCCESS;
    }

    /**
     * checks if reader can return book then returns book
     * @param reader as a Reader is the one returning the book
     * @param book as a Book is the book being returned
     * @return a code
     */
    public Code returnBook(Reader reader, Book book){
        if(!reader.hasBook(book)){
            System.out.println(reader.getName() + " doesn't have " + book.getTitle() + " checked out");
            return Code.READER_DOESNT_HAVE_BOOK_ERROR;
        }
        if(!books.containsKey(book)){
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        System.out.println(reader.getName() + " is returning " + book);
        Code check = reader.removeBook(book);
        if(check == Code.SUCCESS){
            return returnBook(book);
        } else {
            System.out.println("Could not return " + book);
            return check;
        }
    }

    /**
     * returns the book
     * @param book as Book is the book being returned
     * @return a code
     */
    public Code returnBook(Book book){
        if(!shelves.containsKey(book.getSubject())){
            System.out.println("No shelf for " + book);
            return Code.SHELF_EXISTS_ERROR;
        }
        return getShelf(book.getSubject()).addBook(book);
    }

    /**
     * removes a reader from the library
     * @param reader as a Reader to be removed
     * @return a code
     */
    public Code removeReader(Reader reader){
        if(!readers.contains(reader)){
            System.out.println(reader.getName() + " is not part of this Library");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }
        if(readers.contains(reader) && !reader.getBooks().isEmpty()){
            System.out.println(reader.getName() + " must return all books!");
            return Code.READER_STILL_HAS_BOOKS_ERROR;
        }
        readers.remove(reader);
        return Code.SUCCESS;
    }

    /**
     * checks out a book to a reader
     * @param reader as Reader is the person checking out a book
     * @param book as Book is the book being checked out
     * @return a code
     */
    public Code checkOutBook(Reader reader, Book book){
        //is there a reader?
        if(!readers.contains(reader)){
            System.out.println(reader.getName() + " doesn't have an account here");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }
        //have they exceeded the lending limit?
        if(reader.getBookCount() > LENDING_LIMIT){
            System.out.println(reader.getName() + " has reached the lending limit, (" + LENDING_LIMIT + ")");
            return Code.BOOK_LIMIT_REACHED_ERROR;
        }
        //does the book exist?
        if(!books.containsKey(book)){
            System.out.println("ERROR: could not find " + book);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }
        //is there a shelf for the book?
        if(!shelves.containsKey(book.getSubject())){
            System.out.println("No shelve for " + book.getSubject() + " books!");
            return Code.SHELF_EXISTS_ERROR;
        }
        //are there copies?
        if(getShelf(book.getSubject()).getBookCount(book) < 1){
            System.out.println("ERROR: no copies of " + book.getTitle() + " remain");
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        Code returned = reader.addBook(book);
        if(returned != Code.SUCCESS){
            System.out.println("Couldn't checkout " + book);
            return returned;
        }
        returned = getShelf(book.getSubject()).removeBook(book);
        if(returned == Code.SUCCESS){
            System.out.println(book + " checked out successfully");
        }
        return returned;
    }

    /**
     * returns the book with the given isbn
     * @param isbn as a string used to find a book
     * @return a book with the isbn or null if not found
     */
    public Book getBookByISBN(String isbn){
        for(Book b : books.keySet()){
            if(b.getISBN().equals(isbn)){
                return b;
            }
        }
        System.out.println("ERROR: Could not find a book with isbn: " + isbn);
        return null;
    }

    /**
     * returns the reader matching with their card number
     * @param cardNumber as an int used to find the reader
     * @return a reader matching the card number
     */
    public Reader getReaderByCard(int cardNumber){
        for(Reader r : readers){
            if(r.getCardNumber() == cardNumber){
                return r;
            }
        }
        System.out.println("Could not find a reader with card #" + cardNumber);
        return null;
    }

    /**
     * returns a shelf given the shelfNumber
     * @param shelfNumber as an Integer used to find a shelf
     * @return the shelf with the shelfNumber
     */
    public Shelf getShelf(Integer shelfNumber){
        for(Shelf s : shelves.values()){
            if(s.getShelfNumber() == shelfNumber){
                return s;
            }
        }
        System.out.println("No shelf number " + shelfNumber + " found");
        return null;
    }

    /**
     * returns a shelf given the subject
     * @param subject as a String used to find a shelf
     * @return the shelf with the subject
     */
    public Shelf getShelf(String subject){
        for(String s : shelves.keySet()){
            if(s.equals(subject)){
                return shelves.get(s);
            }
        }
        System.out.println("No shelf for " + subject + " books");
        return null;
    }

    /**
     * prints out all the books in the library
     * @return an int of the number of books
     */
    public int listBooks(){
        int count = 0;
        for(Book b : books.keySet()){
            System.out.println(books.get(b) + " copies of " + b);
            count += books.get(b);
        }
        return count;
    }

    /**
     * lists the shelves themselves
     * @return the number of shelves as an int
     */
    public int listShelves(){
        return listShelves(false);
    }

    /**
     * lists the shelves or the books in the shelves
     * @param showBooks as a boolean: tells if the books are shown
     * @return the number of shelves as an int
     */
    public int listShelves(boolean showBooks){
        int count = 0;
        if(showBooks){
            for(Shelf s : shelves.values()){
                System.out.println(s.listBooks());
                count++;
            }
        } else {
            for(Shelf s : shelves.values()){
                System.out.println(s);
                count++;
            }
        }
        return count;
    }

    /**
     * lists the readers themselves
     * @return the number of readers
     */
    public int listReaders(){
        return listReaders(false);
    }

    /**
     * lists the readers or the readers and their books
     * @param showBooks as a boolean to determine if the books are shown
     * @return the number of readers
     */
    public int listReaders(boolean showBooks){
        int count = 0;
        if(showBooks){
            for(Reader r : readers){
                System.out.println(r.getName() + " (#" + r.getCardNumber() + ") has the following books: \n" + r.getBooks().toString().replace("[", "{").replace("]", "}"));
                count++;
            }
        } else {
            for(Reader r : readers){
                System.out.println(r);
                count++;
            }
        }
        return count;
    }

    /**
     * converts a string into an integer
     * @param recordCountString as a string to be converted
     * @param code as Code that determines any error messages
     * @return the string converted into an integer
     */
    public static int convertInt(String recordCountString, Code code){
        try{
            return Integer.parseInt(recordCountString);
        }catch(NumberFormatException e) {
            System.out.println("Value which caused the error: " + recordCountString);
            return switch (code) {
                case BOOK_COUNT_ERROR -> {
                    System.out.println("Error: Could not read number of books");
                    yield -2;
                }
                case PAGE_COUNT_ERROR -> {
                    System.out.println("Error: Could not parse page count");
                    yield -8;
                }
                case DATE_CONVERSION_ERROR -> {
                    System.out.println("Error: Could not parse date component");
                    yield -101;
                }
                case SHELF_COUNT_ERROR -> {
                    System.out.println("Error: Could not parse shelf count");
                    yield -6;
                }
                case SHELF_NUMBER_PARSE_ERROR -> {
                    System.out.println("Error: Could not parse shelf component");
                    yield -61;
                }
                default -> {
                    System.out.println("Error: Unknown conversion error");
                    yield -999;
                }
            };
        }
    }

    /**
     * converts a string into an object LocalDate
     * @param date as a String is the data to be converted
     * @return a LocalDate of a string converted into LocalDate
     */
    public static LocalDate convertDate(String date){
        if(date.equals("0000")){
            return LocalDate.of(1970, 1, 1);
        }
        String[] parseDate = date.split("-");
        if(Integer.parseInt(parseDate[0]) < 0){
            System.out.println("Error converting date: Year " + parseDate[0]);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        } else if (Integer.parseInt(parseDate[1]) < 0){
            System.out.println("Error converting date: Month " + parseDate[1]);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        } else if (Integer.parseInt(parseDate[2]) < 0){
            System.out.println("Error converting date: Day " + parseDate[2]);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        }
        try{
            return LocalDate.parse(date);
        } catch(DateTimeException e){
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        }
    }

    public static int getLibraryCardNumber(){
        return libraryCard + 1;
    }

    public String getName(){
        return name;
    }

    /**
     * a method that provides the code with the corresponding error number
     * @param codeNumber as an int to change into error
     * @return the code that matches the number
     */
    private Code errorCode(int codeNumber){
        for(Code code : Code.values()){
            if(code.getCode() == codeNumber){
                return code;
            }
        }
        return Code.UNKNOWN_ERROR;
    }


}
