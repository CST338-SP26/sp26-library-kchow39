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
//        libraryCard = 0;
//        books = new HashMap<>();
//        readers = new ArrayList<>();
//        shelves = new HashMap<>();
    }

    public Code init(String filename){
        try{
            File f = new File(filename);
            Scanner fs = new Scanner(f);



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
        String[] bookArray = scan.nextLine().split(",");
        if(convertDate(bookArray[Book.DUE_DATE_], null) == null){
            return Code.DATE_CONVERSION_ERROR;
        }
        if(convertInt(bookArray[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR) <= 0){
            return Code.PAGE_COUNT_ERROR;
        }
        for(int i = 0; i < bookCount; i++){
            try {
                Book book = new Book(bookArray[Book.ISBN_], bookArray[Book.TITLE_], bookArray[Book.SUBJECT_], convertInt(bookArray[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR), bookArray[Book.AUTHOR_], convertDate(bookArray[Book.DUE_DATE_], null));
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
        String[] shelfArray = scan.nextLine().split(",");
        if(convertInt(shelfArray[Shelf.SHELF_NUMBER_], Code.SHELF_COUNT_ERROR) <= 0){
            return Code.SHELF_COUNT_ERROR;
        }
        for(int i = 0; i < shelfCount; i++){
            try{
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

    private Code initReader(int readerCount, Scanner scan){
        if(readerCount < 1){
            return Code.READER_COUNT_ERROR;
        }


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
            shelves.put(newBook.getSubject(), );
            //TODO: getShelf for this
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
     * converts a string into an integer
     * @param recordCountString as a string to be converted
     * @param code as Code that determines any error messages
     * @return the string converted into an integer
     */
    public static int convertInt(String recordCountString, Code code){
        try{
            return Integer.getInteger(recordCountString);
        }catch(NumberFormatException e) {
            System.out.println("Value which caused the error: " + recordCountString);
            switch (code) {
                case Code.BOOK_COUNT_ERROR:
                    System.out.println("Error: Could not read number of books");
                    return -2;
                case Code.PAGE_COUNT_ERROR:
                    System.out.println("Error: Could not parse page count");
                    return -8;
                case Code.DATE_CONVERSION_ERROR:
                    System.out.println("Error: Could not parse date component");
                    return -101;
                default:
                    System.out.println("Error: Unknown conversion error");
                    return -999;
            }
        }
    }

    /**
     * converts a string into an object LocalDate
     * @param date as a String is the data to be converted
     * @param errorCode as Code is unused
     * @return a LocalDate of a string converted into LocalDate
     */
    public static LocalDate convertDate(String date, Code errorCode){
        if(date.equals("0000")){
            return LocalDate.of(1970, 1, 1);
        }
        String[] parseDate = date.split("-");
        if(Integer.getInteger(parseDate[0]) < 0){
            System.out.println("Error converting date: Year " + parseDate[0]);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        } else if (Integer.getInteger(parseDate[1]) < 0){
            System.out.println("Error converting date: Month " + parseDate[1]);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        } else if (Integer.getInteger(parseDate[2]) < 0){
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
