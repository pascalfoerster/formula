package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.data.Void;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.analysis.metrics.IDistanceFunction;
import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.*;
import de.featjar.formula.structure.formula.predicate.False;
import de.featjar.formula.structure.formula.predicate.IPredicate;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.predicate.True;
import de.featjar.formula.structure.term.value.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CoreDeadSimplifier implements ITreeVisitor<IFormula,Void> {

    private final ValueAssignment coreDeadFeature;

    public CoreDeadSimplifier(ValueAssignment coreDeadFeature){
        this.coreDeadFeature = coreDeadFeature;
    }

    @Override
    public Result<Void> nodeValidator(List<IFormula> path) {
        return ITreeVisitor.rootValidator(path, root -> root instanceof Reference, "expected formula reference");
    }

    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IExpression formula = ITreeVisitor.getCurrentNode(path);
        if (formula instanceof IPredicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (formula instanceof IConnective) {
            if (formula instanceof AQuantifier) {
                return TraversalAction.FAIL;
            }
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<IFormula> path) {
        IFormula formula = ITreeVisitor.getCurrentNode(path);
        if (formula instanceof Literal || formula instanceof Variable) return TraversalAction.SKIP_CHILDREN;
        if(!(formula instanceof IPredicate)) formula.replaceChildren(this::replace);
        if (formula instanceof And) {
            if (formula.getChildren().stream().anyMatch(c -> c == Expressions.False)) {
                // false dominates conjunction
                formula.clearChildren();
                formula.addChild(Expressions.False);
            } else {
                // true is neutral to conjunction
                formula.flatReplaceChildren(child -> child instanceof True ? new ArrayList<>() : null);
            }
        }else if (formula instanceof Or) {
            if (formula.getChildren().stream().anyMatch(c -> c == Expressions.True)) {
                // true dominates disjunction
                formula.clearChildren();
                formula.addChild(Expressions.True);
            } else {
                // false is neutral to disjunction
                formula.flatReplaceChildren(child -> child instanceof False ? new ArrayList<>() : null);
            }
        }
        return TraversalAction.CONTINUE;
    }

    private IFormula replace(IExpression formula) {
        if ((formula instanceof Literal)) {
            Literal literal = (Literal) formula;
            if (literal.getExpression() instanceof Variable) {
                Variable variable = (Variable) literal.getExpression();
                Result<Object> result = coreDeadFeature.getValue(variable.getName());
                if (result.isPresent()) {
                    if (result.get() instanceof Boolean && (((Boolean) result.get() && literal.isPositive()) || (!(Boolean) result.get() && !literal.isPositive())) ) {
                        return Expressions.True;
                    } else if (result.get() instanceof Boolean && (!((Boolean) result.get() && literal.isPositive()) || ((Boolean) result.get() && !literal.isPositive()))) {
                        return Expressions.False;
                    }
                }
            }
        }else if((formula instanceof And || formula instanceof Or) && formula.getChildrenCount() == 1){
           return (IFormula) formula.getChildren().get(0);
        } else if (formula instanceof  Not && formula.getChildrenCount() == 1) {
           if(formula.getChildren().get(0) == Expressions.False) return Expressions.True;
           else if (formula.getChildren().get(0) == Expressions.True) return Expressions.False;
        }else if(formula instanceof Implies){
            IExpression left = ((Implies) formula).getLeftExpression();
            IExpression right = ((Implies) formula).getRightExpression() ;
            if(left == Expressions.False) return Expressions.True;
            else if(right == Expressions.True) return  Expressions.True;
            else if (left == Expressions.True && right == Expressions.False) return Expressions.False;
            else if(left == Expressions.True) return (IFormula) right;
            else if(right == Expressions.False) return new Not((IFormula) left);
        }else if(formula instanceof BiImplies){
            IExpression left = ((BiImplies) formula).getLeftExpression();
            IExpression right = ((BiImplies) formula).getRightExpression() ;
            if(left == Expressions.True) return (IFormula) right;
            else if(right == Expressions.True) return (IFormula) left;
            else if (left == Expressions.False && right == Expressions.False) return Expressions.True;
            else if(left == Expressions.False) return  new Not((IFormula) right);
            else if(right == Expressions.False) return new Not((IFormula) left);
        }
        return null;
    }



    @Override
    public Result<Void> getResult() {
        return Result.ofVoid();
    }

}
