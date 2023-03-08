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
public class DivisionId  implements Serializable {
    private String idappli;
    private String idapplidiv;

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public String getIdapplidiv() {
        return idapplidiv;
    }

    public void setIdapplidiv(String idapplidiv) {
        this.idapplidiv = idapplidiv;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.idappli);
        hash = 53 * hash + Objects.hashCode(this.idapplidiv);
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
        final DivisionId other = (DivisionId) obj;
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        if (!Objects.equals(this.idapplidiv, other.idapplidiv)) {
            return false;
        }
        return true;
    }
    
}
