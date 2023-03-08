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
public class ExtstateId implements Serializable {
    private String idcountry;
    private String idappli;

    public String getIdcountry() {
        return idcountry;
    }

    public void setIdcountry(String idcountry) {
        this.idcountry = idcountry;
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
        hash = 89 * hash + Objects.hashCode(this.idcountry);
        hash = 89 * hash + Objects.hashCode(this.idappli);
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
        final ExtstateId other = (ExtstateId) obj;
        if (!Objects.equals(this.idcountry, other.idcountry)) {
            return false;
        }
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
