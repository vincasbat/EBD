/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static lt.vinco.ebdimport.Langas.sheet;
import lt.vinco.util.HibernateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Vincas Batulevičius
 */
public class B1beparent {

    static File flog = null;
  public  static Logger log = null;
    static FileHandler hand = null;

    public static void createLogger() throws IOException {

        flog = new File("logai");

        if (!flog.exists()) {
            flog.mkdir();
        }
        if (!flog.exists()) {
            throw new IOException("Turi būti sukurtas log aplankas logai");
        }
        hand = new FileHandler("logai\\div_be_par.log");
        log = Logger.getLogger(EBDimport.class.getName());
        log.addHandler(hand);
        SimpleFormatter formatter = new SimpleFormatter();
        hand.setFormatter(formatter);
        // log.warning("Bandau logus");
        //log.log(Level.SEVERE,"My first log");
    }

    public static int TestFile(File file)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, FileNotFoundException {




        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(file.getAbsolutePath());
        XPath xpath = XPathFactory.newInstance().newXPath();

        String doknr = xpath.evaluate("/ep-bulletin/@doc-number", doc);
        String str = "\n\nDok. Nr. " + doknr;
        System.out.println(str);
        str = null;
        System.out.println(file.getName());

        
        str = xpath.evaluate("/ep-bulletin/@country", doc);
        System.out.println("ep-bulletin/@country: " + str);
        if (!str.equalsIgnoreCase("EP")) {
            return 0;
        }
        str = null;

//(97):  /ep-bulletin/@kind
        str = xpath.evaluate("/ep-bulletin/@kind", doc);
        System.out.println("ep-bulletin/@kind: " + str);
// Ar reikia visų B1, B2, B3  ????????
        if (str.equalsIgnoreCase("B1")) //? B3
        {
            System.out.println("Kind code: " + str);
        } else {
            return 0;
        }
        str = null;

//EURO-PCT applications for which no EPO A-document published
//4 European, 5 EuroPCT (B860 tb netuscias)
        String PCTfilingLang = null;
        int pttyappli = 0;
        str = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B003EP/text()", doc);
        System.out.println("ep-bulletin/B003EP (EURO-PCT no A docs): " + str);
        if (str.equalsIgnoreCase("*")) {
            pttyappli = 5; //EuroPCT   
            System.out.println("EuroPCT patentas *(5)");
//Čia nuskaityti iš B860 PCT duomenis: PCT doknr., app.nr, filing date, filing lang:
            String PCTanum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B861/dnum/anum/text()", doc);
            String PCTdate = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B861/date/text()", doc);
            PCTfilingLang = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B862/text()", doc);
            System.out.println("ep-bulletin/B003EP (EURO-PCT): " + PCTanum + " " + PCTdate + " " + PCTfilingLang);
        } else {
            pttyappli = 4;//European patent
            System.out.println("Europatentas (4)");
        }
        str = null;

// Designated contracting states B840/ctry:
        Boolean LTdesignatedState = false;
        Object result = xpath.evaluate("/ep-bulletin/SDOBI/B800/B840/ctry/text()", doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            //    System.out.println(nodes.item(i).getNodeValue());
            if ("LT".equalsIgnoreCase(nodes.item(i).getNodeValue().toString())) {
                System.out.println(nodes.item(i).getNodeValue() + "     is contracting state.");
                LTdesignatedState = true;
            }
        }


        //B844EP, B845EP - extended state data ctry, date of receipt of payment
        result = xpath.evaluate("/ep-bulletin/SDOBI/B800/B844EP/child::node()", doc, XPathConstants.NODESET);  // /B845EP/ctry/text()
        NodeList extendedStates = (NodeList) result;
        Boolean LTextendedState = false;
        String B845ep_data = null;
        if (extendedStates != null) {
            for (int i = 0; i < extendedStates.getLength(); i++) {
//    System.out.println(nodes.item(i).getNodeValue());
                str = extendedStates.item(i).getFirstChild().getTextContent();
                if ("LT".equals(str)) {
                    LTextendedState = true;
                    B845ep_data = extendedStates.item(i).getLastChild().getTextContent();
                    System.out.println("Extended LT b845ep-ctry: " + LTextendedState);
                    System.out.println("Extended LT b845ep-date (of receipt of payment): " + B845ep_data);

                }//if
            }//for
        }//if not null


        if (LTdesignatedState || LTextendedState) {
            System.out.println("Tai yra Lietuvoje išplėstas patentas_____LT");
        } else {
            return 0;
        }

        // dar tikrinti 2004 datą ir priskirti atitinkamą statusą 621 ir ??? 


// Tikrinimas baigtas, ----------------- tvarkome  --------------------------------



        //(96):  /ep-bulletin/SDOBI/B200/B210  number assigned to the application (EPO nr.)
        String extidappli = null;
        extidappli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B210/text()", doc);
        System.out.println("App. number: " + extidappli);




        int kdpatent = 1;    // 1 ordinary, 2 addintional, 3 divisional, 6 replacement, 7 SPC, ?   
        str = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/pnum/text()", doc);
        if ((str != null) && (str.length() > 2)) {
            kdpatent = 3; //divisional application
            System.out.println("Dokumentas " + doknr + " yra divisional, failas: " + file.getName());
        }

        String anm = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/anum/text()", doc);
        if ((anm != null) && (anm.length() > 2)) {
            kdpatent = 3; //divisional application
            System.out.println("Dokumentas " + doknr + " yra divisional, failas: " + file.getName() + " " + anm);
        }



        if (kdpatent != 3) {
            return 0;
        }


        str = xpath.evaluate("/ep-bulletin/@status", doc);
        System.out.println("Statusas: " + str);

        if (str.equalsIgnoreCase("r")) {
            System.out.println("Dokumentas " + doknr + " yra keitimas.");
            //iškviesti failo updeito procedūrą:
            String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", doc);
            System.out.println("B007EP: " + B007EP);
            //keitimai atjungti:      UpdateFile(file, doknr);
            return 1;
        }//if "r"



        if (str.equalsIgnoreCase("d")) {
            System.out.println("Dokumentas " + doknr + " TURI D statusą!!!!!, ką daryti? ");
            return 1;
        }


        if (!str.equalsIgnoreCase("n")) {
            System.out.println("Dokumentas " + doknr + " neturi N statuso!!!!!, ką daryti? ");
            return 1;
        }





//Naujų atranka:
        if (pttyappli == 5) {
            System.out.println("Dokumentas " + doknr + " naujas ir PCT");
        }
        if (pttyappli == 4) {
            System.out.println("Dokumentas " + doknr + " naujas ir EP");
        }
//log.log(Level.INFO,"Naujas patentas Nr. " + doknr + ", failas " + file.getName());

//App filing date 
        int lgs = 0;
        str = null;
        str = xpath.evaluate("/ep-bulletin/SDOBI/B200/B220/date/text()", doc);
        int data2004 = Integer.parseInt(str);

        if (data2004 >= 20041201) {
            lgs = 623;
        } else {
            lgs = 621;
        }

        System.out.println("App. filing date b220-date: " + data2004);

        java.sql.Date dtappli = EBDimport.getSqldate(str);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException ex) {
            Logger.getLogger(EBDimport.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.sql.Date dtptexpi = EBDimport.addYears(date, 20);

        System.out.println("App. filing date+20 metu, dtptexpi: " + dtptexpi);


//Filing language:
        String langappli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B250/text()", doc);
        System.out.println("filing lang langappli: " + langappli);

        String langpubli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B260/text()", doc);
        System.out.println("Publ lang langpubli: " + langpubli);


        //angl. antraste:
        String engtitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[2]//text()", doc);
        System.out.println(engtitle);
//de title
        String detitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[1]//text()", doc);
        System.out.println(detitle);
//fr title
        String frtitle = xpath.evaluate("//ep-bulletin//SDOBI//B500//B540//B542[3]//text()", doc);
        System.out.println(frtitle);


//B140 dtgrant?":
        str = xpath.evaluate("/ep-bulletin/SDOBI/B100/B140/date/text()", doc);
        System.out.println("dtgrant (B140): " + str);
        java.sql.Date dtgrant = EBDimport.getSqldate(str);



        //Klasifikacijos:
        List<Classif> classifs = new ArrayList<Classif>();
        String sequence = "0", pagr_klas = null;
        result = xpath.evaluate("/ep-bulletin/SDOBI/B500/B510EP/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
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
                System.out.print("Klasif:  " + klasif + "   " + klasif_metai + " " + first + " " + inventive);
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




//(97):  /ep-bulletin/@date-publ
        str = xpath.evaluate("/ep-bulletin/@date-publ", doc);
        System.out.println("ep-bulletin/@date-publ: " + str);
        java.sql.Date date_pub_sql = EBDimport.getSqldate(str);



///ep-bulletin/SDOBI/B500/B560/B561/text/text()   //561  liartdoc
        result = xpath.evaluate("/ep-bulletin/SDOBI/B500/B560/B561/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        String liartdoc = "";
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                liartdoc += nodes.item(i).getTextContent() + "; ";
            }
            liartdoc = liartdoc.trim();
        }
        System.out.println("liartdoc: " + liartdoc);


        Object result1 = null;
        Object result2 = null;
        Object result3 = null;
        NodeList nodes1 = null;
        NodeList nodes2 = null;
        NodeList nodes3 = null;
        Object result4 = null;
        NodeList nodes4 = null;


//Tikriname agenta:
        //Attorney/agent:
        // (74):   /ep-bulletin/SDOBI/B700/B740/B741/snm...

        String agentsnm = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/snm/text()", doc);
        String agentstr = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/str/text()", doc);
        String agentcity = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/city/text()", doc);
        String agentctry = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/adr/ctry/text()", doc);
        String sfx = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/sfx/text()", doc);
        String agentiid = xpath.evaluate("/ep-bulletin/SDOBI/B700/B740/B741/iid/text()", doc);

        agentsnm = ((agentsnm != null) ? agentsnm : "").replace("\n", " ");
        agentstr = ((agentstr != null) ? agentstr : "").replace("\n", " ");
        agentcity = ((agentcity != null) ? agentcity : "").replace("\n", " ");
        agentctry = ((agentctry != null) ? agentctry : "").replace("\n", " ");
        String agentsfx = ((sfx != null) ? sfx : "").replace("\n", " ");
        agentiid = (agentiid != null) ? agentiid : "";






//     String snm=""; String  strt= ""; String city = ""; String ctry="";  
//              if (nodes1!=null)  snm = (nodes1.item(i)!=null)?nodes1.item(i).getNodeValue():"";







        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            System.out.println("Irasymas i baze...");

            String hql = null;
            Query q = null;

          

            //Division new:
            result = null;
            List<String> parent_pnums = new ArrayList<>();
            result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/pnum/child::node()", doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    parent_pnums.add(nodes.item(i).getTextContent());
                }
            }//if nodes!=null
            System.out.println("Parents: " + parent_pnums);



