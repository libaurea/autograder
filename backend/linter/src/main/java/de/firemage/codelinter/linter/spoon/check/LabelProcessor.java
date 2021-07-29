package de.firemage.codelinter.linter.spoon.check;

import de.firemage.codelinter.linter.spoon.InCodeProblem;
import de.firemage.codelinter.linter.spoon.ProblemCategory;
import de.firemage.codelinter.linter.spoon.ProblemLogger;
import spoon.reflect.code.CtLabelledFlowBreak;

public class LabelProcessor extends AbstractLoggingProcessor<CtLabelledFlowBreak> {
    private static final String DESCRIPTION = "Used labels";
    private static final String EXPLANATION = """
            Labels enable a GOTO programming style in Java.
            Many people have written about the problems of GOTO, so PLEASE don't use labels.
            If you are still not convinced, google 'go to statements considered harmful'""";

    public LabelProcessor(ProblemLogger logger) {
        super(logger);
    }

    @Override
    public void process(CtLabelledFlowBreak element) {
        if (element.getTargetLabel() != null) {
            addProblem(new InCodeProblem(element, DESCRIPTION, ProblemCategory.CONTROL_FLOW, EXPLANATION));
        }
    }
}
