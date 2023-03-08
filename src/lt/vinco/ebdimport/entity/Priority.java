/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author vbatulevicius
 */
public class Priority implements Serializable  {
    private PriorityId id;
  
    private Short typrio;
    private String idcountry;
    private String noprio;
    private Date dtprio;
    private String rmprio;
    private Short stprio;

    public PriorityId getId() {
        return id;
    }

    public void setId(PriorityId id) {
        this.id = id;
    }

    
    

    public Short getTyprio() {
        return typrio;
    }

    public void setTyprio(Short typrio) {
        this.typrio = typrio;
    }

    public String getIdcountry() {
        return idcountry;
    }

    public void setIdcountry(String idcountry) {
        this.idcountry = idcountry;
    }

    public String getNoprio() {
        return noprio;
    }

    public void setNoprio(String noprio) {
        this.noprio = noprio;
    }

    public Date getDtprio() {
        return dtprio;
    }

    public void setDtprio(Date dtprio) {
        this.dtprio = dtprio;
    }

    public String getRmprio() {
        return rmprio;
    }

    public void setRmprio(String rmprio) {
        this.rmprio = rmprio;
    }

    public Short getStprio() {
        return stprio;
    }

    public void setStprio(Short stprio) {
        this.stprio = stprio;
    }
    
    
    
    
}
