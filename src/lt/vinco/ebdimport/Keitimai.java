/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.w3c.dom.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;


/**
 *
 * @author Vincas Batulevičius
 */
public class Keitimai {
    
    
    public static int UpdateB475(String extidpatent, Node nodeB475, String status) {
 //Lapse of patent
        String str = nodeB475.getNodeName();
 System.out.println("Keitime updateB475, nodename: " + str + status);
 System.out.println("Keitime updateB475, extidpatent: " + extidpatent);
 
 //Neaisku ka daryti, kai statusas yra d = ???????
 if(status.equalsIgnoreCase("d"))
 {System.out.println("====    Statusas=d   ???"); return 0;}
 
 String sDate = null;
 Boolean ltLapsed = false;
 NodeList nl = nodeB475.getChildNodes();
 System.out.println("Child node count: "+nl.getLength());
 for (int i=0; i<nl.getLength(); i++){
     if(nl.item(i).getNodeName().equalsIgnoreCase("date"))
         sDate = nl.item(i).getTextContent();
      System.out.println("i node value: "+nl.item(i).getTextContent());
     if(nl.item(i).getTextContent().equalsIgnoreCase("LT")) {
         ltLapsed = true;
System.out.println("Lietuvoje lapsed: "+sDate);
break;
     }
 }//for
 
// randame lapso dtlegal: 
  SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
Date dtleg = null;
        try {
            dtleg = df.parse(sDate);
        } catch (ParseException ex) {
            Logger.getLogger(EBDimport.class.getName()).log(Level.SEVERE, null, ex);
        }
 java.sql.Date dtlegal = new java.sql.Date(dtleg.getTime());
 

 
 
 //------------------- KEITIMO ĮRAŠYMAS Į DB  ------------------------------  
 
 
 
if(!Langas.RASYTI_DB) return 0;   //atjungimas
 try {
        Session session = HibernateUtil.getSessionFactory().openSession();
       session.beginTransaction();
       
    
            
                       
            
 System.out.println("Įrašome keitimo history: ");     
String   hql ="select max(h.id.odhisto) from History h, Ptappli p where h.id.idappli = p.idappli and  p.extidpatent = :extidpatent "; 
Query  q = session.createQuery(hql);
       q.setParameter("extidpatent", "EP"+extidpatent);
      Short odhisto = (Short)q.uniqueResult();
        System.out.println("max odhisto______: "+odhisto); 
        if(odhisto==null){
   System.out.println("odhisto lygus null!!!!");
   return 0;
   }  
        
     //Pagal extidappli surandame idappli:   
    hql ="select p.idappli from Ptappli p where p.extidpatent = :extidpatent "; 
  q = session.createQuery(hql);
       q.setParameter("extidpatent", "EP"+extidpatent);
     // String idappli = (String)q.uniqueResult();  
    List list = q.list();   
    String idappli =    (String)list.get(0);
       System.out.println("idappli: "+idappli);
   if(idappli==null){
   System.out.println("idappli lygus null!!!!");
   return 0;
   }    

        
        History history = new History();
        HistoryId hid = new HistoryId();
        hid.setIdappli(idappli);              
        hid.setOdhisto(++odhisto);  //padidiname history įrašo skaičių
        history.setId(hid);
        history.setIdoper("1302_?");  //operacijos kodas 1302 arba 1394
        history.setDtlegal(dtlegal);  //dtlegal

    Date dtop = new Date();
 java.sql.Date dtoper = new java.sql.Date(dtop.getTime());       
 history.setDtoper(dtoper);
 
 history.setOldinfo("TEST OLD INFO");
 
 history.setSthisto(new Short("1"));       
 history.setIduseroper(new Short("12"));  
 history.setCddecioper(new Short("1"));  
 
 history.setStpay(new Short("1"));  
 history.setDtlegal(dtoper); 
 history.setLvlpubli(new Short("0")); 
 

 
 session.save(history);

   session.getTransaction().commit();
        //session.close();??
    } catch (HibernateException he) {
        he.printStackTrace();
    }
 
 //------------------- KEITIMO ĮRAŠYMO Į DB PABAIGA  -----------------------
  
// log.log(Level.INFO, "Baigiamas tvarkyti {0}", file.getName());     
System.out.println("Keitimo B745 įrašymo pabaiga");
    return 1;
    }//UpdateB475
    
    
   
    
}//class
