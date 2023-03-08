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
public class Represent implements Serializable {
  private RepresentId id;
  private Short odagent;
  private Short locked;

    public RepresentId getId() {
        return id;
    }

    public void setId(RepresentId id) {
        this.id = id;
    }

    public Short getOdagent() {
        return odagent;
    }

    public void setOdagent(Short odagent) {
        this.odagent = odagent;
    }

    public Short getLocked() {
        return locked;
    }

    public void setLocked(Short locked) {
        this.locked = locked;
    }
  
  
    
    
    
}
