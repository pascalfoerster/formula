package de.featjar.formula.transformer;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.analysis.bool.ToLiteralClauseList;
import de.featjar.formula.structure.formula.Formula;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {
    public static void traverseAndAssertSameFormula(Formula oldFormula, Function<Computation<Formula>, Computation<Formula>> formulaComputationFunction) {
        Result<Formula> result = formulaComputationFunction.apply(Computation.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertEquals(oldFormula, result.get());
    }

    public static void traverseAndAssertFormulaEquals(Formula oldFormula, Function<Computation<Formula>, Computation<Formula>> formulaComputationFunction, Formula assertFormula) {
        Result<Formula> result = formulaComputationFunction.apply(Computation.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertNotEquals(oldFormula, result.get());
        assertEquals(assertFormula, result.get());
    }
}