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
package de.featjar.formula.analysis.bool;

import de.featjar.base.data.IntegerList;
import de.featjar.base.data.Maps;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.data.Sets;
import de.featjar.formula.analysis.IAssignment;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueAssignment;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.IntStream;

/**
 * Assigns Boolean values to integer-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of literals for use in a satisfiability {@link ISolver}.
 * Implemented as an unordered list of indices to variables in some unspecified {@link VariableMap}.
 * An index can be negative, indicating a negated occurrence of its variable,
 * or 0, indicating no occurrence, and it may occur multiple times.
 * For specific use cases, consider using {@link BooleanClause} (a disjunction
 * of literals) or {@link BooleanSolution} (a conjunction of literals).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ABooleanAssignment extends IntegerList
        implements IAssignment<Integer, Boolean>, IBooleanRepresentation {

    public static int[] simplify(int[] literals) {
        final LinkedHashSet<Integer> integerSet = Sets.empty();
        for (final int integer : literals) {
            if (integer != 0 && integerSet.contains(-integer)) {
                // If this assignment is a contradiction or tautology, it can be simplified.
                return new int[] {integer, -integer};
            } else {
                integerSet.add(integer);
            }
        }
        if (integerSet.size() == literals.length) {
            return Arrays.copyOf(literals, literals.length);
        }
        int[] newArray = new int[integerSet.size()];
        int i = 0;
        for (final int lit : integerSet) {
            newArray[i++] = lit;
        }
        return newArray;
    }

    public static Result<int[]> adapt(
            int[] oldIntegers, VariableMap oldVariableMap, VariableMap newVariableMap, boolean inPlace) {
        final int[] newIntegers = inPlace ? oldIntegers : new int[oldIntegers.length];
        for (int i = 0; i < oldIntegers.length; i++) {
            final int l = oldIntegers[i];
            final Result<String> name = oldVariableMap.get(Math.abs(l));
            if (name.isPresent()) {
                final Result<Integer> index = newVariableMap.get(name.get());
                if (index.isPresent()) {
                    newIntegers[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Problem.Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Problem.Severity.ERROR));
            }
        }
        return Result.of(newIntegers);
    }

    public ABooleanAssignment(int... integers) {
        super(integers);
    }

    public ABooleanAssignment(Collection<Integer> integers) {
        super(integers);
    }

    public ABooleanAssignment(ABooleanAssignment booleanAssignment) {
        super(booleanAssignment);
    }

    public int[] simplify() {
        return simplify(array);
    }

    public Result<int[]> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        return adapt(array, oldVariableMap, newVariableMap, false);
    }

    public boolean containsAnyVariable(int... integers) {
        return Arrays.stream(integers).anyMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public boolean containsAllVariables(int... integers) {
        return Arrays.stream(integers).noneMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public int indexOfVariable(int variableInteger) {
        return IntStream.range(0, array.length)
                .filter(i -> Math.abs(array[i]) == variableInteger)
                .findFirst()
                .orElse(-1);
    }

    protected int countVariables(int[] integers, boolean[] intersectionMarker) {
        int count = 0;
        for (int integer : integers) {
            final int index = indexOfVariable(integer);
            if (index >= 0) {
                count++;
                if (intersectionMarker != null) {
                    intersectionMarker[index] = true;
                }
            }
        }
        return count;
    }

    public int[] removeAllVariables(int... integers) {
        boolean[] intersectionMarker = new boolean[this.array.length];
        int count = countVariables(integers, intersectionMarker);

        int[] newIntegers = new int[this.array.length - count];
        int j = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (!intersectionMarker[i]) {
                newIntegers[j++] = this.array[i];
            }
        }
        return newIntegers;
    }

    public int[] retainAllVariables(int... integers) {
        boolean[] intersectionMarker = new boolean[this.array.length];
        int count = countVariables(integers, intersectionMarker);

        int[] newIntegers = new int[count];
        int j = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (intersectionMarker[i]) {
                newIntegers[j++] = this.array[i];
            }
        }
        return newIntegers;
    }

    public ABooleanAssignment addAll(ABooleanAssignment integers) {
        return new BooleanAssignment(addAll(integers.get()));
    }

    public ABooleanAssignment retainAll(ABooleanAssignment integers) {
        return new BooleanAssignment(retainAll(integers.get()));
    }

    public ABooleanAssignment retainAllVariables(ABooleanAssignment integers) {
        return new BooleanAssignment(retainAllVariables(integers.get()));
    }

    public ABooleanAssignment removeAll(ABooleanAssignment integers) {
        return new BooleanAssignment(removeAll(integers.get()));
    }

    public ABooleanAssignment removeAllVariables(ABooleanAssignment integers) {
        return new BooleanAssignment(removeAllVariables(integers.get()));
    }

    public BooleanAssignment toAssignment() {
        return new BooleanAssignment(IntStream.of(array).filter(l -> l != 0).toArray());
    }

    @Override
    public BooleanClause toClause() {
        return new BooleanClause(IntStream.of(array).filter(l -> l != 0).toArray());
    }

    @Override
    public BooleanSolution toSolution() {
        return new BooleanSolution(IntStream.of(array).map(Math::abs).max().orElse(0), array);
    }

    public abstract ABooleanAssignment inverse();

    public ValueAssignment toValue() {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        for (int literal : array) {
            if (literal != 0) {
                int index = Math.abs(literal);
                variableValuePairs.put(String.valueOf(index), literal > 0);
            }
        }
        return new ValueAssignment(variableValuePairs);
    }
    public ValueAssignment toValueName(VariableMap map) {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        for (int literal : array) {
            if (literal != 0) {
                int index = Math.abs(literal);
                variableValuePairs.put( map.get(index).get(), literal > 0);
            }
        }
        return new ValueAssignment(variableValuePairs);
    }

    @Override
    public LinkedHashMap<Integer, Boolean> getAll() {
        LinkedHashMap<Integer, Boolean> map = Maps.empty();
        for (int integer : array) {
            if (integer > 0) map.put(integer, true);
            else if (integer < 0) map.put(-integer, false);
        }
        return map;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public Result<Boolean> getValue(Integer variable) {
        int index = indexOfVariable(variable);
        if (index < 0) return Result.empty();
        int value = get(index);
        return value == 0 ? Result.empty() : Result.of(value > 0);
    }

    public abstract String print();
}
