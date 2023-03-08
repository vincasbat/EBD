/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lt.vinco.ebdimport.entity.Classin;
import lt.vinco.ebdimport.entity.ClassinId;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Ptappli;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Vincas Batulevičius
 */
public class B510EP {
    //Klasifikacija:

    public static int UpdateB510EP(String extidpatent, Node nodeB510EP, String status, String pak_data) throws XPathExpressionException {
        System.out.println("Keitimo B510EP įrašymo pradžia. extidpatent: " + extidpatent + " " + status);



        XPath xpath = XPathFactory.newInstance().newXPath();
        String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", nodeB510EP);
        System.out.println("B007EP: " + B007EP);

        Boolean b1 = false;
        Boolean b2 = false;
        Boolean b3 = false;
        String idoper = "1360"; //default idoper ??

        List<String> idopers = new ArrayList<>();

        if (B007EP.contains("2990130")) {  //???????
            b1 = true;
            idoper = "1338";
            idopers.add(idoper);

        }
        if (B007EP.contains("2990980")) {  //???????
            b1 = true;
            idoper = "1358";
            idopers.add(idoper);
        }
        if (B007EP.contains("2990990")) {  //???????
            b1 = true;
            idoper = "1360";
            idopers.add(idoper);
        }

     




        PatentasDB pdb = EBDimport.YraDB(extidpatent);
        if (!pdb.YraDB) {
            System.out.println("Duomenų bazėje nėra patento  " + extidpatent);
            return 0;
        }
        String idappli = pdb.idappli;



        System.out.println("+++++++ Tinkamas B510EP patentas +++++++++ " + idappli + " " + pdb.lgstappli);


        if (!status.equalsIgnoreCase("r")) {
            System.out.println("====    Statusas ne r  ");
            return 0;
        }


        if (!Langas.RASYTI_DB) {
            System.out.println("Klasifikacijos keitimo į DB nerašome. " + idappli + " " + pdb.lgstappli);
            return 0;
        }



        Session session = null;
        String pagr_klas = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();


            String date_publ = xpath.evaluate("/ep-bulletin/@date-publ", nodeB510EP);
            java.sql.Date dtdate_publ = EBDimport.getSqldate(date_publ); //history dtaction 
           Trinti.DeleteClassif(idappli);

            //Klasifikacijos:
            List<Classif> classifs = new ArrayList<Classif>();
            String sequence = "0";
            Object result = xpath.evaluate("/ep-bulletin/SDOBI/B500/B510EP/child::node()", nodeB510EP, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Classif classif = new Classif();
                    String klasif = nodes.item(i).getFirstChild().getTextContent().substring(0, 19).trim();
                    String klasif_metai = nodes.item(i).getFirstChild().getTextContent().substring(19, 27);
                    String first = nodes.item(i).getFirstChild().getTextContent().substring(28, 29);
                    String sdtaction = nodes.item(i).getFirstChild().getTextContent().substring(30, 38);

                    String inventive = nodes.item(i).getFirstChild().getTextContent().substring(29, 30);
                    if (inventive.equalsIgnoreCase("N")) {
                        classif.setTyclassif(Short.valueOf("2"));
                    }
                    if (inventive.equalsIgnoreCase("I")) {
                        classif.setTyclassif(Short.valueOf("1"));
                    }

                    System.out.print("Klasif:  " + klasif + "   " + klasif_metai + " " + first);
                    System.out.print(" dtaction:  " + sdtaction);
                    Boolean bfirst = first.equalsIgnoreCase("F");
                    classif.setIpcclass(klasif);
                    classif.setIpcversionindicator(klasif_metai);
                    NamedNodeMap atrs = nodes.item(i).getAttributes();
                    int len = atrs.getLength();
                    for (int j = 0; j < len; j++) {
                        Attr attr = (Attr) atrs.item(j);
                        String nodename = attr.getNodeName();
                        String nodevalue = attr.getNodeValue();
                        System.out.println(" " + nodename + "=\"" + nodevalue + "\"");
                        if (nodename.equalsIgnoreCase("sequence")) {
                            sequence = nodevalue;
                        }
                        classif.setSequence(sequence);
                    }//for 
                    classif.setSdtaction(sdtaction);
                    if (bfirst) {
                        pagr_klas = klasif;
                        classif.setSymbpos(Short.valueOf("1"));
                    } else {
                        classif.setSymbpos(Short.valueOf("2"));
                    }
                    classifs.add(classif);
                } //for
            }//if not null

            System.out.println("pagr_klas: " + pagr_klas);


//Įrašome naują ipcmclass ptappli lentelėje:  
            Ptappli ptappli = (Ptappli) session.get(Ptappli.class, idappli);
            ptappli.setIpcmclass(pagr_klas);
            session.update(ptappli);


            //Įrašome į klasifikacijoos lentelę kitas klases:         
            for (int i = 0; i < classifs.size(); i++) {
                ClassinId clid = new ClassinId();
                clid.setIdappli(idappli);
                clid.setIpcclass(classifs.get(i).getIpcclass());

                Classin classin = new Classin();
                classin.setId(clid);
                classin.setIpcversion(classifs.get(i).getIpcversion());
                classin.setOdclass(classifs.get(i).getSequenceShort());
                classin.setOdlink(Short.valueOf("0"));      //----------------  ??? pasitikslinti
                classin.setTyipcclass(Short.valueOf("0"));// pagal Zenoną tn 0
                classin.setDtversion(classifs.get(i).getDtversion());
                classin.setSymbpos(classifs.get(i).getSymbpos());
                classin.setTyclassif(classifs.get(i).getTyclassif());
                classin.setDtaction(EBDimport.getSqldate(classifs.get(i).getSdtaction()));   //????pak_data?
                classin.setStclassif(Short.valueOf("1"));
                classin.setScclassif(Short.valueOf("1"));
                classin.setOrigin("EP"); //?


                session.save(classin);
                System.out.println("Is classif: " + classifs.get(i).toString());
            }    //for classisfs


//EBDimport.log


            for (String idop : idopers) {

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
                history.setIdoper(idop);  //Klasifikacijos pakeitimas


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

            }//for idopers           

            session.getTransaction().commit();
            EBDimport.logkeit.log(Level.INFO, "Įrašytas keitimas\tB510EP\t" + extidpatent);
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            EBDimport.log.log(Level.FINE, "Klaida įrašant keitimą B510EP\tEP" + extidpatent);
            EBDimport.logkeit.log(Level.INFO, "Įvyko klaida\tB510EP\t" + extidpatent);
        } finally {
//    session.flush();
            session.close();


        }


        System.out.println("Keitimo B510EP įrašymo pabaiga " + extidpatent);
        return 1;
    } //update510EP classification  
}
