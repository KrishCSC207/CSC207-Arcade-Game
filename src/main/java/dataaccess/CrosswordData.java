package dataaccess;

import entity.CrosswordPuzzle;
import java.util.*;

public final class CrosswordData {
    private CrosswordData() {}

    public static final Map<String, List<CrosswordPuzzle>> DB;
    static {
        Map<String, List<CrosswordPuzzle>> m = new HashMap<>();

        m.put("EASY", List.of(
                new CrosswordPuzzle(
                        "easy",
                        "images/crosswords/easy/easy_wordsearch.png",
                        List.of("Adapter","String","Integer","Regex"))
        ));

        m.put("MEDIUM", List.of(
                new CrosswordPuzzle(
                        "medium",
                        "images/crosswords/medium/medium_wordsearch.png",
                        List.of("Compiler","Machine","Reference","Equality","Pattern","Ethics","Principle"))
        ));

        m.put("HARD", List.of(
                new CrosswordPuzzle(
                        "hard",
                        "images/crosswords/hard/hard_wordsearch.png",
                        List.of("Boolean","Entity","Interactor","Relational","Controller","Environment","Interface",
                                "Substitution","Dependency","Expression","Inversion","Variable"))
        ));

        DB = Collections.unmodifiableMap(m);
    }
}
