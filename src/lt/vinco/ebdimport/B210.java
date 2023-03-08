/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.Date;
import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Ptappli;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Node;

/**
 *
 * @author vbatulevicius
 */
public class B210 {
    //Application number 
    //early app: 2990860
    //original app: 2990880
    //divisional app: 2990890
    
      public static int UpdateB210(String extidpatent, Node nodeB210, String status, String pak_data) throws XPathExpressionException {
 //Application number
   System.out.println("App. number keitimo B210 įrašymo pradžia. extidpatent: "+ extidpatent); 
   
 String idoper = "1354";  //default idoper original appl?
 Boolean b1 = false;
 Boolean b2 = false;
 Boolean b3 = false;
   
      
 XPath xpath = XPathFactory.newInstance().newXPath();
 String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB210);
        System.out.println("B007EP: " + B007EP);
        if(B007EP.contains("2990860")) {
        idoper = "1352"; b1 = true;
        }
         if(B007EP.contains("2990880")) {
        idoper = "1354"; b2 = true;
        }
          if(B007EP.contains("2990890")) {
        idoper = "1356"; b2 = true;
        }
 
// if(!(b1||b2||b3))       {  
//   System.out.println("B007EP neturi 2990860, 2990880 arba 2990890 (app num): " + B007EP);
//   return 0;
// }         
 
 PatentasDB pdb = EBDimport.YraDB(extidpatent);
  if(!pdb.YraDB) {
 System.out.println("Duomenų bazėje nėra patento  "+extidpatent );
 return 0;
 }
  String idappli = pdb.idappli;
  
  
  System.out.println("Return: lgstappli: " + pdb.lgstappli );
  
 
   
 if(status.equalsIgnoreCase("d"))
 {System.out.println("====    Statusas=d   ???"); return 0;}
 
 
  

  if(!status.equalsIgnoreCase("r"))
 {System.out.println("====    Statusas ne r  "); return 0;}
 
 System.out.println("+++++++ Tinkamas B210 keitimas +++++++++ "+idappli);

  if(!Langas.RASYTI_DB) { 
 System.out.println("App. number keitimo į db nerašome. " +idappli+" "+pdb.lgstappli);
 return 0;
 }
  
   
  Boolean klaida = false;
 Session session = null; 

        try {
             session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
   String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB210);
java.sql.Date dtdate_publ =   EBDimport.getSqldate(date_publ); //history dtaction
            
 
  
   //Revocation data:
     String anum = xpath.evaluate("/ep-bulletin/SDOBI/B200/B210/text()", nodeB210);
   // java.sql.Date dtanum =   EBDimport.getSqldate(anum); 
   
            
            
     //Įrašome naują lgstappli ptappli lentelėje:  
     Ptappli ptappli = (Ptappli)session.get(Ptappli.class, idappli);
      ptappli.setExtidappli("EP"+anum);
      session.update(ptappli);
       
            
  
  //Irasome i History lentele:          
 String    hq = "select max(h.id.odhisto)  from History h where h.id.idappli = :idappli ";
      Query qu = session.createQuery(hq);
            qu.setParameter("idappli", idappli);
           Short odh = (Short)qu.uniqueResult();
           Short newodh = (short)(odh.intValue()+1);
 System.out.println("odh: "+odh);
 System.out.println("newodh: "+newodh);
         
  History history = new History();
            HistoryId hid = new HistoryId();
            hid.setIdappli(idappli);
            hid.setOdhisto(newodh);
            history.setId(hid);
            history.setIdoper(idoper);  //app number keitimas

//Date dtop = new Date();
//            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
java.sql.Date dtpak_data =   EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));  // 12
      //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //zenonas: dtgrant?
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ); 
            session.save(history);              
  
 session.getTransaction().commit();
         EBDimport.logkeit.log(Level.INFO,"Įrašytas keitimas\tB210\t"+extidpatent);
    } catch (HibernateException he) {
        he.printStackTrace();
         session.getTransaction().rollback();
          EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B210\tEP"+extidpatent); 
     EBDimport.logkeit.log(Level.INFO,"Įvyko klaida\tB210\t"+extidpatent);
    }
 finally{
  //  session.flush();
   session.close();
        
                 
            
 }
      System.out.println("Keitimo app number B210 įrašymo pabaiga");    
  return 1;
 } 
    
    
    
}
