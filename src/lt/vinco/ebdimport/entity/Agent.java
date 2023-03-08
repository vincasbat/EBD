/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

import java.io.Serializable;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;


public class Agent implements Serializable {

  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idagent;
    private Short dmspecia;
     private Short kdagent;
    private String fnagent;
      private String midnagent;
      private String nmagent;
      private String adagent;
      private String idcountry;
      private Integer epidagent;
      private String nmtown;

    public Integer getIdagent() {
        return idagent;
    }

    public void setIdagent(Integer idagent) {
        this.idagent = idagent;
    }

    public Short getDmspecia() {
        return dmspecia;
    }

    public void setDmspecia(Short dmspecia) {
        this.dmspecia = dmspecia;
    }

    public Short getKdagent() {
        return kdagent;
    }

    public void setKdagent(Short kdagent) {
        this.kdagent = kdagent;
    }

    public String getFnagent() {
        return fnagent;
    }

    public void setFnagent(String fnagent) {
        this.fnagent = fnagent;
    }

    public String getMidnagent() {
        return midnagent;
    }

    public void setMidnagent(String midnagent) {
        this.midnagent = midnagent;
    }

    public String getNmagent() {
        return nmagent;
    }

    public void setNmagent(String nmagent) {
        this.nmagent = nmagent;
    }

    public String getAdagent() {
        return adagent;
    }

    public void setAdagent(String adagent) {
        this.adagent = adagent;
    }

    public String getIdcountry() {
        return idcountry;
    }

    public void setIdcountry(String idcountry) {
        this.idcountry = idcountry;
    }

    public Integer getEpidagent() {
        return epidagent;
    }

    public void setEpidagent(Integer epidagent) {
        this.epidagent = epidagent;
    }

    public String getNmtown() {
        return nmtown;
    }

    public void setNmtown(String nmtown) {
        this.nmtown = nmtown;
    }
      
      
      
      
    
}
