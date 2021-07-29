package de.firemage.codelinter.linter.spoon.check;

import de.firemage.codelinter.linter.spoon.InCodeProblem;
import de.firemage.codelinter.linter.spoon.ProblemCategory;
import de.firemage.codelinter.linter.spoon.ProblemLogger;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class AbstractClassWithoutChildCheck extends AbstractLoggingProcessor<CtClass<?>> {
    public static final String DESCRIPTION = "Abstract class is never implemented";
    public static final String EXPLANATION = """
            There's no reason to make a class abstract if you never implement it.
            If you want to prevent instantiating the class, create a private constructor.""";

    public AbstractClassWithoutChildCheck(ProblemLogger logger) {
        super(logger);
    }

    @Override
    public void process(CtClass<?> element) {
        if (element.isAbstract() && !hasChildren(element)) {
            addProblem(new InCodeProblem(element, DESCRIPTION, ProblemCategory.OOP, EXPLANATION));
        }
    }

    private boolean hasChildren(CtClass<?> element) {
        return Query.getElements(getFactory(), new TypeFilter<>(CtClass.class))
                .stream()
                .anyMatch(c -> element.getReference().equals(c.getSuperclass()));
    }
}