            PatentasDB pr;
            Boolean hasParent = false;
            int parentCount = 0;
            if (!parent_pnums.isEmpty()) {
                for (String pnm : parent_pnums) {
                    pr = EBDimport.YraDB(pnm);
                    if (pr.YraDB) {
                        System.out.println("Patentas EP" + doknr + " turi parent " + pr.idappli);
                        hasParent = true;
                        parentCount++;
                    }

                }//for




                System.out.println("Divisional patentas    " + doknr + " , failas " + file.getName() + " turi "
                        + parent_pnums.size() + " parent EP st36 faile");

                if (parentCount > 0) {
                    System.out.println("Divisional patentas    " + doknr + " , failas " + file.getName() + " turi "
                            + parentCount + " parent EP VPB duomenų bazėje");

                    if (parent_pnums.size() > parentCount) {
                        log.log(Level.INFO, "\tEP" + doknr + "\t " + file.getName() + " turi daugiau kaip vieną parent EP st36 faile, bet ne visus VPB DB");
                        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
      row.createCell(0).setCellValue("EP"+doknr);
       row.createCell(1).setCellValue(file.getName());
      row.createCell(2).setCellValue(" turi daugiau kaip vieną parent EP st36 faile, bet ne visus VPB DB");
                    }

                }




            }//!is empty  pnum


