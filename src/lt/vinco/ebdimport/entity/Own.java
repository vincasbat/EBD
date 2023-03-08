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
public class Own implements Serializable {
   private OwnId id;
   private Short odowner;
   private Short is_adr;  //0 arba 1

    public OwnId getId() {
        return id;
    }

    public void setId(OwnId id) {
        this.id = id;
    }

    public Short getOdowner() {
        return odowner;
    }

    public void setOdowner(Short odowner) {
        this.odowner = odowner;
    }

    public Short getIs_adr() {
        return is_adr;
    }

    public void setIs_adr(Short is_adr) {
        this.is_adr = is_adr;
    }
   
   
    
    
}
