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
import lt.vinco.ebdimport.entity.Agent;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Ptappli;
import lt.vinco.ebdimport.entity.Represent;
import lt.vinco.ebdimport.entity.RepresentId;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Node;

/**
 *
 * @author Vincas Batulevičius
 */
public class B740 {
    
    public static int UpdateB740(String extidpatent, Node nodeB740, String status, String pak_data) throws XPathExpressionException {
 //agento keitimas:
  String str = nodeB740.getNodeName();
 System.out.println("Keitime updateB740, nodename: " + str + " "+status+ " "+extidpatent);
 
 
  
 XPath xpath = XPathFactory.newInstance().newXPath();
 String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB740);
        System.out.println("B007EP: " + B007EP);
//        if(!B007EP.contains("2990820")) {
//         System.out.println("B007EP neturi 2990820: " + B007EP);
//            return 0;
//        }
 
 
 

 PatentasDB pdb = EBDimport.YraDB(extidpatent);
  if(!pdb.YraDB) {
 System.out.println("Duomenų bazėje nėra patento  "+extidpatent );
 return 0;
 }
   String idappli = pdb.idappli;
   int lgstappli = pdb.lgstappli;  
   
   
   //Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
   Integer[] statusaiB2 = new Integer[] {621, 623};
  Boolean statuse = false;
  for (Integer st : statusaiB2){
  if(pdb.lgstappli.intValue()==st.intValue()) {statuse = true; break;}
  }
  
  System.out.println("Return: lgstappli: " + pdb.lgstappli );
  
 //
 if(!statuse) {
   System.out.println("Return: patento statusas ne {621, 623}, o: "+pdb.lgstappli );  
     return 0;}
 
 System.out.println("+++++++ Tinkamas B740 patentas +++++++++ "+idappli);

  
  
  
  if(!Langas.RASYTI_DB) { 
 System.out.println("Agento keitimo į DB nerašome. " +idappli+" "+pdb.lgstappli);
 return 0;
 }
 
  String oldidagent = null;
 if(pdb.idagent!=null){
       oldidagent = pdb.getAgentName();//galima į history, tikrinti ar ne null
 }
 
 
  Session   session = null;
  
   try {
         session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
     
      
    String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB740);
java.sql.Date dtdate_publ =   EBDimport.getSqldate(date_publ); //history dtaction       
      