            result = null;
            List<String> parent_anums = new ArrayList<>();
            result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/anum/child::node()", doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    parent_anums.add(nodes.item(i).getTextContent());
                }
            }//if nodes!=null
            System.out.println("Parent anums: " + parent_anums);

            if (!parent_anums.isEmpty()) {
                for (String an : parent_anums) {

                    pr = EBDimport.YraDBextidappli(an);
                    if (pr.YraDB) {
                        System.out.println("Patentas EP" + doknr + " turi parent " + pr.idappli);
                        hasParent = true;
                        parentCount++;
                    }
                }//for
            }//if anums isempty

            String pap = " neturi nei vieno parent EP";
            if (parent_pnums.isEmpty() && (!parent_anums.isEmpty() && (!hasParent))) {
                pap = " faile parent tik anum!______";
            }


            if (!hasParent) {
                System.out.println("Divisional patentas  " + doknr + " , failas " + file.getName() + " neturi nei vieno parent EP duomenų bazėje.");
                log.log(Level.INFO, "\tEP" + doknr + "\t " + file.getName() + pap);
                   
      Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
      row.createCell(0).setCellValue("EP"+doknr);
       row.createCell(1).setCellValue(file.getName());
      row.createCell(2).setCellValue(pap);
            }


           

            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įrašant į duomenų bazę įvyko klaida. Dokumento nr. " + doknr + ", failas: " + file.getName());
        }

        return 0;
    }//testfile
}//class
