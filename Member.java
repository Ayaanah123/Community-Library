public class Member {
    // Public class + variables
    int id;
    String first;
    String last;
    int[] borrowed;
    int borrowedBooks = 0;
    // Constructor
    public Member(int id, String first, String last, int[] borrowed, int borrowedBooks) {
        this.id = id;
        this.first=first;
        this.last=last;
        this.borrowed=borrowed;
        this.borrowedBooks=borrowedBooks;
    }
}
