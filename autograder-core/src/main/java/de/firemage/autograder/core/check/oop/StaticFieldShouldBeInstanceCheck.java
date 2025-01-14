package de.firemage.autograder.core.check.oop;

import de.firemage.autograder.core.LocalizedMessage;
import de.firemage.autograder.core.ProblemType;
import de.firemage.autograder.core.check.ExecutableCheck;
import de.firemage.autograder.core.dynamic.DynamicAnalysis;
import de.firemage.autograder.core.integrated.IntegratedCheck;
import de.firemage.autograder.core.integrated.SpoonUtil;
import de.firemage.autograder.core.integrated.StaticAnalysis;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtField;

import java.util.Map;

@ExecutableCheck(reportedProblems = { ProblemType.STATIC_FIELD_SHOULD_BE_INSTANCE })
public class StaticFieldShouldBeInstanceCheck extends IntegratedCheck {
    @Override
    protected void check(StaticAnalysis staticAnalysis, DynamicAnalysis dynamicAnalysis) {
        staticAnalysis.processWith(new AbstractProcessor<CtField<?>>() {
            @Override
            public void process(CtField<?> ctField) {
                if (ctField.isImplicit() || !ctField.getPosition().isValidPosition() || !ctField.isStatic() || ctField.isFinal()) {
                    return;
                }

                // the field is not marked as final, so values can be assigned to it.
                // if the field is assigned multiple times, it should not be static
                if (!SpoonUtil.isEffectivelyFinal(ctField)) {
                    addLocalProblem(
                        ctField,
                        new LocalizedMessage(
                            "static-field-should-be-instance",
                            Map.of("name", ctField.getSimpleName())
                        ),
                        ProblemType.STATIC_FIELD_SHOULD_BE_INSTANCE
                    );
                }
            }
        });
    }
}
