package de.featjar.formula.io;

import de.featjar.base.FeatJAR;
import de.featjar.base.data.Pair;
import de.featjar.base.io.format.AFormats;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.BiImplies;

import java.util.List;

public class HiddenFormulaFormats extends AFormats<Pair<IFormula,Pair<List<String>,List<BiImplies>>>> {
    public static HiddenFormulaFormats getInstance() {
        return FeatJAR.extensionPoint(HiddenFormulaFormats.class);
    }
}
