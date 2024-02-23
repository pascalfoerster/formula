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
package de.featjar.formula.structure.formula.predicate;

import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.term.ITerm;
import java.util.List;

/**
 * Expresses "A = B" constraints.
 * Evaluates to {@code true} iff both children evaluate to the same value.
 *
 * @author Sebastian Krieter
 */
public class Equals extends ANonTerminalExpression implements IBinaryPredicate, IInvertiblePredicate {

    protected Equals() {}

    public Equals(ITerm leftTerm, ITerm rightTerm) {
        super(leftTerm, rightTerm);
    }

    public Equals(List<? extends ITerm> terms) {
        super(terms);
    }

    @Override
    public String getName() {
        return "=";
    }

    @Override
    public Equals cloneNode() {
        return new Equals();
    }

    @Override
    public NotEquals invert() {
        return new NotEquals((ITerm) getLeftExpression(), (ITerm) getRightExpression());
    }

    @Override
    public boolean compareDifference(int difference) {
        return difference == 0;
    }
}
