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
import lt.vinco.ebdimport.entity.Priority;
import lt.vinco.ebdimport.entity.PriorityId;
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
public class B300 {

    //prioritetai:
    public static int UpdateB300(String extidpatent, Node nodeB300, String status, String pak_data) throws XPathExpressionException {
        //DIM360 Ver 2.15 (14 Jul 2008) - 2990810/0</B007EP> hist 1340
        System.out.println("Keitimo B300 įrašymo pradžia. extidpatent: " + extidpatent);




        XPath xpath = XPathFactory.newInstance().newXPath();
// String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB300);
//        System.out.println("B007EP: " + B007EP);
//        if(!B007EP.contains("2990810")) {
//         System.out.println("B007EP neturi 2990810: " + B007EP);
//            return 0;
//        }


        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;



        //Neaisku ka daryti, kai statusas yra d = ???????
        if (status.equalsIgnoreCase("d")) {
            System.out.println("====    Statusas=d   ???");
            return 0;
        }


        if (!status.equalsIgnoreCase("r")) {
            System.out.println("====    Statusas ne r  ");
            return 0;
        }


        System.out.println("+++++++ Tinkamas B300 patentas +++++++++ " + idappli);


        if (!Langas.RASYTI_DB) {
            System.out.println("Prioriteto keitimo į DB nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }


        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB300);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction

            //Istriname senus prioritetus is priority:
            Trinti.DeletePrio(idappli);

            //Prioritetai
            Object result1 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B310/text()", nodeB300, XPathConstants.NODESET);
            NodeList nodes1 = (NodeList) result1;
            Object result2 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B320/date/text()", nodeB300, XPathConstants.NODESET);
            NodeList nodes2 = (NodeList) result2;
            Object result3 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B330/ctry/text()", nodeB300, XPathConstants.NODESET);
            NodeList nodes3 = (NodeList) result3;
            for (int i = 0; i < nodes1.getLength(); i++) {

                String b310 = "";
                String b320 = "";
                String b330 = "";
                if (nodes1 != null) {
                    b310 = (nodes1.item(i) != null) ? nodes1.item(i).getNodeValue() : "";
                }
                if (nodes2 != null) {
                    b320 = (nodes2.item(i) != null) ? nodes2.item(i).getNodeValue() : "";
                }
                if (nodes3 != null) {
                    b330 = (nodes3.item(i) != null) ? nodes3.item(i).getNodeValue() : "";
                }

                String str = "Prioritetas: " + nodes1.item(i).getNodeValue() + ", " + EBDimport.formatDate(nodes2.item(i).getNodeValue())
                        + ", " + nodes3.item(i).getNodeValue();
                System.out.println(str);
                str = null;


                PriorityId pid = new PriorityId();
                pid.setIdappli(idappli);
                pid.setOdprio((short) (i + 1));
                Priority prio = new Priority();
                prio.setId(pid);
                prio.setDtprio(EBDimport.getSqldate(b320));//data
                prio.setIdcountry(b330);//ctry
                prio.setNoprio(b310);
                prio.setRmprio("");
                prio.setStprio(Short.parseShort("1"));//zenonas sake, kad tb 1
                prio.setTyprio(Short.parseShort("1")); ///???
                session.save(prio);
            }// for prio



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
            history.setIdoper("1340");  //Prioriteto pakeitimas

//java.sql.Date dtgrant = EBDimport.getSqldate("20130330");
            Date dtop = new Date();
            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
            java.sql.Date dtpak_data = EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));
            //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);
            session.save(history);

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB300\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B300\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB300\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();


        }
        System.out.println("Keitimo B300 įrašymo pabaiga");
        return 1;
    }
}
