package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Pair;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanSolution;

public class ComputeValueRepresentationOfSolution extends AComputeValueRepresentation<BooleanSolution, ValueSolution> {
    public ComputeValueRepresentationOfSolution(IComputation<BooleanSolution> booleanRepresentation, IComputation<VariableMap> variableMap) {
        super(booleanRepresentation, variableMap);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeValueRepresentationOfSolution(getInput(), getVariableMap());
    }
}