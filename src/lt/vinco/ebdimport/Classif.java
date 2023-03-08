/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.sql.Date;

/**
 *
 * @author Vincas Batulevičius
 */
public class Classif {
  private String sequence;
  private String ipcclass;
  private String ipcversionindicator;
  private Short symbpos;
  private String sdtaction;
   private Short tyclassif;

    public Classif(String sequence, String ipcclass, String ipcversionindicator) {
        this.sequence = sequence;
        this.ipcclass = ipcclass;
        this.ipcversionindicator = ipcversionindicator;
    }
  
   public Classif() {
        this.sequence = "";
        this.ipcclass = "";
        this.ipcversionindicator = "";
    }

    public Short getSymbpos() {
        return symbpos;
    }

    public void setSymbpos(Short symbpos) {
        this.symbpos = symbpos;
    }
   
   
   
   
   
   public Short getIpcversion() {
        return Short.valueOf("8");   
    }
   
   public Date getDtversion(){
   return EBDimport.getSqldate(this.ipcversionindicator);
   }
   
   
     public String toString() {
        return sequence+" "+ipcclass+" "+ipcversionindicator;
    }
   
  

    public String getSequence() {
        return sequence;
    }
    
    public Short getSequenceShort() {
        return Short.parseShort(sequence);
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getIpcclass() {
        return ipcclass;
    }

    public void setIpcclass(String ipcclass) {
        this.ipcclass = ipcclass;
    }

    public String getIpcversionindicator() {
        return ipcversionindicator;
    }

    public void setIpcversionindicator(String ipcversionindicator) {
        this.ipcversionindicator = ipcversionindicator;
    }

    public String getSdtaction() {
        return sdtaction;
    }

    public void setSdtaction(String sdtaction) {
        this.sdtaction = sdtaction;
    }

    public Short getTyclassif() {
        return tyclassif;
    }

    public void setTyclassif(Short tyclassif) {
        this.tyclassif = tyclassif;
    }
          
    
    
    
}
