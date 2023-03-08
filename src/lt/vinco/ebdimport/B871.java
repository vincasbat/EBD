package lt.vinco.ebdimport;

import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Pctref;
import lt.vinco.ebdimport.entity.PctrefId;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Node;

/**
 *
 * @author vbatulevicius
 */
public class B871 {

    public static int UpdateB871(String extidpatent, Node nodeB871, String status, String pak_data) throws XPathExpressionException {
        //pct pub number date
        System.out.println("pct pub date keitimo B871 įrašymo pradžia. extidpatent: " + extidpatent);

     XPath xpath = XPathFactory.newInstance().newXPath();


     String   idoper = "1366";  //  ???????????????????????????????? pctref?pct app number date

        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;


        System.out.println("Return: lgstappli: " + pdb.lgstappli);


        if (!status.equalsIgnoreCase("r")) {
            System.out.println("====    Statusas ne r  ");
            return 0;
        }

        System.out.println("+++++++ Tinkamas B871 keitimas +++++++++ " + idappli);

        if (!Langas.RASYTI_DB) {
            System.out.println("pct pub number date keitimo į db nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }

        String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB871);
        java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction


        String pnum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B870/B871/dnum/pnum/text()", nodeB871);
        String dtpctpubli = xpath.evaluate("/ep-bulletin/SDOBI/B800/B870/B871/date/text()", nodeB871);

        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            PctrefId pid = new PctrefId();
            pid.setIdappli(idappli);
            pid.setOdpctep(Short.valueOf("2")); //pct
            Pctref pref = (Pctref) session.get(Pctref.class, pid);
            if (pref != null) {
                pref.setNopubli(pnum);
                pref.setDtpctpubli(EBDimport.getSqldate(dtpctpubli));//871date

                session.update(pref);
            }

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
            history.setIdoper(idoper);  //pct pub number date B871-- ??????

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
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB871\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B871\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB871\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();
        }
        System.out.println("Keitimo pct pub number date B871 įrašymo pabaiga");
        return 1;
    }
}
