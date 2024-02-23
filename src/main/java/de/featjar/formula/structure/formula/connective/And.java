/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
package de.featjar.formula.structure.formula.connective;

import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.formula.IFormula;
import java.util.List;

/**
 * Expresses "A and B" constraints (i.e., conjunction).
 * Evaluates to {@code true} iff all of its children evaluate to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class And extends ANonTerminalExpression implements IConnective {
    protected And() {}

    public And(IFormula... formulas) {
        super(formulas);
    }

    public And(List<? extends IFormula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "and";
    }

    @Override
    public Object evaluate(List<?> values) {
        if (values.stream().anyMatch(v -> Boolean.FALSE.equals(v))) {
            return Boolean.FALSE;
        }
        return values.stream().filter(v -> Boolean.TRUE.equals(v)).count() == getChildrenCount() ? Boolean.TRUE : null;
    }

    @Override
    public And cloneNode() {
        return new And();
    }
}
