import gateway.IWriter;
import gateway.Writer;
import useCases.*;
import gateway.IReader;
import gateway.Reader;

public class NewApp {
    public static void main(String[] args) {
        final String userDataFileDirectory = "data/userData.txt";
        final String postDataFileDirectory = "data/postData.txt";
        final String commentDataFileDirectory = "data/commentData.txt";
        IReader reader1 = new Reader(userDataFileDirectory);
        IReader reader2 = new Reader(postDataFileDirectory);
        IReader reader3 = new Reader(commentDataFileDirectory);
        IWriter writer1 = new Writer(userDataFileDirectory);
        IWriter writer2 = new Writer(postDataFileDirectory);
        IWriter writer3 = new Writer(commentDataFileDirectory);


        Server server = new Server(new ManagerData(
                new AccountManager(reader1, writer1),
                new PostManager(reader2, writer2),
                new CommentManager(reader3, writer3)));
        server.start();
    }
}