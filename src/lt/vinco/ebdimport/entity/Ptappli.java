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
public class Ptappli implements Serializable {
    

    private String idappli;
    private Integer idagent;
    private Integer idexaminer;
    private String idpatent;
     private Integer novolume;
    private Date dtappli;
    private String idrecept;
    private Date dtrecept;
    private Integer langappli;
    private Integer pttyappli;
    private Integer tyinvent;
    private String title;
    private String engtitle;
    private String stitle;
    private Integer lvsecret;
    private Integer nbclaim;
    private Integer modabstract;
    private Integer nbdraw;
    private Integer vipcmclass;
    private String ipcmclass;
    private Integer tyapplic;
    private Date dtaccept;
    private String idlocation;
    private Date dtlgstappli;
    private Integer lgstappli;
    private Date dttcstappli;
    private Integer tcstappli;
    private Date dtupdate;
    private Integer iduser;
    private Date dtgrant;
    private Integer tyadrsce;
    private String extidappli;  //EP...
    private String extidrecept;
    private String extidpatent; //EP....
    private Date dtnextpay;
    private Integer kdpatent;
    private Integer oldlgstappli;
    private Date dtadvert;
    private Integer offerlic;
    private Integer stprint;
    private Integer k_ptappli; // serial
    private Date dtptexpi;
    private String liartdoc;
    
     public Ptappli() {
    }


