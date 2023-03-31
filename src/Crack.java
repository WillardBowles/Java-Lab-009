/**
 *
 *
 * @author Trevor Hartman
 * @author Willard Bowles
 *
 * @since Version 1.0
 */



import org.apache.commons.codec.digest.Crypt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(this.dictionary);
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine().trim();
            for (User user : users) {
                if (user.getPassHash().contains("$")) {
                    String hash = Crypt.crypt(word, user.getPassHash());
                    if (hash.equals(user.getPassHash())) {
                        System.out.println("Found password " + word + " for user " + user.getUsername() + ".");
                    }
                }
            }
        }
    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                lineCount++;
            }
        } catch (FileNotFoundException ignored) {
        }
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
        User[] users = new User[getLineCount(shadowFile)];
        FileInputStream fis = new FileInputStream(shadowFile);
        Scanner scanner = new Scanner(fis);
        int i = 0;
        while (scanner.hasNextLine()) {
            String[] elements = scanner.nextLine().split(":");
            User user = new User(elements[0], elements[1]);
            users[i] = user;
            i++;
        }
        return users;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }

    public static class User {
        private final String username;
        private final String passHash;

        public User(String username, String passHash) {
            this.username = username;
            this.passHash = passHash;
        }

        public String getUsername() {
            return username;
        }

        public String getPassHash() {
            return passHash;
        }
    }
}