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
public class Inventor  implements Serializable {
  private Integer idinvent;
  private Integer epoidinvent;
  private Short tyinventor;
  private String nminventor;
  private String adinventor;
  private String idcountry;
  private String nmtown;
  private String unminventor;

    public Integer getIdinvent() {
        return idinvent;
    }

    public void setIdinvent(Integer idinvent) {
        this.idinvent = idinvent;
    }

    public Integer getEpoidinvent() {
        return epoidinvent;
    }

    public void setEpoidinvent(Integer epoidinvent) {
        this.epoidinvent = epoidinvent;
    }

    public Short getTyinventor() {
        return tyinventor;
    }

    public void setTyinventor(Short tyinventor) {
        this.tyinventor = tyinventor;
    }

    public String getNminventor() {
        return nminventor;
    }

    public void setNminventor(String nminventor) {
        this.nminventor = nminventor;
    }

    public String getAdinventor() {
        return adinventor;
    }

    public void setAdinventor(String adinventor) {
        this.adinventor = adinventor;
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

    public String getUnminventor() {
        return unminventor;
    }

    public void setUnminventor(String unminventor) {
        this.unminventor = unminventor;
    }
  
  
  
    
    
    
}
