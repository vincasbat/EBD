/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

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
 * @author User
 */
public class B239 {
    //Revocation:2710000

    public static int UpdateB239(String extidpatent, Node nodeB239, String status, String pak_data) throws XPathExpressionException {
        // hist 1312   į 640
        //neaišku ar būna d???, į kokią lentelę rašyti revocation datą? dtpak_data?
        System.out.println("Revocation B239 įrašymo pradžia. extidpatent: " + extidpatent);

        Boolean rev1 = false;
        Boolean rev2 = false;
        String idoper = "1312";  //default idoper   ????



        XPath xpath = XPathFactory.newInstance().newXPath();
        String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB239);

        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;
        System.out.println("B007EP: " + B007EP);
        if (B007EP.contains("2710000")) {  //rev1
            rev1 = true;
            idoper = "1312";



            //Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
            Integer[] statusaiB2 = new Integer[]{625, 20, 2, 4, 5, 32, 34, 35, 30};
            Boolean statuse = false;
            for (Integer st : statusaiB2) {
                if (pdb.lgstappli.intValue() == st.intValue()) {
                    statuse = true;
                    break;
                }
            }
            System.out.println("Return: lgstappli: " + pdb.lgstappli);
            //
            if (!statuse) {
                System.out.println("Return: patento statusas ne {625, 20, 2, 4, 5,  32, 34, 35, 30}, o: " + pdb.lgstappli);
            }
        }//rev1



        if (B007EP.contains("2991210")) {   //rev2
            rev2 = true;
            idoper = "1374";
//Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
            Integer[] statusaiB2 = new Integer[]{621, 623, 20, 2, 4, 5, 32, 34, 35, 30};  //????????patikslinti
            Boolean statuse = false;
            for (Integer st : statusaiB2) {
                if (pdb.lgstappli.intValue() == st.intValue()) {
                    statuse = true;
                    break;
                }
            }
            System.out.println("Return: lgstappli: " + pdb.lgstappli);
            if (!statuse) {
                System.out.println("Return: patento statusas ne {621, 623,  20, 2, 4, 5,  32, 34, 35, 30}, o: " + pdb.lgstappli);
            }
        }//rev2


// if(!(rev1||rev2)) {
//   System.out.println("ne 2710000 arba 2991210" );  
//     return 0;
// }

        //Neaisku ka daryti, kai statusas yra d = ???????
        if (status.equalsIgnoreCase("d")) {
            System.out.println("====    Statusas=d   ???");
            return 0;
        }


        // į kokią lentelę rašyti revocation datą?
        if (!status.equalsIgnoreCase("n")) {
            System.out.println("====    Statusas ne n  ");
            return 0;
        }

        System.out.println("+++++++ Tinkamas B239 keitimas +++++++++ " + idappli);

        if (!Langas.RASYTI_DB) {
            System.out.println("Revocation į db nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }


        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB239);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction

           //Revocation data:
            String revocdate = xpath.evaluate("/ep-bulletin/SDOBI/B200/B230/B239/date/text()", nodeB239);
            java.sql.Date dtrevoc = EBDimport.getSqldate(revocdate);
            //? Kur rašyti revocation datą ?????????????????????????    history dtlegal


            //Įrašome naują lgstappli ptappli lentelėje:  
            Ptappli ptappli = (Ptappli) session.get(Ptappli.class, idappli);
            ptappli.setLgstappli(640);
            session.update(ptappli);



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
            history.setIdoper(idoper);  //Revocation

//Date dtop = new Date();
//            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
            java.sql.Date dtpak_data = EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));  // 12
            //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //
            history.setDtlegal(dtrevoc); //  pagal zenoną
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);
            session.save(history);

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB239\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B239\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB239\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();


        }
        System.out.println("Keitimo B239 įrašymo pabaiga");
        return 1;
    }
}
