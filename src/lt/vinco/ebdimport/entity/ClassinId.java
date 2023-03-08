/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport.entity;

/**
 *
 * @author vbatulevicius
 */
public class ClassinId implements java.io.Serializable {
    private String idappli;
     private String ipcclass;

    public ClassinId() {
    }

    public ClassinId(String idappli, String ipcclass) {
       this.idappli = idappli;
       this.ipcclass = ipcclass;
    }
   
    public String getIdappli() {
        return this.idappli;
    }
    
    public void setIdappli(String idappli) {
        this.idappli = idappli;
    }
    public String getIpcclass() {
        return this.ipcclass;
    }
    
    public void setIpcclass(String ipcclass) {
        this.ipcclass = ipcclass;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof ClassinId) ) return false;
		 ClassinId castOther = ( ClassinId ) other; 
         
		 return ( (this.getIdappli()==castOther.getIdappli()) || ( this.getIdappli()!=null && castOther.getIdappli()!=null && this.getIdappli().equals(castOther.getIdappli()) ) )
 && ( (this.getIpcclass()==castOther.getIpcclass()) || ( this.getIpcclass()!=null && castOther.getIpcclass()!=null && this.getIpcclass().equals(castOther.getIpcclass()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getIdappli() == null ? 0 : this.getIdappli().hashCode() );
         result = 37 * result + ( getIpcclass() == null ? 0 : this.getIpcclass().hashCode() );
         return result;
   }   
  
}
