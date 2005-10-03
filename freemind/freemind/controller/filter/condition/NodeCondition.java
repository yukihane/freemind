/*
 * Created on 15.05.2005
 *
 */
package freemind.controller.filter.condition;


import javax.swing.JComponent;

/**
 * @author dimitri
 * 15.05.2005
 */
public abstract class NodeCondition implements Condition {

    String description;
    
    protected NodeCondition (String description) {
        super();
        this.description = description;
    }
    
    public JComponent getListCellRendererComponent() {
        return ConditionFactory.createCellRendererComponent(description);
    }
    
    public String toString(){
        return description;
    }

}