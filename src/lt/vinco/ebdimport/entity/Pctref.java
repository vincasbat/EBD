/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author User
 */
public class Pctref implements Serializable  {
    
    private PctrefId id;
    private Short typctep;
    private String nopctep;
    private Date dtpctappli;
    private Short langpctep;
    private String orictry;
    private String nopubli;
    private Date dtpctpubli;
    private Short langpubli;
    private Short stpctep;
    private Date dtelection;
    private Date dtnational;
    private Short nboppo;

    public PctrefId getId() {
        return id;
    }

    public void setId(PctrefId id) {
        this.id = id;
    }

    public Short getTypctep() {
        return typctep;
    }

    public void setTypctep(Short typctep) {
        this.typctep = typctep;
    }

    public String getNopctep() {
        return nopctep;
    }

    public void setNopctep(String nopctep) {
        this.nopctep = nopctep;
    }

    public Date getDtpctappli() {
        return dtpctappli;
    }

    public void setDtpctappli(Date dtpctappli) {
        this.dtpctappli = dtpctappli;
    }

    public Short getLangpctep() {
        return langpctep;
    }

    public void setLangpctep(Short langpctep) {
        this.langpctep = langpctep;
    }

    public String getOrictry() {
        return orictry;
    }

    public void setOrictry(String orictry) {
        this.orictry = orictry;
    }

    public String getNopubli() {
        return nopubli;
    }

    public void setNopubli(String nopubli) {
        this.nopubli = nopubli;
    }

    public Date getDtpctpubli() {
        return dtpctpubli;
    }

    public void setDtpctpubli(Date dtpctpubli) {
        this.dtpctpubli = dtpctpubli;
    }

    public Short getLangpubli() {
        return langpubli;
    }

    public void setLangpubli(Short langpubli) {
        this.langpubli = langpubli;
    }

    public Short getStpctep() {
        return stpctep;
    }

    public void setStpctep(Short stpctep) {
        this.stpctep = stpctep;
    }

    public Date getDtelection() {
        return dtelection;
    }

    public void setDtelection(Date dtelection) {
        this.dtelection = dtelection;
    }

    public Date getDtnational() {
        return dtnational;
    }

    public void setDtnational(Date dtnational) {
        this.dtnational = dtnational;
    }

    public Short getNboppo() {
        return nboppo;
    }

    public void setNboppo(Short nboppo) {
        this.nboppo = nboppo;
    }
    
    
    
}
