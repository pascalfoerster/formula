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
package de.featjar.formula.io.xml;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.data.Sets;
import de.featjar.base.io.format.ParseException;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.BiImplies;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses feature model formulas from FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelExtraFormulaFormat extends AXMLFeatureModelExtraFormat<Pair<IExpression,Pair<List<String>,List<BiImplies>>>, Pair<Literal,Boolean>, Boolean> {
    protected final LinkedHashSet<String> featureLabels = Sets.empty();
    protected final List<IFormula> constraints = new ArrayList<>();
    protected final List<String> hiddenVariables = new ArrayList<>();
    protected final List<BiImplies> biImpliesList = new ArrayList<>();

    @Override
    public XMLFeatureModelExtraFormulaFormat getInstance() {
        return new XMLFeatureModelExtraFormulaFormat();
    }

    @Override
    public String getName() {
        return "FeatureIDE";
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    protected Pair<IExpression,Pair<List<String>,List<BiImplies>>> parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Result<Element> constraintsElement = getElementResult(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) parseConstraints(constraintsElement.get());
        if (constraints.isEmpty()) {
            return new Pair<>( new And(),new Pair<>(hiddenVariables,biImpliesList));
        } else {
            if (constraints.get(0).getChildren().isEmpty()) {
                constraints.set(0, new Or());
            }
        }
        return new Pair<>(new And(constraints),new Pair<>(hiddenVariables,biImpliesList));
    }

    @Override
    protected void writeDocument(Pair<IExpression,Pair<List<String>,List<BiImplies>>> object, Document doc) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Pattern getInputHeaderPattern() {
        return AXMLFeatureModelFormat.inputHeaderPattern;
    }

    @Override
    protected Pair<Literal,Boolean> newFeatureLabel(
            String name, Pair<Literal,Boolean> parentFeatureLabel, boolean mandatory, boolean _abstract, boolean hidden, boolean or, boolean alt)
            throws ParseException {
        if (featureLabels.contains(name)) {
            throw new ParseException("Duplicate feature name!");
        } else {
            featureLabels.add(name);
        }
        if(hidden || (parentFeatureLabel != null && parentFeatureLabel.getValue())) {
            hiddenVariables.add(name);
        }
        Literal literal = new Literal(name);
        if (parentFeatureLabel == null) {
            constraints.add(literal);
        } else {
            if( !alt && ! or) {
                if (mandatory) {
                    IFormula biImplies = biImplies(parentFeatureLabel.getKey(), literal);
                    biImpliesList.add((BiImplies) biImplies);
                    constraints.add(biImplies);
                } else {

                    constraints.add(implies(literal, parentFeatureLabel.getKey()));
                }
            }
        }
        return new Pair<>(literal,hidden);
    }

    @Override
    protected void addAndGroup(Pair<Literal,Boolean> featureLabel, List<Pair<Literal,Boolean>> childFeatureLabels) {}

    @Override
    protected void addOrGroup(Pair<Literal,Boolean> featureLabel, List<Pair<Literal,Boolean>> childFeatureLabels) {
        IFormula biImplies = biImplies(featureLabel.getKey(), new Or(childFeatureLabels.stream().map(Pair::getKey).collect(Collectors.toList())));
        biImpliesList.add((BiImplies) biImplies);
        constraints.add(biImplies);
    }

    @Override
    protected void addAlternativeGroup(Pair<Literal,Boolean> featureLabel, List<Pair<Literal,Boolean>> childFeature) {
        List<Literal> childFeatureLabels = childFeature.stream().map(Pair::getKey).collect(Collectors.toList());
        for(final Literal literal: childFeatureLabels){
            List<IFormula> negatedChildFeatureLabels =childFeatureLabels.stream().filter(lit ->!lit.equals(literal)).map(Not::new).collect(Collectors.toList());
            negatedChildFeatureLabels.add(featureLabel.getKey());
            IFormula biImplies = biImplies(literal,new And(negatedChildFeatureLabels));
            biImpliesList.add((BiImplies) biImplies);
            constraints.add(biImplies);

        }
        biImpliesList.add((BiImplies) biImplies(featureLabel.getKey(),new Or(childFeatureLabels)));
    }

    @Override
    protected void addFeatureMetadata(Pair<Literal,Boolean> featureLabel, Element e) {}

    @Override
    protected Boolean newConstraintLabel() {
        return true;
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, IFormula formula) throws ParseException {
        constraints.add(formula);
    }

    @Override
    protected void addConstraintMetadata(Boolean constraintLabel, Element e) {}
}
