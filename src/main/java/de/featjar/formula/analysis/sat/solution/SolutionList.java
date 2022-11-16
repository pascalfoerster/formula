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
package de.featjar.formula.analysis.sat.solution;

import de.featjar.formula.analysis.sat.LiteralMatrix;
import de.featjar.formula.analysis.sat.clause.CNF;

import java.util.Collection;
import java.util.List;

/**
 * A list of solutions.
 * Typically used to express solutions to a {@link CNF}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class SolutionList extends LiteralMatrix<SolutionList, SATSolution> {
    public SolutionList() {
    }

    public SolutionList(int size) {
        super(size);
    }

    public SolutionList(Collection<? extends SATSolution> solutions) {
        super(solutions);
    }

    public SolutionList(SolutionList other) {
        super(other);
    }

    @Override
    protected SolutionList newLiteralMatrix(List<SATSolution> SATSolutions) {
        return new SolutionList(SATSolutions);
    }

//    public SortedIntegerList getVariableAssignment(int variable) {
//        final int[] assignment = new int[solutions.size()];
//        int index = 0;
//        for (final SortedIntegerList solution : solutions) {
//            assignment[index++] = solution.getIntegers()[variable];
//        }
//        return new SortedIntegerList(assignment, SortedIntegerList.Order.UNORDERED);
//    }

//    private String literalToString(int literal) {
//        final Optional<String> name = variables.get(Math.abs(literal));
//        return name.isEmpty() ? "?" : (literal > 0 ? "" : "-") + name.get();
//    }
//
//    public String getSolutionsString() {
//        final StringBuilder sb = new StringBuilder();
//        for (final SortedIntegerList sortedIntegerList : solutions) {
//            sb.append("(");
//            final List<String> literals = Arrays.stream(sortedIntegerList.getIntegers())
//                    .mapToObj(this::literalToString)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//            for (final String literal : literals) {
//                sb.append(literal);
//                sb.append(", ");
//            }
//            if (!literals.isEmpty()) {
//                sb.delete(sb.length() - 2, sb.length());
//            }
//            sb.append("), ");
//        }
//        if (!solutions.isEmpty()) {
//            sb.delete(sb.length() - 2, sb.length());
//        }
//        return sb.toString();
//    }
//
//    public Stream<SortedIntegerList> getInvalidSolutions(CNF cnf) {
//        return solutions.stream() //
//                .filter(s -> cnf.getClauseList().stream() //
//                        .anyMatch(clause -> s.containsAll(clause.negate())));
//    }
//
//    public Stream<SortedIntegerList> getValidSolutions(CNF cnf) {
//        return solutions.stream() //
//                .filter(s -> cnf.getClauseList().stream() //
//                        .allMatch(clause -> !s.isDisjoint(clause)));
//    }
}