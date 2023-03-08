/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;

/**
 *
 * @author vbatulevicius
 */
public class Division  implements Serializable {
    private DivisionId id;

    public DivisionId getId() {
        return id;
    }

    public void setId(DivisionId id) {
        this.id = id;
    }
    
}
