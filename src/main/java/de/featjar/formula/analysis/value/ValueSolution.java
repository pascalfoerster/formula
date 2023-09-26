/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.ISolution;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanSolution;
import java.util.LinkedHashMap;

/**
 * A (partial) value solution; that is, a conjunction of equalities.
 * Often holds output of an SMT {@link ISolver}.
 *
 * @author Elias Kuiter
 */
public class ValueSolution extends AValueAssignment implements ISolution<String, Object> {
    public ValueSolution() {}

    public ValueSolution(LinkedHashMap<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueSolution(ValueClause predicateClause) {
        this(new LinkedHashMap<>(predicateClause.variableValuePairs));
    }

    public ValueSolution(Object... variableValuePairs) {
        super(variableValuePairs);
    }

    @Override
    public Result<BooleanSolution> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanSolution> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanSolution>) super.toBoolean(variableMap);
    }

    @Override
    public String toString() {
        return String.format("ValueSolution[%s]", print());
    }
}
