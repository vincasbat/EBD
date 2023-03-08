/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.Date;
import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Invent;
import lt.vinco.ebdimport.entity.Inventor;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Vincas Batulevičius
 */
public class B720 {
    //Inventoriu keitimas:
   public static int UpdateB720(String extidpatent, Node nodeB720, String status, String pak_data) throws XPathExpressionException {
    System.out.println("Keitimo B720 įrašymo pradžia. extidpatent: "+ extidpatent+"   "+status);      
 
   
 XPath xpath = XPathFactory.newInstance().newXPath();
 String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB720);
        System.out.println("B007EP: " + B007EP);
//        if(!B007EP.contains("2990850")) {
//         System.out.println("B007EP neturi 2990850: " + B007EP);
//            return 0;
//        }
 
 
 PatentasDB pdb = EBDimport.YraDB(extidpatent);
  if(!pdb.YraDB) {
 System.out.println("Duomenų bazėje nėra patento  "+extidpatent );
 return 0;
 }
   String idappli = pdb.idappli;
   
   
    //Buvęs inventorius (-ai):
  String oldInvs = pdb.getInvNames();
   
   
   //Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
   Integer[] statusaiB2 = new Integer[] {601, 621, 623, 32, 34, 35, 30};
  Boolean statuse = false;
  for (Integer st : statusaiB2){
  if(pdb.lgstappli.intValue()==st.intValue()) {statuse = true; break;}
  }
  
  System.out.println("lgstappli: " + pdb.lgstappli );
  
 //
 if(!statuse) {
   System.out.println("Return: B2 patento statusas ne {601, 621, 623, 32, 34, 35, 30}, o: "+pdb.lgstappli );  
     return 0;}
 
 if(!status.equalsIgnoreCase("r"))
 {System.out.println("====    Statusas ne r  "); return 0;}
 
 
 System.out.println("+++++++ Tinkamas B720 patentas +++++++++ "+idappli);

  
  
 
  
  if(!Langas.RASYTI_DB) { 
 System.out.println("Inventoriaus keitimo į DB nerašome. " +idappli+" "+pdb.lgstappli);
 return 0;
 }
  
 
 
  Session session = null; 
 
        try {
             session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
        String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB720);
java.sql.Date dtdate_publ =   EBDimport.getSqldate(date_publ); //history dtaction   
 
 //Istriname senus inventorius is invent:
 Trinti.DeleteInventor(idappli);
 

 
 Object  result1 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/snm/text()", nodeB720, XPathConstants.NODESET);
  NodeList          nodes1 = (NodeList) result1;
     Object        result2 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/str/text()", nodeB720, XPathConstants.NODESET);
   NodeList          nodes2 = (NodeList) result2;
     Object        result3 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/city/text()", nodeB720, XPathConstants.NODESET);
  NodeList           nodes3 = (NodeList) result3;
      Object       result4 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/ctry/text()", nodeB720, XPathConstants.NODESET);
 NodeList            nodes4 = (NodeList) result4;

   
String str;
            if (nodes1.getLength() > 0) {
                for (int i = 0; i < nodes1.getLength(); i++) {
                
               String snm=""; String  strt= ""; String city = ""; String ctry="";  
              if (nodes1!=null)  snm = ((nodes1.item(i)!=null)?nodes1.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes2!=null)      strt = ((nodes2.item(i)!=null)?nodes2.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes3!=null)    city =  ((nodes3.item(i)!=null)?nodes3.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes4!=null)   ctry = (nodes4.item(i)!=null)?nodes4.item(i).getNodeValue():"";

                str = "Inventor: " + snm + ", " + strt  + ", " + city + ", " + ctry;
                str = str.replace("\n", " ");
                System.out.println(str);
                //log.log(Level.INFO, "Išradėjas įrašytas:  {0}", str); 
                 //String mood = (isHappy == true)?"I'm Happy!":"I'm Sad!";   
         
                Inventor inventor = new Inventor();
                inventor.setAdinventor(strt);
                inventor.setEpoidinvent(Integer.parseInt("-1"));
                inventor.setIdcountry(ctry);
                inventor.setNminventor(snm);
                inventor.setNmtown(city);
                inventor.setTyinventor(Short.parseShort("2"));
                inventor.setUnminventor(snm.toUpperCase());

                session.save(inventor);
                session.refresh(inventor);

                Integer idinvent = inventor.getIdinvent();
                //irasyti i invent:
                Invent invent = new Invent();
                invent.setConfident(Short.valueOf("0"));
                invent.setIdappli(idappli);
                invent.setIdinvent(idinvent);
                invent.setOrdinvent((short) (i + 1));
                session.save(invent);

            } //for
            }//if nodes1.getLength()>0



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
            history.setIdoper("1350");  //Inventoriaus pakeitimas
            
             if (oldInvs!=null)        history.setOldinfo(oldInvs);

//java.sql.Date dtgrant = EBDimport.getSqldate("20130330");
Date dtop = new Date();
            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
java.sql.Date dtpak_data =   EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));  // 12
      //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //zenonas: dtgrant?
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);  //?dtgrant?
            session.save(history);              
  
 session.getTransaction().commit();
 EBDimport.logkeit.log(Level.INFO,"Įrašytas keitimas\tB720\t"+extidpatent);
    } catch (HibernateException he) {
        he.printStackTrace();
         session.getTransaction().rollback();
      EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B720\tEP"+extidpatent);    
 EBDimport.logkeit.log(Level.INFO,"Įvyko klaida\tB720\t"+extidpatent);
    }
 finally{
//    session.flush();
    session.close();
 }
      System.out.println("Keitimo B720 įrašymo pabaiga");          
 return 1;  
  }//Update720
 
    
}
