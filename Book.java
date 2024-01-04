import java.util.LinkedList;
import java.util.Queue;
// publc class + variables
public class Book {
    int id;
    String title;
    String author;
    int copies;
    // Queue for waitlist
    Queue<Integer> wait = new LinkedList<Integer>();
    // constructor
    public Book(int id, String title, String author, int copies, Queue<Integer> wait) {
        this.id=id;
        this.title=title;
        this.author=author;
        this.copies=copies;
        this.wait=wait;
    }
}