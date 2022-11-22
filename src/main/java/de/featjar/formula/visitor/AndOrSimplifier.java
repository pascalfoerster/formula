/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.visitor;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.connective.Reference;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Merges nested {@link And} and {@link Or} connectives and reduces
 * occurrences of {@link de.featjar.formula.structure.formula.predicate.True}
 * and {@link de.featjar.formula.structure.formula.predicate.False}.
 *
 * @author Sebastian Krieter
 */
public class AndOrSimplifier implements TreeVisitor<Formula, Result.Unit> {
    @Override
    public TraversalAction firstVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if (formula instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (formula instanceof Connective) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if (formula instanceof Connective) {
            if (formula instanceof And) {
                // todo: False dominates And, which is implemented, but True is neutral and can be removed
                if (formula.getChildren().stream().anyMatch(c -> c == Expressions.False)) {
                    formula.setChildren(Collections.singletonList(Expressions.False));
                } else {
                    formula.flatReplaceChildren(this::mergeAnd);
                }
            } else if (formula instanceof Or) {
                // todo: True dominates Or, which is implemented, but False is neutral and can be removed
                if (formula.getChildren().stream().anyMatch(c -> c == Expressions.True)) {
                    formula.setChildren(Collections.singletonList(Expressions.True));
                } else {
                    formula.flatReplaceChildren(this::mergeOr);
                }
            }
        }
        return TraversalAction.CONTINUE;
    }

    private List<? extends Expression> mergeAnd(final Expression child) {
        return (child instanceof And)
                        || (child instanceof Or && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends Expression> mergeOr(final Expression child) {
        return (child instanceof Or)
                        || (child instanceof And && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    @Override
    public Result<Result.Unit> getResult() {
        return Result.unit();
    }
}
