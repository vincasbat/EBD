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
public class Extstate implements Serializable {
 private ExtstateId id;
 private Date dtrpay;
 private Date dtwithdraw;

    public ExtstateId getId() {
        return id;
    }

    public void setId(ExtstateId id) {
        this.id = id;
    }

    public Date getDtrpay() {
        return dtrpay;
    }

    public void setDtrpay(Date dtrpay) {
        this.dtrpay = dtrpay;
    }

    public Date getDtwithdraw() {
        return dtwithdraw;
    }

    public void setDtwithdraw(Date dtwithdraw) {
        this.dtwithdraw = dtwithdraw;
    }
    
 
 
    
}
