/* 
 * Copyright 2017 Dagmar Prokopova <xproko26@stud.fit.vutbr.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.vutbr.fit.xproko26.pivis.model.expressions;

/**
 * Abstract class which helps to traverse the expression tree by
 * calling the same method for any type of expression.
 * @author Dagmar Prokopova
 * @param <T> type of the object which is passed into the visit method
 * and of the object returned as result
 */
public abstract class ExpressionVisitor<T>
{

    public abstract T visit(RootExpression node, T o);
    public abstract T visit(RestrictionExpression node, T o);
    public abstract T visit(SumExpression node, T o);
    public abstract T visit(ParallelExpression node, T o);
    public abstract T visit(ParallelReplicationExpression node, T o);
    public abstract T visit(ReplicationExpression node, T o);
    public abstract T visit(InPrefixExpression node, T o);
    public abstract T visit(OutPrefixExpression node, T o);
    public abstract T visit(TauPrefixExpression node, T o);
    public abstract T visit(MatchExpression node, T o);
    public abstract T visit(ConcretizeExpression node, T o);
    public abstract T visit(NilExpression node, T o);
    public abstract T visit(AbstractionExpression node, T o);

    /**
     * Redirects the processing of expression into the correct visit method
     * according to its class type.
     * @param node expression to be visited
     * @param o object passed as a parameter
     * @return result of the processing
     */
    public T visit(Expression node, T o) {
        
        //if no expression is passed, no processing is done
        if (node == null)
            return null;
        
        if (node instanceof RootExpression) {
            return visit((RootExpression)node, o);
        } else if (node instanceof RestrictionExpression) {
            return visit((RestrictionExpression)node, o);
        } else if (node instanceof SumExpression) {
            return visit((SumExpression)node, o);
        } else if (node instanceof ParallelReplicationExpression) { // order matters
            return visit((ParallelReplicationExpression)node, o);
        } else if (node instanceof ParallelExpression) {
            return visit((ParallelExpression)node, o);
        } else if (node instanceof ReplicationExpression) {
            return visit((ReplicationExpression)node, o);
        } else if (node instanceof InPrefixExpression) {
            return visit((InPrefixExpression)node, o);
        } else if (node instanceof OutPrefixExpression) {
            return visit((OutPrefixExpression)node, o);
        } else if (node instanceof TauPrefixExpression) {
            return visit((TauPrefixExpression)node, o);
        } else if (node instanceof MatchExpression) {
            return visit((MatchExpression)node, o);
        } else if (node instanceof ConcretizeExpression) {
            return visit((ConcretizeExpression)node, o);
        } else if (node instanceof NilExpression) {
            return visit((NilExpression)node, o);
        } else if (node instanceof AbstractionExpression) {
            return visit((AbstractionExpression)node, o);
        } else
            return null;
    }
    
}
