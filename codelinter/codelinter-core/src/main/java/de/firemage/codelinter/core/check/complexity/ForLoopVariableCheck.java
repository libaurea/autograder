package de.firemage.codelinter.core.check.complexity;

import de.firemage.codelinter.core.pmd.PMDCheck;
import java.util.List;

public class ForLoopVariableCheck extends PMDCheck {
    private static final String DESCRIPTION = "for-loops should have exactly one locally declared control variable";

    public ForLoopVariableCheck() {
        super(DESCRIPTION, List.of(
                createXPathRule("multi variable for", "//ForInit/LocalVariableDeclaration[count(VariableDeclarator) > 1]"),
                createXPathRule("for without variable declaration", "//ForStatement[not(ForInit)]")
        ));
    }
}