package live.rehope.site.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * A random name generator for first names.
 */
public final class RandomNameGenerator {
    private static final String VERB_LIST_FILE = "verbs.txt";
    private static final String NOUN_LIST_FILE = "nouns.txt";

    private static final Random random = new Random();

    private static final List<String> nouns = loadWords(VERB_LIST_FILE);
    private static final List<String> verbs = loadWords(NOUN_LIST_FILE);

    @NotNull
    public static String getRandomName() {
        return getRandomWord(nouns) + getRandomWord(verbs) + getRandomNumber();
    }

    private static List<String> loadWords(String file) {
        List<String> loaded = null;
        try {
            Path filePath = Paths.get(file);
            loaded = Files.readAllLines(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loaded;
    }

    private static String getRandomWord(List<String> words) {
        int randomIndex = random.nextInt(words.size());
        return words.get(randomIndex);
    }

    private static int getRandomNumber() {
        return random.nextInt(1000, 9999);
    }

}
