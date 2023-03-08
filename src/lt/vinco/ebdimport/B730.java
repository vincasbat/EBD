/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Jcs_owner;
import lt.vinco.ebdimport.entity.Own;
import lt.vinco.ebdimport.entity.OwnId;
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
public class B730 {

    public static int UpdateB730(String extidpatent, Node nodeB730, String status, String pak_data) throws XPathExpressionException {
        //savininko keitimas/transfer of rights   2990830
        System.out.println("Keitimo B730 įrašymo pradžia. extidpatent: " + extidpatent);



        XPath xpath = XPathFactory.newInstance().newXPath();
        String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB730);
        System.out.println("B007EP: " + B007EP);

        Boolean transfer = false;
        Boolean detail = false;
        String idoper = "1344";  //default idoper transfer?


        if (B007EP.contains("2990830")) { //2990830-transfer of rights, 2992100-details
            transfer = true;
            idoper = "1344";
        }

        if (!B007EP.contains("2992100")) { //2990830-transfer of rights, 299210-???
            detail = true;
            idoper = "1412";
        }

//        if (!(transfer || detail)) {
//            System.out.println("B007EP neturi 2990830 arba 2992100 : " + B007EP);
//            return 0;
//        }


        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }

        //Buvęs savininkas (-ai):
        String oldGrantees = pdb.getOwnerNames();


        String idappli = pdb.idappli;
        int lgstappli = pdb.lgstappli;  //Ar reikia tikrinti teisinį statusa? 
        //Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
        Integer[] statusaiB2 = new Integer[]{601, 621, 623, 32, 34, 35, 30};
        Boolean statuse = false;
        for (Integer st : statusaiB2) {
            if (lgstappli == st.intValue()) {
                statuse = true;
                break;
            }
        }

        System.out.println("Return: lgstappli: " + pdb.lgstappli);

        //
        if (!statuse) {
            System.out.println("Return: patento statusas ne {601, 621, 623, 32, 34, 35, 30}, o: " + pdb.lgstappli);
            return 0;
        }

        System.out.println("+++++++ Tinkamas B730 patentas +++++++++ " + idappli);

        if (!Langas.RASYTI_DB) {
            System.out.println("Savininko keitimo į DB nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }

        //d statuso nebūna
        if (!status.equalsIgnoreCase("r")) {
            System.out.println("Return:   Statusas ne r, o  " + status);
            return 0;
        }




        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB730);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); // i history dtoper   (dtaction)


            //Istriname senus savininkus:
            Trinti.DeleteOwn(idappli);



            Object result1 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/snm/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes1 = (NodeList) result1;
            Object result2 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/str/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes2 = (NodeList) result2;
            Object result3 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/city/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes3 = (NodeList) result3;
            Object result4 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/ctry/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes4 = (NodeList) result4;
            Object result5 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/iid/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes5 = (NodeList) result5;
            Object result6 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/irf/text()", nodeB730, XPathConstants.NODESET);
            NodeList nodes6 = (NodeList) result6;

            String str;
            if (nodes1.getLength() > 0) {
                for (int i = 0; i < nodes1.getLength(); i++) {

                    //Tikriname ar db jau yra savininkas:
                    Integer epidowner = Integer.parseInt(nodes5.item(i).getNodeValue());
                    String hql = "select count(*)  from Jcs_owner jo where jo.epidowner = :epidowner ";
                    Query q = session.createQuery(hql);
                    q.setParameter("epidowner", epidowner);
                    Long countown = (Long) q.uniqueResult();
                    System.out.println("count j own:  " + countown);
                    Integer idowner = 0;
                    if (countown > 0) {
// // randame idowner pagal epidowner:
                        hql = "select jo.idowner  from Jcs_owner jo where jo.epidowner = :epidowner ";
                        q = session.createQuery(hql);
                        q.setParameter("epidowner", epidowner);
                        idowner = (Integer) q.uniqueResult();
                        System.out.println("Savininkas jau yra, jo iid ir idowner: " + epidowner + "  " + idowner);
                    } else {
// //irasome nauja savininką ir randame jo idowner:
                        Jcs_owner own = new Jcs_owner();
                        String granteeStreet = null;

                        if ((nodes2.item(i) != null)) {
                            System.out.println("NODES2length: " + nodes2.getLength() + " i: " + i);
                            granteeStreet = nodes2.item(i).getNodeValue().toString();
                        } else {
                            granteeStreet = "";
                        }

                        //String mood = (isHappy == true)?"I'm Happy!":"I'm Sad!";                       

                        own.setAdowner(granteeStreet.replace("\n", " "));
                        own.setEpidowner(epidowner);
                        String granteeCtry = null;
                        if (!(nodes4 == null)) {
                            granteeCtry = nodes4.item(i).getNodeValue();
                        }
                        own.setIdcountry(granteeCtry);
                        String granteeName = null;
                        if (!(nodes1 == null)) {
                            granteeName = nodes1.item(i).getNodeValue();
                        }
                        own.setNmowner(granteeName);
                        String granteeCity = null;
                        if (!(nodes3 == null)) {
                            granteeCity = nodes3.item(i).getNodeValue();
                        }
                        own.setNmtown(granteeCity);
                        own.setNtincorp(Short.valueOf("3"));
                        own.setPaycat(Short.valueOf("0"));
                        own.setUnmowner(granteeName.toUpperCase());

                        session.save(own);
                        session.refresh(own);
                        idowner = own.getIdowner();
                        System.out.println("Naujas savininkas: " + own.getNmowner());
                        str = "New Grantee: " + granteeName + ", "
                                + ", " + granteeCity + ", " + granteeCtry;
                        str = str.replace("\n", " ");
                        System.out.println(str);
                    }//else 




//Dabar rašome į own lentelę idowner, idappli, odowner, is_adr:     

                    OwnId ownid = new OwnId();
                    ownid.setIdappli(idappli);
                    ownid.setIdowner(idowner);
                    Own own = new Own();
                    own.setId(ownid);
                    own.setIs_adr(Short.valueOf("1"));///zenonas sake, kad turi buti 1
                    own.setOdowner((short) (i + 1));
                    session.save(own);

                    //String mood = (isHappy == true)?"I'm Happy!":"I'm Sad!";   
                    //log.log(Level.INFO, "Savininkas įrašytas:  {0}", str); 
                }//for grantees
            }//if nodes1.getLength()>0



            //Irasome i History lentele:          
            String hq = "select max(h.id.odhisto)  from History h where h.id.idappli = :idappli ";
            Query qu = session.createQuery(hq);
            qu.setParameter("idappli", idappli);
            Short odh = (Short) qu.uniqueResult();
            Short newodh = (short) (odh.intValue() + 1);
            System.out.println("odh: " + odh);
            System.out.println("newodh: " + newodh);

            History history = new History();
            HistoryId hid = new HistoryId();
            hid.setIdappli(idappli);
            hid.setOdhisto(newodh);
            history.setId(hid);

            if (oldGrantees != null) {
                history.setOldinfo(oldGrantees);
            }

            history.setIdoper(idoper);//Savininko pakeitimas

//java.sql.Date dtgrant = EBDimport.getSqldate("20130330");
//Date dtop = new Date();
//            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
//            

            java.sql.Date dtpak_data = EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   




            history.setDtoper(dtdate_publ); //zenonas: tb dtdate_publ visuose keitimuose
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));
            //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data);
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);
            session.save(history);

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB730\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B730\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB730\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();
        }



        System.out.println("Keitimo B730 įrašymo pabaiga");
        return 1;
    }//Update730
}
