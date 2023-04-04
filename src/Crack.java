import org.apache.commons.codec.digest.Crypt;

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
// the crack method got me good, had to do a lot of troubleshooting and research to make it work lol
    public void crack() throws FileNotFoundException {
        User[] users = Crack.parseShadow(dictionary);
        FileInputStream inputStream = new FileInputStream(dictionary);
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()){
            String word = scanner.nextLine();

            for (User user : users) {
                if (user != null && user.getPassHash().contains("$")){
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
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
       int numLines = getLineCount("resources/shadow");
       User[] users = new User[numLines];
       FileInputStream inputStream = new FileInputStream("resources/shadow");
       Scanner scanner = new Scanner(inputStream);

       int i = 0;
       while (scanner.hasNextLine()){
           String line = scanner.nextLine();
           String[] parts = line.split(":");
           if (parts.length >= 2 && parts[1].contains("$")){
               users[i] = new User(parts[0], parts[1]);
               i++;
           }
       }
       scanner.close();
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
}
