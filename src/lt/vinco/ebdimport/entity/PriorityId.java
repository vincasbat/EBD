/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.util.Objects;

/**
 *
 * @author vbatulevicius
 */
public class PriorityId implements java.io.Serializable {
    private String idappli;
    private Short odprio;

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public Short getOdprio() {
        return odprio;
    }

    public void setOdprio(Short odprio) {
        this.odprio = odprio;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.idappli);
        hash = 47 * hash + Objects.hashCode(this.odprio);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PriorityId other = (PriorityId) obj;
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        if (!Objects.equals(this.odprio, other.odprio)) {
            return false;
        }
        return true;
    }

   

    
    
    
}
