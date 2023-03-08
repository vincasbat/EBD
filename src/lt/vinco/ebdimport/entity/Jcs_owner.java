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
public class Jcs_owner implements Serializable {
    private Integer idowner;
    private Integer epidowner;
    private Short ntincorp;
    private String nmowner;
    private String adowner;
    private String idcountry;
    private String nmtown;
    private String unmowner;
    private Short paycat;

    public Integer getIdowner() {
        return idowner;
    }

    public void setIdowner(Integer idowner) {
        this.idowner = idowner;
    }

    public Integer getEpidowner() {
        return epidowner;
    }

    public void setEpidowner(Integer epidowner) {
        this.epidowner = epidowner;
    }

    public Short getNtincorp() {
        return ntincorp;
    }

    public void setNtincorp(Short ntincorp) {
        this.ntincorp = ntincorp;
    }

    public String getNmowner() {
        return nmowner;
    }

    public void setNmowner(String nmowner) {
        this.nmowner = nmowner;
    }

    public String getAdowner() {
        return adowner;
    }

    public void setAdowner(String adowner) {
        this.adowner = adowner;
    }

    public String getIdcountry() {
        return idcountry;
    }

    public void setIdcountry(String idcountry) {
        this.idcountry = idcountry;
    }

    public String getNmtown() {
        return nmtown;
    }

    public void setNmtown(String nmtown) {
        this.nmtown = nmtown;
    }

    public String getUnmowner() {
        return unmowner;
    }

    public void setUnmowner(String unmowner) {
        this.unmowner = unmowner;
    }

    public Short getPaycat() {
        return paycat;
    }

    public void setPaycat(Short paycat) {
        this.paycat = paycat;
    }
    
    
    
}
