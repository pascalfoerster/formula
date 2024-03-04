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
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.IBooleanRepresentation;
import java.util.LinkedHashSet;

public interface IValueRepresentation {
    /**
     * {@return a Boolean object with the same contents as this object}
     * If needed, translates variable indices using the given variable map.
     * The returned result may contain warnings, as this can be a lossy conversion.
     *
     * @param variableMap the variable map
     */
    Result<? extends IBooleanRepresentation> toBoolean(VariableMap variableMap);

    default IComputation<? extends IBooleanRepresentation> toBoolean(IComputation<VariableMap> variableMap) {
        return Computations.of(Computations.of(this), variableMap)
                .flatMapResult(IValueRepresentation.class, "toBoolean", pair -> pair.getKey()
                        .toBoolean(pair.getValue()));
    }

    LinkedHashSet<String> getVariableNames();
}
