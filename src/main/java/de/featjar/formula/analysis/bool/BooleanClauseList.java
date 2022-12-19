package de.featjar.formula.analysis.bool;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueClauseList;
import de.featjar.formula.transformer.ComputeCNFFormula;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of Boolean clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link de.featjar.formula.structure.formula.Formula} in CNF (e.g., computed with
 * {@link ComputeCNFFormula}), a {@link ValueClauseList} is a more low-level representation.
 * A Boolean clause list only contains indices into a {@link VariableMap}, which links
 * a {@link BooleanClauseList} to the {@link de.featjar.formula.structure.term.value.Variable variables}
 * in the original {@link de.featjar.formula.structure.formula.Formula}.
 * TODO: more error checking for consistency of clauses with variables
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanClauseList extends BooleanAssignmentList<BooleanClauseList, BooleanClause> {
    public BooleanClauseList() {
    }

    public BooleanClauseList(int size) {
        super(size);
    }

    public BooleanClauseList(Collection<? extends BooleanClause> clauses) {
        super(clauses);
    }

    public BooleanClauseList(BooleanClauseList other) {
        super(other);
    }

    @Override
    protected BooleanClauseList newAssignmentList(List<BooleanClause> clauses) {
        return new BooleanClauseList(clauses);
    }

    @Override
    public BooleanClauseList toClauseList() {
        return new BooleanClauseList(assignments);
    }

    @Override
    public BooleanSolutionList toSolutionList() {
        return new BooleanSolutionList(assignments.stream().map(BooleanClause::toSolution).collect(Collectors.toList()));
    }

    @Override
    public Result<ValueClauseList> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @Override
    public Computation<ValueClauseList> toValue(Computation<VariableMap> variableMap) {
        return variableMap.mapResult(v -> toValue(v).get());
    }

    public String print() {
        return VariableMap.toAnonymousValue(this).getAndLogProblems().print();
    }

    @Override
    public String toString() {
        return String.format("BooleanClauseList[%s]", print());
    }
}