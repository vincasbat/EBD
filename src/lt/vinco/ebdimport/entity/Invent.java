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
public class Invent implements Serializable {
    private Integer idinvent;
    private String idappli;
    private Short ordinvent;
    private Short confident;

    public Integer getIdinvent() {
        return idinvent;
    }

    public void setIdinvent(Integer idinvent) {
        this.idinvent = idinvent;
    }

    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public Short getOrdinvent() {
        return ordinvent;
    }

    public void setOrdinvent(Short ordinvent) {
        this.ordinvent = ordinvent;
    }

    public Short getConfident() {
        return confident;
    }

    public void setConfident(Short confident) {
        this.confident = confident;
    }
    
    
    
    
}