    public String getIdappli() {
        return idappli;
    }

    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }

    public Integer getIdagent() {
        return idagent;
    }

    public void setIdagent(Integer idagent) {
       
        this.idagent = (Integer)idagent;
    }

    public Integer getIdexaminer() {
        return idexaminer;
    }

    public void setIdexaminer(Integer idexaminer) {
        this.idexaminer = idexaminer;
    }

    public String getIdpatent() {
        return idpatent;
    }

    public void setIdpatent(String idpatent) {
        this.idpatent = idpatent;
    }

    public Integer getNovolume() {
        return novolume;
    }

    public void setNovolume(Integer novolume) {
        this.novolume = novolume;
    }
    
    
    
    

    public Date getDtappli() {
        return dtappli;
    }

    public void setDtappli(Date dtappli) {
        this.dtappli = dtappli;
    }

    public String getIdrecept() {
        return idrecept;
    }

    public void setIdrecept(String idrecept) {
        this.idrecept = idrecept;
    }

    public Date getDtrecept() {
        return dtrecept;
    }

    public void setDtrecept(Date dtrecept) {
        this.dtrecept = dtrecept;
    }

    public Integer getLangappli() {
        return langappli;
    }

    public void setLangappli(Integer langappli) {
        this.langappli = langappli;
    }

    public Integer getPttyappli() {
        return pttyappli;
    }

    public void setPttyappli(Integer pttyappli) {
        this.pttyappli = pttyappli;
    }

    public Integer getTyinvent() {
        return tyinvent;
    }

    public void setTyinvent(Integer tyinvent) {
        this.tyinvent = tyinvent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEngtitle() {
        return engtitle;
    }

    public void setEngtitle(String engtitle) {
        this.engtitle = engtitle;
    }

    public String getStitle() {
        return stitle;
    }

    public void setStitle(String stitle) {
        this.stitle = stitle;
    }

    public Integer getLvsecret() {
        return lvsecret;
    }

    public void setLvsecret(Integer lvsecret) {
        this.lvsecret = lvsecret;
    }

    public Integer getNbclaim() {
        return nbclaim;
    }

    public void setNbclaim(Integer nbclaim) {
        this.nbclaim = nbclaim;
    }

    public Integer getModabstract() {
        return modabstract;
    }

    public void setModabstract(Integer modabstract) {
        this.modabstract = modabstract;
    }

    public Integer getNbdraw() {
        return nbdraw;
    }

    public void setNbdraw(Integer nbdraw) {
        this.nbdraw = nbdraw;
    }

    public Integer getVipcmclass() {
        return vipcmclass;
    }

    public void setVipcmclass(Integer vipcmclass) {
        this.vipcmclass = vipcmclass;
    }

    public String getIpcmclass() {
        return ipcmclass;
    }

    public void setIpcmclass(String ipcmclass) {
        this.ipcmclass = ipcmclass;
    }

    public Integer getTyapplic() {
        return tyapplic;
    }

    public void setTyapplic(Integer tyapplic) {
        this.tyapplic = tyapplic;
    }

    public Date getDtaccept() {
        return dtaccept;
    }

    public void setDtaccept(Date dtaccept) {
        this.dtaccept = dtaccept;
    }

    public String getIdlocation() {
        return idlocation;
    }

    public void setIdlocation(String idlocation) {
        this.idlocation = idlocation;
    }

    public Date getDtlgstappli() {
        return dtlgstappli;
    }

    public void setDtlgstappli(Date dtlgstappli) {
        this.dtlgstappli = dtlgstappli;
    }

    public Integer getLgstappli() {
        return lgstappli;
    }

    public void setLgstappli(Integer lgstappli) {
        this.lgstappli = lgstappli;
    }

    public Date getDttcstappli() {
        return dttcstappli;
    }

    public void setDttcstappli(Date dttcstappli) {
        this.dttcstappli = dttcstappli;
    }

    public Integer getTcstappli() {
        return tcstappli;
    }

    public void setTcstappli(Integer tcstappli) {
        this.tcstappli = tcstappli;
    }

    public Date getDtupdate() {
        return dtupdate;
    }

    public void setDtupdate(Date dtupdate) {
        this.dtupdate = dtupdate;
    }

    public Integer getIduser() {
        return iduser;
    }

    public void setIduser(Integer iduser) {
        this.iduser = iduser;
    }

    public Date getDtgrant() {
        return dtgrant;
    }

    public void setDtgrant(Date dtgrant) {
        this.dtgrant = dtgrant;
    }

    public Integer getTyadrsce() {
        return tyadrsce;
    }

    public void setTyadrsce(Integer tyadrsce) {
        this.tyadrsce = tyadrsce;
    }

    public String getExtidappli() {
        return extidappli;
    }

    public void setExtidappli(String extidappli) {
        this.extidappli = extidappli;
    }

    public String getExtidrecept() {
        return extidrecept;
    }

    public void setExtidrecept(String extidrecept) {
        this.extidrecept = extidrecept;
    }

    public String getExtidpatent() {
        return extidpatent;
    }

    public void setExtidpatent(String extidpatent) {
        this.extidpatent = extidpatent;
    }

    public Date getDtnextpay() {
        return dtnextpay;
    }

    public void setDtnextpay(Date dtnextpay) {
        this.dtnextpay = dtnextpay;
    }

    public Integer getKdpatent() {
        return kdpatent;
    }

    public void setKdpatent(Integer kdpatent) {
        this.kdpatent = kdpatent;
    }

    public Integer getOldlgstappli() {
        return oldlgstappli;
    }

    public void setOldlgstappli(Integer oldlgstappli) {
        this.oldlgstappli = oldlgstappli;
    }

    public Date getDtadvert() {
        return dtadvert;
    }

    public void setDtadvert(Date dtadvert) {
        this.dtadvert = dtadvert;
    }

    public Integer getOfferlic() {
        return offerlic;
    }

    public void setOfferlic(Integer offerlic) {
        this.offerlic = offerlic;
    }

    public Integer getStprint() {
        return stprint;
    }

    public void setStprint(Integer stprint) {
        this.stprint = stprint;
    }

    public Integer getK_ptappli() {
        return k_ptappli;
    }

    public void setK_ptappli(Integer k_ptappli) {
        this.k_ptappli = k_ptappli;
    }
    
    

   
    
    
    public Date getDtptexpi() {
        return dtptexpi;
    }

    public void setDtptexpi(Date dtptexpi) {
        this.dtptexpi = dtptexpi;
    }

    public String getLiartdoc() {
        return liartdoc;
    }

    public void setLiartdoc(String liartdoc) {
        this.liartdoc = liartdoc;
    }
    
    
    
    
    
    
    
}//class
