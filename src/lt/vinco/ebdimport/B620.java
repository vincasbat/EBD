/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.Division;
import lt.vinco.ebdimport.entity.DivisionId;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vbatulevicius
 */
public class B620 {

    public static int UpdateB620(String extidpatent, Node nodeB620, String status, String pak_data) throws XPathExpressionException {

        //division keitimas
        System.out.println("Keitimo B620EP įrašymo pradžia. extidpatent: " + extidpatent);




        XPath xpath = XPathFactory.newInstance().newXPath();
        String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB620);
        System.out.println("B007EP: " + B007EP);
//        if(!B007EP.contains("29908???")) {    ????? ar reikia?
//         System.out.println("B007EP neturi 2990???: " + B007EP);
//            return 0;
//        }


        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;



        //Ka daryti, kai statusas yra d = ???????
        if (status.equalsIgnoreCase("d")) {
            System.out.println("====    Statusas=d   ???");
            return 0;
        }


        if (!status.equalsIgnoreCase("r")) {
            System.out.println("====    Statusas ne r  ");
            return 0;
        }


        System.out.println("+++++++ Tinkamas B620 patentas +++++++++ " + idappli);


        if (!Langas.RASYTI_DB) {
            System.out.println("Division keitimo į DB nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }







        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB620);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction


            Object result = null;
            List<String> parent_pnums = new ArrayList<>();
            result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/pnum/child::node()", nodeB620, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    parent_pnums.add(nodes.item(i).getTextContent());
                }
            }//if nodes!=null
            System.out.println("Parents: " + parent_pnums);


            result = null;
            List<String> parent_anums = new ArrayList<>();
            result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/anum/child::node()", nodeB620, XPathConstants.NODESET);
            nodes = (NodeList) result;
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    parent_anums.add(nodes.item(i).getTextContent());
                }
            }//if nodes!=null
            System.out.println("Parent anums: " + parent_anums);


            //Istriname sena div:
            Trinti.DeleteDiv(idappli);



            if (!parent_pnums.isEmpty()) {
                for (String pnm : parent_pnums) {
                    PatentasDB pr = EBDimport.YraDB(pnm);
                    if (pr.YraDB) {  //jei yra parent, užpildome Division lentelę
                        //įrašome į Division:
                        DivisionId divid = new DivisionId();
                        divid.setIdappli(pr.idappli);
                        divid.setIdapplidiv(idappli);
                        Division div = new Division();
                        div.setId(divid);
                        session.save(div);
                    }// if
                }//for
            }//is empty     






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
            history.setIdoper("div_B620_idoper");  //Koks idoper keičiant division?

//Date dtop = new Date();
//            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
            java.sql.Date dtpak_data = EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));
            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //zenonas: dtgrant?
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);
            session.save(history);

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB620\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B620\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB620\t" + extidpatent);
        } finally {
            session.flush();
            session.close();
        }
        System.out.println("Keitimo B620 įrašymo pabaiga");
        return 1;
    }
}
