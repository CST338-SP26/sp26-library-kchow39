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

    private Code initBooks(int bookCount, Scanner scan){
        if(bookCount < 1){
            return Code.LIBRARY_ERROR;
        }
        String[] bookArray = scan.nextLine().split(",");
        if(convertDate(bookArray[5], null) == null){
            return Code.DATE_CONVERSION_ERROR;
        }
        if(convertInt(bookArray[3], Code.PAGE_COUNT_ERROR) <= 0){
            return Code.PAGE_COUNT_ERROR;
        }
        for(int i = 0; i < bookCount; i++){
            try {
                Book book = new Book(bookArray[0], bookArray[1], bookArray[2], convertInt(bookArray[3], Code.PAGE_COUNT_ERROR), bookArray[4], convertDate(bookArray[5], null));
            }catch(IndexOutOfBoundsException e){
                return Code.BOOK_RECORD_COUNT_ERROR;
            }
        }
        return null;
    }

    private Code initShelves(int shelfCount, Scanner scan){

    }

    private Code initReader(int readerCount, Scanner scan){

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