  if(status.equalsIgnoreCase("d"))  {
      System.out.println("====    Statusas=d" );
      
            
   //istriname idagent ptappli lenteleje:         
      Ptappli ptappli = (Ptappli)session.get(Ptappli.class, pdb.idappli);
      ptappli.setIdagent(0);//gal null?
      session.update(ptappli);  
            
    Trinti.DeleteRep(idappli);
  }//if statusas d
 
 
 
 
 if(status.equalsIgnoreCase("r"))  {
     
     System.out.println("====    Statusas  r  ");
 
  
 
 String agentsnm = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/snm/text()", nodeB740);
        String agentstr = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/str/text()", nodeB740);
        String agentcity = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/city/text()", nodeB740);
        String agentctry = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/ctry/text()", nodeB740);
        String sfx = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/sfx/text()", nodeB740);
        String agentiid = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/iid/text()", nodeB740);

        agentsnm = ((agentsnm != null) ? agentsnm : "").replace("\n", " ");
         agentstr = ((agentstr != null) ? agentstr : "").replace("\n", " ");
          agentcity = ((agentcity != null) ? agentcity : "").replace("\n", " ");
           agentctry = ((agentctry != null) ? agentctry : "").replace("\n", " ");
         String agentsfx = ((sfx != null) ? sfx : "").replace("\n", " ");
         agentiid = (agentiid != null) ? agentiid : "";

 System.out.println("Naujas agentas: "+agentsnm + " "+agentstr+ " "+agentcity+ " "+agentsfx+ " "+agentiid+ " "+agentctry);

 Integer idagent = null;
 
 
               
            Integer epidagent = null;
            String hql = null;
             Query q = null;
             Long countagent = null;
              
    //jei faile yra agento duomenys:
      if(!agentiid.equalsIgnoreCase("")&&(agentiid.length()>2))
             {  
            //Tikriname ar db jau yra agentas:
             epidagent = Integer.parseInt(agentiid);
             hql = "select count(*)  from Agent a where a.epidagent = :epidagent ";
             q = session.createQuery(hql);
            q.setParameter("epidagent", epidagent);
             countagent = (Long) q.uniqueResult();
            System.out.println("countagent_:  " + countagent);
             idagent = 0;
            if (countagent > 0) {
                // randame idagen pagal epidagent:
                hql = "select a.idagent  from Agent a where a.epidagent = :epidagent ";
                q = session.createQuery(hql);
                q.setParameter("epidagent", epidagent);
                idagent = (Integer) q.uniqueResult();
                System.out.println("Agentas jau yra, jo iid: " + epidagent);
            } else {
                //irasome nauja agenta ir randame jo idagent:
                Agent ag = new Agent();
                ag.setAdagent(agentstr);
                ag.setDmspecia(Short.parseShort("7"));//--------??????
                ag.setEpidagent(Integer.parseInt(agentiid));
                ag.setFnagent("");
                ag.setIdcountry(agentctry);
                ag.setKdagent(Short.parseShort("1"));
                ag.setMidnagent("");
                ag.setNmagent(agentsnm + " " + agentsfx);
                ag.setNmtown(agentcity);
                session.save(ag);
                session.refresh(ag);
                idagent = ag.getIdagent();
                System.out.println("naujas agentas " + idagent + " " + ag.getNmagent().trim() + " " + ag.getEpidagent());
            }
      }// if faile yra agento duomenys
      
      
      
      
  //Įrašome naują idagent ptappli lentelėje:  
     Ptappli ptappli = (Ptappli)session.get(Ptappli.class, idappli);
      ptappli.setIdagent(idagent);
      session.update(ptappli);

 
 
   //Istriname sena agenta:
      Trinti.DeleteRep(idappli);
//    RepresentId oldrid = new RepresentId();
//            oldrid.setIdagent(oldidagent);
//            oldrid.setIdappli(idappli);   
//       Represent oldrp = (Represent)session.get(Represent.class, oldrid);
//         if(oldrp!=null)  session.delete(oldrp);
//      
//Įrašome agentą į Represent lentelę, nes jau turime idappli ir idagent:
            
            RepresentId rid = new RepresentId();
            rid.setIdagent(idagent.intValue());    //shortvalue buvo
            rid.setIdappli(idappli);
           
             System.out.println("oldidagent: " + oldidagent);
            System.out.println("idppli, new idagent: " + idappli +" "+idagent);
            
                 Represent rp = new Represent();
                 rp.setId(rid);
             rp.setOdagent(Short.valueOf("1"));
             rp.setLocked(Short.valueOf("0"));
             session.save(rp);
       
             System.out.println("Po issaugojimo idppli, new idagent: " + rp.getId().getIdappli() +" "+rp.getId().getIdagent());
             
 }//if status r
      
      
 //Įrašome į history:     
     String      hql = "select max(h.id.odhisto)  from History h where h.id.idappli = :idappli ";
  Query   q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
           Short odh = (Short)q.uniqueResult();
           Short newodh = (short)(odh.intValue()+1);
 System.out.println("odh: "+odh);
 System.out.println("newodh: "+newodh);
         
  History history = new History();
            HistoryId hid = new HistoryId();
            hid.setIdappli(idappli);
            hid.setOdhisto(newodh);
            history.setId(hid);
            history.setIdoper("1342");  //Agento pakeitimas, o kai d????
 if(oldidagent!=null)    history.setOldinfo(oldidagent);

//java.sql.Date dtgrant = EBDimport.getSqldate("20130330");
Date dtop = new Date();
            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
java.sql.Date dtpak_data =   EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));  
      //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //zenonas: dtgrant?
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);  
            session.save(history);   
      
    
      
  session.getTransaction().commit();
EBDimport.logkeit.log(Level.INFO,"Įrašytas keitimas\tB740\t"+extidpatent);
    } catch (HibernateException he) {
        he.printStackTrace();
         session.getTransaction().rollback();
     EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B740\tEP"+extidpatent);     
  EBDimport.logkeit.log(Level.INFO,"Įvyko klaida\tB740\t"+extidpatent);
    }
 
 finally{
//    session.flush();
   session.close();
 }
 
       
     
 System.out.println("Keitimo B740 įrašymo pabaiga "+extidpatent);          
 return 1;       
 }// UpdateB740   
    
    
}
