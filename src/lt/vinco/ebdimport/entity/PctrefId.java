/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author User
 */
public class PctrefId implements Serializable {
    private String idappli;
    private Short odpctep;

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public Short getOdpctep() {
        return odpctep;
    }

    public void setOdpctep(Short odpctep) {
        this.odpctep = odpctep;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.idappli);
        hash = 23 * hash + Objects.hashCode(this.odpctep);
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
        final PctrefId other = (PctrefId) obj;
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        if (!Objects.equals(this.odpctep, other.odpctep)) {
            return false;
        }
        return true;
    }
    
    
    
}
