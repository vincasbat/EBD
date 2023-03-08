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
public class B540 {
    //Title of invention keitimas
    //601,621, 623 -> nesikeicia
    //hist 1362

    public static int UpdateB540(String extidpatent, Node nodeB540, String status, String pak_data) throws XPathExpressionException {

        System.out.println("Title of invention B540 įrašymo pradžia. extidpatent: " + extidpatent);


// Boolean b1 = false;
// Boolean b2 = false;
// Boolean b3 = false;
//   

        XPath xpath = XPathFactory.newInstance().newXPath();
// String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB540);
//        System.out.println("B007EP: " + B007EP);
//        if(B007EP.contains("2991000")) {
//      b1 = true;
//        }
//         if(B007EP.contains("2991???????????????")) {
//         b2 = true;
//        }
//          if(B007EP.contains("2991020")) {
//        b2 = true;
//        }
// 
// if(!(b1||b2||b3))       {  
//   System.out.println("B007EP neturi 2991000, ???????????????, 2991020 (title): " + B007EP);
//   return 0;
// }         


        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;



        //Tikrinti ar patentas turi reikiamą teisinį statusą lgstappli:
        Integer[] statusaiB2 = new Integer[]{601, 621, 623};
        Boolean statuse = false;
        for (Integer st : statusaiB2) {
            if (pdb.lgstappli.intValue() == st.intValue()) {
                statuse = true;
                break;
            }
        }

        System.out.println("lgstappli: " + pdb.lgstappli);

        //
        if (!statuse) {
            System.out.println("Return: patento statusas ne {621, 623}, o: " + pdb.lgstappli);
            return 0;
        }






        //Neaisku ka daryti, kai statusas yra d = ???????
        if (status.equalsIgnoreCase("d")) {
            System.out.println("====    Statusas=d   ???");
            return 0;
        }



        // į kokią lentelę rašyti revocation datą?
        if (!status.equalsIgnoreCase("r")) {
            System.out.println("====    Statusas ne r  ");
            return 0;
        }

        System.out.println("+++++++ Tinkamas B540 keitimas +++++++++ " + idappli);

        if (!Langas.RASYTI_DB) {
            System.out.println("Title keitimo į db nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }



        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();



            Ptappli ptappli = (Ptappli) session.get(Ptappli.class, idappli);

            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB540);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction

            //Filing language:
            String langappli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B250/text()", nodeB540);
            System.out.println("filing lang langappli: " + langappli);
            String langpubli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B260/text()", nodeB540);
            System.out.println("Publ lang langpubli: " + langpubli);
            //angl. antraste:
            String engtitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[2]//text()", nodeB540);
            System.out.println(engtitle);
//de title
            String detitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[1]//text()", nodeB540);
            System.out.println(detitle);
//fr title
            String frtitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[3]//text()", nodeB540);
            System.out.println(frtitle);

            if (langappli.equalsIgnoreCase("en")) {    // o k1 jeigu filing language yra sv????????????????????????
                ptappli.setTitle(engtitle);
            }
            if (langappli.equalsIgnoreCase("de")) {
                ptappli.setTitle(detitle);
            }
            if (langappli.equalsIgnoreCase("fr")) {
                ptappli.setTitle(frtitle);
            }

            ptappli.setEngtitle(engtitle);
            ptappli.setStitle(engtitle.toUpperCase());

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
            history.setIdoper("1362");  //Title of invention

            Date dtop = new Date();
            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());//????????
            java.sql.Date dtpak_data = EBDimport.getSqldate(pak_data);       //? gal sita data rasyti?   

            history.setDtoper(dtdate_publ); //zenonas ? dtgrant?
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));
            //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  //???
            history.setDtlegal(dtpak_data); //zenonas: keitimo data?
            history.setLvlpubli(new Short("0")); // ????
            history.setDtaction(dtdate_publ);  //?@date-publ
            session.save(history);

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB540\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B540\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB540\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();
        }
        System.out.println("Keitimo B540 įrašymo pabaiga");
        return 1;
    }
}
