/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author vbatulevicius
 */
public class OwnId implements Serializable {
    private Integer idowner;
    private String idappli;

    public Integer getIdowner() {
        return idowner;
    }

    public void setIdowner(Integer idowner) {
        this.idowner = idowner;
    }

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.idowner);
        hash = 71 * hash + Objects.hashCode(this.idappli);
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
        final OwnId other = (OwnId) obj;
        if (!Objects.equals(this.idowner, other.idowner)) {
            return false;
        }
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
}
