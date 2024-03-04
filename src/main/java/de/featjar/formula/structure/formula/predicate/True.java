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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.structure.formula.predicate;

import de.featjar.formula.structure.ATerminalExpression;
import java.util.List;

/**
 * Expresses a tautology.
 * Always evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class True extends ATerminalExpression implements IPolarPredicate {

    public static final True INSTANCE = new True();

    protected True() {}

    @Override
    public String getName() {
        return "true";
    }

    @Override
    public Boolean evaluate(List<?> values) {
        return Boolean.TRUE;
    }

    @Override
    public True cloneNode() {
        return this;
    }

    @Override
    public False invert() {
        return False.INSTANCE;
    }

    @Override
    public boolean isPositive() {
        return true;
    }
}
