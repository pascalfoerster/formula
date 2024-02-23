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
import de.featjar.formula.structure.IUnaryExpression;
import de.featjar.formula.structure.formula.IFormula;
import java.util.List;

/**
 * Expresses "not A" constraints (i.e., negation).
 * Evaluates to {@code true} iff its child evaluates to {@code false}.
 *
 * @author Sebastian Krieter
 */
public class Not extends ANonTerminalExpression implements IConnective, IUnaryExpression {
    protected Not() {}

    public Not(IFormula formula) {
        super(formula);
    }

    public Not(List<? extends IFormula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "not";
    }

    @Override
    public Object evaluate(List<?> values) {
        Object a = values.get(0);
        if (a == null) {
            return null;
        }
        return !(boolean) a;
    }

    @Override
    public Not cloneNode() {
        return new Not();
    }
}
