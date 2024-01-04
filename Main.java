import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

// Hashmaps for books and members
public class Main {
    static HashMap<Integer, Member> members = new HashMap<Integer, Member>();
    static HashMap<Integer, Book> books = new HashMap<Integer, Book>();

    // Shutdown hook to save changes in data before it ends (found this on stack overflow)
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    saveFiles();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Reads books data from csv file
        String file = "books.csv";
        BufferedReader reader = null;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                // Splits a comma seperated string while ignoring the commas in quotes (found this on stack overflow)
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Queue<Integer> wait = new LinkedList<Integer>();
                for (int i = 4; i < row.length; i++) {
                    wait.add(Integer.parseInt(row[i]));
                }
                Book book = new Book(Integer.parseInt(row[0]), row[1].replace("\"", ""), row[2].replace("\"", ""), Integer.parseInt(row[3]), wait);
                books.put(book.id, book);
            }
        } catch (Exception e) {

        // Handles possible exceptions during file reading
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        // reads members data from csv file (stack overflow)
        file = "communityMembers.csv";
        reader = null;
        line = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                // Splits a comma seperated string while ignoring the commas in quotes (found this on stack overflow)
                String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                int[] borrowed = new int[5];
                int borrowedBooks = 0;
                for (int i = 3; i < row.length; i++) {
                    borrowed[i-3] = Integer.parseInt(row[i]);
                    if (!(row[i].isEmpty())) {
                        borrowedBooks += 1;
                    }
                }
                Member member = new Member(Integer.parseInt(row[0]), row[1], row[2], borrowed, borrowedBooks);
                members.put(member.id, member);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // User input/interaction
        Scanner myObj = new Scanner(System.in);
        while (true) {
            System.out.println("Hello Librarian. What would you like to do? (H FOR COMMANDS)");
            String c = myObj.nextLine();
            // If A is pressed, the following prompts are given
            if (c.equals("A")) {
                // Adding book
                System.out.println("Enter the ID");
                String id = myObj.nextLine();
                System.out.println("Enter the title");
                String title = myObj.nextLine();
                System.out.println("Enter the author");
                String author = myObj.nextLine();
                System.out.println("How many copies");
                String copies = myObj.nextLine();
                addBook(id, title, author, copies);

            }
            // If B is pressed, the following prompts are given
            else if (c.equals("B")) {
                // Adding member
                System.out.println("Enter the ID");
                String id = myObj.nextLine();
                System.out.println("Enter the first name");
                String firstName = myObj.nextLine();
                System.out.println("Enter the last name");
                String lastName = myObj.nextLine();
                addMember(id, firstName, lastName);

            }
            // If C is pressed, the following prompts are given
            else if (c.equals("C")) {
                // Removing book
                System.out.println("Enter the ID to remove the book");
                String id = myObj.nextLine();
                Book book = getBook(id);
                if (book != null) {
                    System.out.println("Removing " + book.title + " from the library");
                    books.remove(book.id);

                } else {
                    System.out.println("Removing cancelled");
                }

            }
            // If D is pressed, the following prompts are given
            else if (c.equals("D")) {
                // Removing member
                System.out.println("Enter the ID to remove the member");
                String id = myObj.nextLine();
                Member member = getMember(id);
                if (member != null) {
                    System.out.println("Removing " + member.first + " " + member.last + " from the library");
                    members.remove(member.id);
                } else {
                    System.out.println("Removing cancelled");
                }

            }
            // If E is pressed, the following prompts are given
            else if (c.equals("E")) {
                // Loaning book to member
                System.out.println("Enter the ID for the member to loan the book to");
                String id = myObj.nextLine();
                Member member = getMember(id);
                if (member != null) {
                    if (member.borrowedBooks < 5) {
                        System.out.println("Enter the ID for the book to loan");
                        String bookID = myObj.nextLine();
                        Book book = getBook(bookID);
                        if (book != null) {
                            loanBook(member,book);
                        } else {
                            System.out.println("Cancelling loan");
                        }
                    } else {
                        System.out.println("Too many books already, please return one");
                    }
                } else {
                    System.out.println("Cancelling loan");
                }

            }
            // If F is pressed, the following prompts are given
            else if (c.equals("F")) {
                // Book returned from member
                System.out.println("Enter the ID for the member who wants to return a book");
                String id = myObj.nextLine();
                Member member = getMember(id);
                if (member != null) {
                    if (member.borrowedBooks > 0) {
                        System.out.println("Borrowed books: ");
                        for (int i = 0; i < member.borrowed.length; i++) {
                            if (member.borrowed[i] != 0) {
                                System.out.println(member.borrowed[i]);
                            }
                        }
                        System.out.println("What book would you like to return");
                        String bookID = myObj.nextLine();
                        Book book = getBook(bookID);
                        if (book != null) {
                            boolean found = false;
                            for (int i = 0; i < member.borrowed.length; i++) {
                                if (member.borrowed[i] == book.id && !found) {
                                    member.borrowed[i] = 0;
                                    member.borrowedBooks -= 1;
                                    System.out.println(book.title + "  returned");
                                    returnBook(book);

                                    found = true;
                                }
                            }
                        } else {
                            System.out.println("Book doesn't exist cannot return it");
                        }

                    } else {
                        System.out.println("No books to return");
                    }
                } else {
                    System.out.println("Cancelling return");
                }
            }
            // If G is pressed, the following prompts are given
            else if (c.equals("G")) {
                // Displaying book availability
                System.out.println("Enter the ID for book info");
                String id = myObj.nextLine();
                Book book = getBook(id);
                if (book != null) {
                    System.out.println(book.title + " has " + book.copies + " copies available");
                } else {
                    System.out.println("Cancelling getting info");
                }


            }
            // If H is pressed, commands are displayed
            else if (c.equals("H")) {
                displayCommands();
            }
            else {
                System.out.println("Enter a valid command, capital letters please");
            }
        }





    }
    // Function for returning book, updating and waitlist
    public static void returnBook(Book book) {
        book.copies += 1;
        boolean found = false;
        while (!book.wait.isEmpty() && !found) {
            Member member = members.get(book.wait.remove());
            if (member.borrowedBooks < 5) {
                found = true;
                loanBook(member, book);
            }
        }

    }
    // Function for loaning book to member(s)
    public static void loanBook(Member member, Book book) {
        if (book.copies > 0) {
            book.copies -= 1;
            member.borrowedBooks += 1;
            for (int i = 0; i < 5; i ++) {
                if (member.borrowed[i] == 0) {
                    member.borrowed[i] = book.id;
                    break;
                }
            }
            System.out.println(book.title + " loaned to " + member.first + " " + member.last);
        } else {
            book.wait.add(member.id);
            System.out.println(member.first + " " + member.last + " added to waitlist for " + book.title);
        }
    }
    // Main menu
    public static void displayCommands() {
        System.out.println("A - ADD BOOK");
        System.out.println("B - ADD MEMBER");
        System.out.println("C - REMOVE BOOK");
        System.out.println("D - REMOVE MEMBER");
        System.out.println("E - LOAN BOOK");
        System.out.println("F - RETURN BOOK");
        System.out.println("G - BOOK AVAILABILITY");
        System.out.println("H - COMMANDS");
    }
    // Try and catch for adding book option, in which if the case does not work it gives an error message
    public static void addBook(String id, String title, String author, String copies) {
        try {
            Book book = new Book(Integer.parseInt(id), title, author, Integer.parseInt(copies), new LinkedList<Integer>());
            System.out.println("Adding " + book.title + " to the library");
            books.put(book.id, book);
        } catch (Exception e){
            System.out.println("Adding book failed, please enter integers for ID and copies");
        }
    }
    // Function for adding member(s), error handling if string values not inputted
    public static void addMember(String id, String first, String last) {
        try {
            Member member = new Member(Integer.parseInt(id), first, last, new int[5], 0);
            System.out.println("Adding " + member.first + " " + member.last + " to the library");
            members.put(member.id, member);
        } catch (Exception e){
            System.out.println("Adding member failed, please enter an integer for ID");
        }
    }
    // Function that retrieves book based on id, handles invalid inputs/book unavailabilty
    public static Book getBook(String input) {
        try {
            int ID = Integer.parseInt(input);
            if (books.get(ID) == null) {
                System.out.println("Book not found in library");
                return null;
            } else {
                return books.get(ID);
            }
        } catch (Exception e) {
            System.out.println("Invalid input, please enter an integer for the ID");
            return null;
        }

    }
    // Retrieves member based on ID, handles bad inputs/member not existing
    public static Member getMember(String input) {
        try {
            int ID = Integer.parseInt(input);
            if (members.get(ID) == null) {
                System.out.println("Member not found in library");
                return null;
            } else {
                return members.get(ID);
            }
        } catch (Exception e) {
            System.out.println("Invalid input, please enter an integer for the ID");
            return null;
        }

    }
    // Function that saves data files for books.csv & communityMembers.csv
    public static void saveFiles() throws IOException {
        // Deletes existing books.csv file
        new File("books.csv").delete();
        // FileWriter that writes data for books.csv
        FileWriter w = new FileWriter("books.csv");
        // Format for inputted data to books.csv
        w.append("bookID").append(",").append("title").append(",").append("author").append(",").append("available");
        w.append("\n");
        // Iterates through books hashmap
        for (int s : books.keySet()) {
            Book book = books.get(s);
            // Writing book data to books.csv
            w.append(Integer.toString(book.id));
            w.append(",");
            w.append("\"" + book.title + "\"");
            w.append(",");
            w.append("\"" + book.author + "\"");
            w.append(",");
            w.append(Integer.toString(book.copies));
            // Writing waitlist
            while (!book.wait.isEmpty()) {
                w.append(",");
                w.append(Integer.toString(book.wait.remove()));
            }
            w.append("\n");



        }
        // Flushes and closes filwWriter for books.csv
        w.flush();
        w.close();
        // Same thing but for communityMembers.csv, deletes existing files
        new File("communityMembers.csv").delete();
        // FileWriter to rewrite
        FileWriter j = new FileWriter("communityMembers.csv");
        // Format for fileWriter
        j.append("ID").append(",").append("First").append(",").append("Last");
        j.append("\n");
        // Iterates through members hashmap and getting member
        for (int s : members.keySet()) {
            Member mem = members.get(s);
            // Writing member data in file
            j.append(Integer.toString(mem.id));
            j.append(",");
            j.append(mem.first);
            j.append(",");
            j.append(mem.last);
            // Writing borrowed books data
            for (int i = 0; i < 5; i++) {
                if (mem.borrowed[i] != 0) {
                    j.append(",");
                    j.append(Integer.toString(mem.borrowed[i]));
                }
            }
            j.append("\n");



        }
        // Flushes and closes fileWriter for communityMembers.csv
        j.flush();
        j.close();
        // only gives error when run in cancelled and no changes are made
        // or no valid commands are given

    }



}