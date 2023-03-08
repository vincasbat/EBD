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
public class RepresentId  implements Serializable  {
    private Integer idagent;
    private String idappli;

    public Integer getIdagent() {
        return idagent;
    }

    public void setIdagent(Integer idagent) {
        this.idagent = idagent;
    }

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.idagent);
        hash = 67 * hash + Objects.hashCode(this.idappli);
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
        final RepresentId other = (RepresentId) obj;
        if (!Objects.equals(this.idagent, other.idagent)) {
            return false;
        }
        if (!Objects.equals(this.idappli, other.idappli)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
}
