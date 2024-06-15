package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author Zhang, Xiaochen
 * The structure of a Capers Repository is as follows:
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all the persistent data for dogs
 *    - story -- file containing the current story
 */
public class CapersRepository extends IOException{
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join("capers", ".capers");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        try {
            File createMain = new File("capers", ".capers");
            if (!createMain.exists()) {
                createMain.mkdir();
            }

            createMain = Utils.join("capers", ".capers", "dogs");
            if (!createMain.exists()) {
                createMain.mkdir();
            }

            createMain = Utils.join("capers", ".capers", "story");
            if (!createMain.exists()) {
                createMain.createNewFile();
            }
        } catch (IOException excp) {
            System.out.println(excp);
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File outFile = Utils.join(CAPERS_FOLDER, "story");
        String originalText = readContentsAsString(outFile);
        String outText = originalText + text + "\n";
        Utils.writeContents(outFile, outText);
        System.out.println(outText);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog dog = Dog.fromFile(name);
        dog.haveBirthday();
        dog.saveDog();
    }
}
