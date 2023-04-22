package de.firemage.autograder.core.check.oop;

import de.firemage.autograder.core.LocalizedMessage;
import de.firemage.autograder.core.ProblemType;
import de.firemage.autograder.core.check.ExecutableCheck;
import de.firemage.autograder.core.dynamic.DynamicAnalysis;
import de.firemage.autograder.core.integrated.IntegratedCheck;
import de.firemage.autograder.core.integrated.SpoonUtil;
import de.firemage.autograder.core.integrated.StaticAnalysis;
import de.firemage.autograder.core.integrated.effects.AssignmentStatement;
import de.firemage.autograder.core.integrated.effects.Effect;
import de.firemage.autograder.core.integrated.effects.TerminalEffect;
import de.firemage.autograder.core.integrated.effects.TerminalStatement;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExecutableCheck(reportedProblems = {ProblemType.SHOULD_BE_ENUM_ATTRIBUTE})
public class ShouldBeEnumAttribute extends IntegratedCheck {
    public ShouldBeEnumAttribute() {
        super(new LocalizedMessage("should-be-enum-attribute"));
    }

    @Override
    protected void check(StaticAnalysis staticAnalysis, DynamicAnalysis dynamicAnalysis) {
        staticAnalysis.processWith(new AbstractProcessor<CtAbstractSwitch<?>>() {
            @Override
            public void process(CtAbstractSwitch<?> ctSwitch) {
                // skip switch statements that are not on an enum
                if (!ctSwitch.getSelector().getType().isEnum()) {
                    return;
                }

                List<Effect> effects = new ArrayList<>();
                for (CtCase<?> ctCase : ctSwitch.getCases()) {
                    List<CtStatement> statements = SpoonUtil.getEffectiveStatements(ctCase);

                    if (statements.size() != 1 && (statements.size() != 2 || !(statements.get(1) instanceof CtBreak))) {
                        return;
                    }

                    Optional<Effect> effect = SpoonUtil.tryMakeEffect(statements.get(0));
                    if (effect.isEmpty()) {
                        return;
                    }

                    Effect resolvedEffect = effect.get();


                    // check for default case, which is allowed to be a terminal effect, even if the other cases are not:
                    if (ctCase.getCaseExpressions().isEmpty() && resolvedEffect instanceof TerminalEffect) {
                        continue;
                    }

                    effects.add(resolvedEffect);
                }

                if (effects.isEmpty()) return;

                Effect firstEffect = effects.get(0);
                for (Effect effect : effects) {
                    if (!firstEffect.isSameEffect(effect)) {
                        return;
                    }

                    Optional<CtExpression<?>> ctExpression = effect.value();
                    if (ctExpression.isEmpty()) {
                        return;
                    }

                    if (!(ctExpression.get() instanceof CtLiteral<?>)) {
                        return;
                    }
                }

                addLocalProblem(
                    ctSwitch,
                    new LocalizedMessage("should-be-enum-attribute"),
                    ProblemType.SHOULD_BE_ENUM_ATTRIBUTE
                );
            }
        });
    }
}
