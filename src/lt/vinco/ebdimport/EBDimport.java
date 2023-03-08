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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import static lt.vinco.ebdimport.Langas.sheet;
import lt.vinco.ebdimport.entity.Agent;
import lt.vinco.ebdimport.entity.Classin;
import lt.vinco.ebdimport.entity.ClassinId;
import lt.vinco.ebdimport.entity.Division;
import lt.vinco.ebdimport.entity.DivisionId;
import lt.vinco.ebdimport.entity.Extstate;
import lt.vinco.ebdimport.entity.ExtstateId;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.HistoryId;
import lt.vinco.ebdimport.entity.Invent;
import lt.vinco.ebdimport.entity.Inventor;
import lt.vinco.ebdimport.entity.Jcs_owner;
import lt.vinco.ebdimport.entity.Own;
import lt.vinco.ebdimport.entity.OwnId;
import lt.vinco.ebdimport.entity.Pctref;
import lt.vinco.ebdimport.entity.PctrefId;
import lt.vinco.ebdimport.entity.Priority;
import lt.vinco.ebdimport.entity.PriorityId;
import lt.vinco.ebdimport.entity.Ptappli;
import lt.vinco.ebdimport.entity.Represent;
import lt.vinco.ebdimport.entity.RepresentId;
import lt.vinco.util.HibernateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Vincas Batulevičius
 */
public class EBDimport {
    
 
 
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
        hand = new FileHandler("logai\\ebd.log");
        log = Logger.getLogger(EBDimport.class.getName());
        log.addHandler(hand);
        SimpleFormatter formatter = new SimpleFormatter();
        hand.setFormatter(formatter);
     }
    
   
    static Logger logkeit = null;
    static FileHandler handkeit = null;

    public static void createKeitLogger() throws IOException {
      handkeit = new FileHandler("logai\\keitimai.log");
        logkeit = Logger.getLogger(Keitimai.class.getName());
        logkeit.addHandler(handkeit);
        SimpleFormatter formatter = new SimpleFormatter();
        handkeit.setFormatter(formatter);
     }
    
    
    
    
    
    
    

    public static int ImportFile(File file)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, FileNotFoundException {
        
   
       
        
// log.log(Level.INFO, "Pradedamas tvarkyti {0}", file.getName());
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

        if (str.equalsIgnoreCase("B1")) 
        {
            System.out.println("Kind code turi būti b1: " + str);
        } else {
            return 0;
        }
        str = null;

//EURO-PCT applications for which no EPO A-document published
//4 European, 5 EuroPCT (B860 tb netuscias)
String PCTfilingLang=null;
        int pttyappli = 0;
        str = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B003EP/text()", doc);
        System.out.println("ep-bulletin/B003EP (EURO-PCT no A docs): " + str);
        
        
   String pnum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B870/B871/dnum/pnum/text()", doc);
   String anum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B861/dnum/anum/text()", doc);
   Boolean isPCT = false;
   int lpnum = 0;
   int lanum = 0;
   if (pnum!=null)  { lpnum = pnum.length();}  
   if (anum!=null)  { lanum = anum.length();}     
   if((lpnum>0) || (lanum>0)) isPCT = true;     
        
       
        if (str.equalsIgnoreCase("*")||isPCT) {
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
  if(extendedStates!=null)      {
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

       

// Tikrinimas baigtas, ----------------- tvarkome  --------------------------------
        
        

      
        String extidappli = null;
        extidappli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B210/text()", doc);
        System.out.println("App. number: " + extidappli); 

       
        
         
 int kdpatent = 1;    // 1 ordinary, 2 addintional, 3 divisional, 6 replacement, 7 SPC, ?   
            str = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/pnum/text()", doc);
            if ((str != null)&&(str.length()>2)){
            kdpatent = 3; //divisional application
            System.out.println("Dokumentas " + doknr + " yra divisional, failas: "+file.getName());
            }
 
            String anm = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/anum/text()", doc);
            if ((anm != null)&&(anm.length()>2)){
            kdpatent = 3; //divisional application
            System.out.println("Dokumentas " + doknr + " yra divisional, failas: "+file.getName()+" "+anm);
            }
    
   
     if(Langas.TIK_DIVISION&&kdpatent!=3) return 0;
   
     
      str = xpath.evaluate("/ep-bulletin/@status", doc);
        System.out.println("Statusas: " + str);
        
        if (str.equalsIgnoreCase("r")) {
            if(Langas.TIK_DIVISION)     
                return 0; else {
            System.out.println("Dokumentas " + doknr + " yra nenaujas.");
            //iškviesti failo updeito procedūrą:
              String B007EP = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B007EP/text()", doc);
        System.out.println("B007EP: " + B007EP);

            UpdateFile(file, doknr);
            return 1;
        }
        }
        
 //O jei statusai d, n?????         
         if (str.equalsIgnoreCase("d")) {
  System.out.println("Dokumentas " + doknr + " TURI D statusą!!!!!, ką daryti? ");
                   return 1;
        }   
        
      
      if (!str.equalsIgnoreCase("n")) {
  System.out.println("Dokumentas " + doknr + " neturi N statuso!!!!!, ką daryti? ");
                   return 1;
        }
     
        
        
        
 //Naujas patentas:       
        
    if(Langas.TIK_DIVISION) System.out.println("Naujas divisional patentas.: "+file.getName());     
   
        

//Naujų atranka:
if(pttyappli == 5) System.out.println("Dokumentas " + doknr + " naujas ir PCT");
if(pttyappli == 4) System.out.println("Dokumentas " + doknr + " naujas ir EP");
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

        java.sql.Date dtappli = getSqldate(str);
        
         SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException ex) {
            Logger.getLogger(EBDimport.class.getName()).log(Level.SEVERE, null, ex);
        }
       java.sql.Date dtptexpi = addYears(date, 20);
        
        System.out.println("App. filing date+20 metu, dtptexpi: " + dtptexpi); 
        

//Filing language:
        String langappli = xpath.evaluate("/ep-bulletin/SDOBI/B200/B251EP/text()", doc);  
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



        str = xpath.evaluate("/ep-bulletin/SDOBI/B100/B140/date/text()", doc);
        System.out.println("dtgrant (B140): " + str);
        java.sql.Date dtgrant = getSqldate(str);



        //Klasifikacijos:
        List<Classif> classifs = new ArrayList<Classif>();
        String sequence = "0", pagr_klas = null;
        result = xpath.evaluate("/ep-bulletin/SDOBI/B500/B510EP/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        if(nodes!=null) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Classif classif = new Classif();
            String klasif = nodes.item(i).getFirstChild().getTextContent().substring(0, 19).trim();
            String klasif_metai = nodes.item(i).getFirstChild().getTextContent().substring(19, 27);
           String first = nodes.item(i).getFirstChild().getTextContent().substring(28, 29);
           String sdtaction = nodes.item(i).getFirstChild().getTextContent().substring(30, 38);
           
           String inventive = nodes.item(i).getFirstChild().getTextContent().substring(29, 30);
           if(inventive.equalsIgnoreCase("N")) classif.setTyclassif(Short.valueOf("2"));
           if(inventive.equalsIgnoreCase("I")) classif.setTyclassif(Short.valueOf("1"));
           
           System.out.print("Klasif:  " + klasif + "   " + klasif_metai+" "+first+" "+inventive);
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
            }
            else {
              classif.setSymbpos(Short.valueOf("2"));
            }
            classifs.add(classif);
        } //for
        }//if not null
        
        System.out.println("pagr_klas: " + pagr_klas);




//(97):  /ep-bulletin/@date-publ
        str = xpath.evaluate("/ep-bulletin/@date-publ", doc);
        System.out.println("ep-bulletin/@date-publ: " + str);
        java.sql.Date date_pub_sql = getSqldate(str);



///ep-bulletin/SDOBI/B500/B560/B561/text/text()   //561  liartdoc
        result = xpath.evaluate("/ep-bulletin/SDOBI/B500/B560/B561/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
         String liartdoc = "";
         if(nodes!=null){
        for (int i = 0; i < nodes.getLength(); i++) {
              liartdoc += nodes.item(i).getTextContent() + "; ";
             }
         liartdoc =  liartdoc.trim();
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
         
         
   System.out.println("agentsnm ir agentsfx: "+agentsnm +" "+agentsfx);      
       
         
 //Division:
         result = null;
        List<String> parent_pnums = new ArrayList<>();
        result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/pnum/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                parent_pnums.add(nodes.item(i).getTextContent());
            }
        }//if nodes!=null
       System.out.println("Parents: "+parent_pnums);   
              
      
        result = null;
        List<String> parent_anums = new ArrayList<>();
        result = xpath.evaluate("/ep-bulletin/SDOBI/B600/B620/parent/pdoc/dnum/anum/child::node()", doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                parent_anums.add(nodes.item(i).getTextContent());
            }
        }//if nodes!=null
       System.out.println("Parent anums: "+parent_anums);        
         
         
         
 
       
  Boolean klaida = false;

//------------------- ĮRAŠYMAS Į DB  ------------------------------  

       if (!Langas.RASYTI_DB) { 
           log.log(Level.INFO, "Tinkamas patentas\tEP"+doknr+"\t"+file.getName());
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
       row.createCell(0).setCellValue("EP"+doknr);
       row.createCell(1).setCellValue(file.getName());
       return 0; 
            }
        
   PatentasDB pdb =  YraDB(doknr);
   //čia tikrinti ar statusas 653: (yraDB ). jei 653 tai viską ištrinti ir rašyti iš naujo:    
  
  if(pdb.YraDB && (pdb.lgstappli==653)) { Trinti.DeleteFromDB(pdb.idappli);  
  System.out.println("Duomenų bazėje patento statusas 653: "+doknr ); 
  }
  
  pdb = null;
  pdb = YraDB(doknr);  //įsitikiname, kad patentas st653 buvo ištrintas
  
        if (pdb.YraDB) {
            System.out.println("Duomenų bazėje jau yra įrašytas patentas " + doknr);
            //log.log(Level.WARNING, "Į DB jau buvo įrašytas\t"+ file.getName()+"\tEP"+doknr); 
            return 0;
        } else {
//Tikriname ar nėra paraiškos, jei yra triname:  
            pdb = null;
            pdb = YraDBextidappli(extidappli);
            if (pdb.YraDB) {
                Trinti.DeleteFromDB(pdb.idappli);
            }
        }
       
       
       
       Session session = null; 
        try {
             session = HibernateUtil.getSessionFactory().openSession();  //getCurrentSession?????
            session.beginTransaction();

            System.out.println("Irasymas i baze...");
            
             String hql = null;
              Query q = null;
              
              
           

              
              
              
              
              
              

      
//Randame idappli:             
            hql = "select max(p.k_ptappli)  from Ptappli p";
            List list = session.createQuery(hql).list();
            int k_ptappli = ((Integer) list.get(0)).intValue();
            String idappli = "X" + (k_ptappli + 1);
            System.out.println("k_ptappli: " + k_ptappli);
            System.out.println("idappli: " + idappli);
            
      
            
             

            //Division new:
            Boolean hasParent = false;
             
            if (!parent_pnums.isEmpty()) {
                for (String pnm : parent_pnums) {
                    hasParent = false;
                    PatentasDB pr = YraDB(pnm);
                    if (pr.YraDB) {
                        hasParent = true;
                    }
                    
                    
                    if (hasParent && Langas.TIK_DIVISION) {  //jei yra parent, tai patentas nebus įrašomas, įrašys spiritas
                        session.getTransaction().rollback();
                        System.out.println("Nebus įrašytas, nes pasirinkta tik_division be parent");
                        return 0;
                    }//if tik_division
                    
                    
        if(hasParent) {  //jei yra parent, užpildome Division lentelę
                    //įrašome į Division:
                    DivisionId divid = new DivisionId();
                    divid.setIdappli(pr.idappli);  //  ????
                    divid.setIdapplidiv(idappli);
                    Division div = new Division();
                    div.setId(divid);
                    session.save(div);
                }// if
      }//for
   }//is empty          
            
            
            
            
            
  
            
    Integer epidagent = null; 
             Long countagent = null;
          //    Integer idagent = null;
              Integer idagent = null;
    //jei faile yra agento duomenys:
      if(!agentiid.equalsIgnoreCase(""))
             {  
            //Tikriname ar db jau yra agentas:
            // epidagent = Long.parseLong(agentiid);
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
         
         
         
           
            

            Ptappli ptappli = new Ptappli();
            ptappli.setIdappli(idappli);
            //      Įrašome duomenis į ptappli:  
            ptappli.setIdagent(idagent);     
            ptappli.setIdexaminer(1);
            ptappli.setIdpatent(doknr);
            ptappli.setNovolume(0);  //visada 0
            ptappli.setDtappli(dtappli); //b220/date
          
            ptappli.setDtrecept(dtappli);  // = dtappli
            //ptappli.setRfappli, dtRfappli?? nereikia
            int lang = 6;  
            if (langappli.equalsIgnoreCase("en")) {
                lang = 2; //kitos kalbos?
            }
            if (langappli.equalsIgnoreCase("de")) {
                lang = 3;
            }
            if (langappli.equalsIgnoreCase("fr")) {
                lang = 4;
            }
            if (langappli.equalsIgnoreCase("ru")) {
                lang = 5;
            }
            if (langappli.equalsIgnoreCase("lt")) {
                lang = 1;  //???????? national
            }
            ptappli.setLangappli(lang);  //B250 arba <ep-bulletin lang="fr"
            //     1 nat, 2 foreigh, 3 pct, 4 European, 5 EuroPCT    
            ptappli.setPttyappli(pttyappli); //turi buti 4 arba 5
            ptappli.setTyinvent(3); // 1 employer 2 employee 3 undefined; visur 3

            if (langappli.equalsIgnoreCase("en")) {
                ptappli.setTitle(engtitle);
            }
            if (langappli.equalsIgnoreCase("de")) {
                ptappli.setTitle(detitle);
            }
            if (langappli.equalsIgnoreCase("fr")) {
                ptappli.setTitle(frtitle);
            }

            ptappli.setEngtitle(engtitle);
            ptappli.setStitle(engtitle.toUpperCase()); //???
            ptappli.setLvsecret(1);
            // ptappli.setdtopen nereikia
            ptappli.setNbclaim(0); //???
            ptappli.setModabstract(1); //visada 1
            ptappli.setNbdraw(0);//
            ptappli.setVipcmclass(8);//visada 8
            ptappli.setIpcmclass(pagr_klas);
            ptappli.setTyapplic(1);//??? 1 none, 2 ordinary, 4 compulsory visada 1
            ptappli.setDtaccept(dtappli);// = dtappli
            ptappli.setIdlocation("232");//visada 232
           
            ptappli.setDtlgstappli(date_pub_sql);

            ptappli.setLgstappli(lgs);   //623 arba 621, jei pries 20041201
            ptappli.setDttcstappli(date_pub_sql);
            ptappli.setTcstappli(106);
            ptappli.setDtupdate(date_pub_sql);//??????
            ptappli.setIduser(16);
            ptappli.setDtgrant(dtgrant);



            ptappli.setDtptexpi(dtptexpi); 
            //stpatent, dtstpatent
            ptappli.setTyadrsce(2);
            ptappli.setExtidappli("EP" + extidappli);
            ptappli.setExtidrecept("y4 n03");
            ptappli.setExtidpatent("EP" + doknr);
            ptappli.setDtnextpay(dtgrant);  //B140, dtgrant? 
            ptappli.setKdpatent(kdpatent);
            ptappli.setOldlgstappli(600); //????
            ptappli.setDtadvert(null);//????????
            //     ptappli.setCntrenew(); //nera klaseje 0, 5
            ptappli.setOfferlic(0);//????? 0 no, 1 yes
            ptappli.setStprint(1);// 1, -1
            ptappli.setLiartdoc(liartdoc);
            session.save(ptappli);
            
            
            
     
        
            
            
           
           
            
    //Įrašome agentą į Represent lentelę, nes jau turime idappli ir idagent:
             if((!agentiid.equalsIgnoreCase(""))&&(idagent>0))  {
            RepresentId rid = new RepresentId();
            rid.setIdagent(idagent.intValue());  //----------------
            rid.setIdappli(idappli);
                 Represent rp = new Represent();
                 rp.setId(rid);
             rp.setOdagent(Short.valueOf("1"));
             rp.setLocked(Short.valueOf("0"));
             session.save(rp);
             }//if
           
          
            
            

            
            
            

            Date dtop = new Date();
            java.sql.Date dtoper = new java.sql.Date(dtop.getTime());


            //Įrašome į klasifikacijoos lentelę kitas klases:         
            for (int i = 0; i < classifs.size(); i++) {
                ClassinId clid = new ClassinId();
                clid.setIdappli(idappli);
                clid.setIpcclass(classifs.get(i).getIpcclass());

                Classin classin = new Classin();
                classin.setId(clid);
                classin.setIpcversion(classifs.get(i).getIpcversion());
                classin.setOdclass(classifs.get(i).getSequenceShort());
                classin.setOdlink(Short.valueOf("0"));     
                classin.setTyipcclass(Short.valueOf("0")); //Zenonas sako, kad turi būti 0
                classin.setDtversion(classifs.get(i).getDtversion());
                classin.setSymbpos(classifs.get(i).getSymbpos());
                classin.setTyclassif(classifs.get(i).getTyclassif());
                classin.setDtaction(getSqldate(classifs.get(i).getSdtaction()));   // 
                classin.setStclassif(Short.valueOf("1"));
                classin.setScclassif(Short.valueOf("1"));
                classin.setOrigin("EP"); //?


                session.save(classin);
                System.out.println("Is classif: " + classifs.get(i).toString());
            }    //for classisfs








            //Inventors:
// (72):  /ep-bulletin/SDOBI/B700/B720/...
            result1 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/snm/text()", doc, XPathConstants.NODESET);
            nodes1 = (NodeList) result1;
            result2 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/str/text()", doc, XPathConstants.NODESET);
            nodes2 = (NodeList) result2;
            result3 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/city/text()", doc, XPathConstants.NODESET);
            nodes3 = (NodeList) result3;
            result4 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B720/B721/adr/ctry/text()", doc, XPathConstants.NODESET);
            nodes4 = (NodeList) result4;

            for (int i = 0; i < nodes1.getLength(); i++) {
                
               String snm=""; String  strt= ""; String city = ""; String ctry="";  
              if (nodes1!=null)  snm = ((nodes1.item(i)!=null)?nodes1.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes2!=null)      strt = ((nodes2.item(i)!=null)?nodes2.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes3!=null)    city =  ((nodes3.item(i)!=null)?nodes3.item(i).getNodeValue():"").replace("\n", " ");
              if (nodes4!=null)   ctry = (nodes4.item(i)!=null)?nodes4.item(i).getNodeValue():"";

                str = "Inventor_: " + snm + ", " + strt  + ", " + city + ", " + ctry;
       //         str = str.replace("\n", " ");
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




            //Prioritetai
            result1 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B310/text()", doc, XPathConstants.NODESET);
            nodes1 = (NodeList) result1;
            result2 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B320/date/text()", doc, XPathConstants.NODESET);
            nodes2 = (NodeList) result2;
            result3 = xpath.evaluate("/ep-bulletin/SDOBI/B300/B330/ctry/text()", doc, XPathConstants.NODESET);
            nodes3 = (NodeList) result3;
            for (int i = 0; i < nodes1.getLength(); i++) {
                
        String b310=""; String  b320= ""; String b330 = "";   
              if (nodes1!=null)  b310 = (nodes1.item(i)!=null)?nodes1.item(i).getNodeValue():"";          
              if (nodes2!=null)  b320 = (nodes2.item(i)!=null)?nodes2.item(i).getNodeValue():""; 
              if (nodes3!=null)  b330 = (nodes3.item(i)!=null)?nodes3.item(i).getNodeValue():""; 
                
                str = "Prioritetas: " + nodes1.item(i).getNodeValue() + ", " + formatDate(nodes2.item(i).getNodeValue())
                        + ", " + nodes3.item(i).getNodeValue();
                System.out.println(str);
                str = null;


                PriorityId pid = new PriorityId();
                pid.setIdappli(idappli);
                pid.setOdprio((short) (i + 1));
                Priority prio = new Priority();
                prio.setId(pid);
                prio.setDtprio(getSqldate(b320));//data
                prio.setIdcountry(b330);//ctry
                prio.setNoprio(b310);
                prio.setRmprio("");
                prio.setStprio(Short.parseShort("1"));//zenonas sake, kad tb 1
                prio.setTyprio(Short.parseShort("1")); ///???
                session.save(prio);
            }// for prio




//Savininkų įrašymas į db: 
            //73       grantees  /ep-bulletin/SDOBI/B700/B730/B731/snm/text()   //savininkai, gb ne vienas
            result1 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/snm/text()", doc, XPathConstants.NODESET);
            nodes1 = (NodeList) result1;
            result2 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/str/text()", doc, XPathConstants.NODESET);
            nodes2 = (NodeList) result2;
            result3 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/city/text()", doc, XPathConstants.NODESET);
            nodes3 = (NodeList) result3;
            result4 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/adr/ctry/text()", doc, XPathConstants.NODESET);
            nodes4 = (NodeList) result4;
            Object result5 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/iid/text()", doc, XPathConstants.NODESET);
            NodeList nodes5 = (NodeList) result5;
            Object result6 = xpath.evaluate("/ep-bulletin/SDOBI/B700/B730/B731/irf/text()", doc, XPathConstants.NODESET);
            NodeList nodes6 = (NodeList) result6;

            if (nodes1.getLength() > 0) {
                for (int i = 0; i < nodes1.getLength(); i++) {

                    //Tikriname ar db jau yra savininkas:
                    Integer epidowner = Integer.parseInt(nodes5.item(i).getNodeValue());
                    hql = "select count(*)  from Jcs_owner jo where jo.epidowner = :epidowner ";
                    q = session.createQuery(hql);
                    q.setParameter("epidowner", epidowner);
                    Long countown = (Long) q.uniqueResult();
                    System.out.println("count j own:  " + countown);
                    Integer idowner = 0;
                    if (countown > 0) {
// // randame idagen pagal epidagent:
                        hql = "select jo.idowner  from Jcs_owner jo where jo.epidowner = :epidowner ";
                        q = session.createQuery(hql);
                        q.setParameter("epidowner", epidowner);
                        idowner = (Integer) q.uniqueResult();
                        System.out.println("Savininkas jau yra, jo iid ir idowner: " + epidowner + "  " + idowner);
                    } else {
// //irasome nauja savininką ir randame jo idowner:
                        Jcs_owner own = new Jcs_owner();
                        String granteeStreet = null;

                         if ((nodes2.item(i)!=null)) {
                            System.out.println("NODES2length: " + nodes2.getLength() + " i: " + i);
                            granteeStreet = nodes2.item(i).getNodeValue().toString();
                        } else {granteeStreet = "";}
                        
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
                    str = null;
                }//for grantees
            }//if nodes1.getLength()>0




//Extended states i db:
// NodeList  extendedStates = (NodeList) result; //deklaruota anksčiau
//Boolean LTextendedState = false;  
//String B845ep_data = null;
            if ((extendedStates != null) && (extendedStates.getLength() > 0)) {
                for (int i = 0; i < extendedStates.getLength(); i++) {
//    System.out.println(nodes.item(i).getNodeValue());
                    str = extendedStates.item(i).getFirstChild().getTextContent();
                    B845ep_data = extendedStates.item(i).getLastChild().getTextContent();
                    System.out.println("i db extended state " + str + " b845ep-date (of receipt of payment): " + B845ep_data);
                    ExtstateId extid = new ExtstateId();
                    extid.setIdappli(idappli);
                    extid.setIdcountry(str);
                    Extstate exs = new Extstate();
                    exs.setId(extid);
                    exs.setDtrpay(getSqldate(B845ep_data));
                    session.save(exs);
                }//for
            }// if

            
          
            
            
            

          


            // PCTREF:   pttyappli = 4;//European patent  pttyappli =4, PCT pttyappli = 5. 
            pnum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B870/B871/dnum/pnum/text()", doc);
            String dtpctappli = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B861/date/text()", doc);
            String dtb430 = xpath.evaluate("/ep-bulletin/SDOBI/B400/B430/date/text()", doc);
            anum = xpath.evaluate("/ep-bulletin/SDOBI/B800/B860/B861/dnum/anum/text()", doc);
            String dtpctpubli = xpath.evaluate("/ep-bulletin/SDOBI/B800/B870/B871/date/text()", doc);
            
            
              lang = 5;  // 
            if (langappli.equalsIgnoreCase("en")) {
                lang = 1; //kitos kalbos?
            }
            if (langappli.equalsIgnoreCase("de")) {
                lang = 3;
            }
            if (langappli.equalsIgnoreCase("fr")) {
                lang = 2;
            }
            if (langappli.equalsIgnoreCase("ru")) {
                lang = 4;
            }
            
            
            
          int    lng = 5;  // 
            if (langpubli.equalsIgnoreCase("en")) {
                lng = 1;
            }
            if (langpubli.equalsIgnoreCase("de")) {
                lng = 3;
            }
            if (langpubli.equalsIgnoreCase("fr")) {
                lng = 2;
            }
            if (langpubli.equalsIgnoreCase("ru")) {
                lng = 4;
            }
              

            if (pttyappli == 4) { // European patent
                // Tik viena eilute:
                PctrefId pid = new PctrefId();
                pid.setIdappli(idappli);
                pid.setOdpctep(Short.valueOf("1"));

                Pctref pctref = new Pctref();
                pctref.setId(pid);
                pctref.setTypctep(Short.valueOf("2")); //1 = PCT-nera; 2 = EP; 3 = Euro-PCT  
                pctref.setNopctep(extidappli); 
                pctref.setDtpctappli(dtappli);//???//paraiškos data iš b220
                pctref.setLangpctep((short)lang);  
                pctref.setOrictry("EP");//neaisku??
                pctref.setNopubli(doknr);
                pctref.setDtpctpubli(getSqldate(dtb430));
               pctref.setLangpubli((short)lng);// B260 turi būti langpubli
                pctref.setStpctep(Short.valueOf("1"));
                pctref.setDtnational(dtgrant);  //dtgrant
                pctref.setNboppo(Short.valueOf("0"));
                session.save(pctref);
            }//if European patent, pttyappli = 4

            if (pttyappli == 5) { // Euro-PCT  5
                //1-a eilute
                PctrefId pid = new PctrefId();
                pid.setIdappli(idappli);
                pid.setOdpctep(Short.valueOf("1"));
                Pctref pctref = new Pctref();
                pctref.setId(pid);
                pctref.setTypctep(Short.valueOf("2")); //1 = PCT; 2 = EP; 3 = Euro-PCT  
                pctref.setNopctep(extidappli);
                pctref.setDtpctappli(dtappli);//b220
                pctref.setLangpctep((short)lang);  
                pctref.setOrictry("WO");
                pctref.setNopubli(doknr);
                pctref.setDtpctpubli(getSqldate(dtb430));//???
                pctref.setLangpubli((short)lng);//?
                pctref.setStpctep(Short.valueOf("1"));
                pctref.setDtnational(dtgrant);  //dtgrant
                pctref.setNboppo(Short.valueOf("0"));
                session.save(pctref);

                // 2-a eilute:
                PctrefId pid2 = new PctrefId();
                pid2.setIdappli(idappli);
                pid2.setOdpctep(Short.valueOf("2"));   
                Pctref pctref2 = new Pctref();
                pctref2.setId(pid2);
                pctref2.setTypctep(Short.valueOf("3")); 
                pctref2.setNopctep(anum);//???
                pctref2.setDtpctappli(getSqldate(dtpctappli));//???861 data
                 //PCTfilingLang      
                
                
                pctref2.setLangpctep((short)lang);  
                pctref2.setOrictry("WO");//WO visada
                pctref2.setNopubli(pnum);
                pctref2.setDtpctpubli(getSqldate(dtpctpubli));//871date
               
                pctref2.setStpctep(Short.valueOf("1"));
               pctref2.setNboppo(Short.valueOf("0"));
                session.save(pctref2);
            }//if PCT



            History history = new History();
            HistoryId hid = new HistoryId();
            hid.setIdappli(idappli);
            short odhistosh = 1;
            Short odhisto = new Short(odhistosh);
            hid.setOdhisto(odhisto);
            history.setId(hid);
            history.setIdoper("1300");  


            history.setDtoper(dtgrant); //zenonas
            history.setSthisto(new Short("4"));       // zenonas: tb 4
            history.setIduseroper(new Short("12"));  
      //      history.setCddecioper(new Short("1"));  // zenonas sake, kad nereikia

            history.setStpay(new Short("1"));  
            history.setDtlegal(dtgrant); //zenonas: dtgrant
            history.setLvlpubli(new Short("0")); 
            history.setDtaction(dtgrant);
            session.save(history);



            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įrašant į duomenų bazę įvyko klaida. Dokumento nr. "+ doknr +", failas: "+file.getName());
            klaida = true;
         }
finally {     session.flush();  session.close();    }

        //------------------- ĮRAŠYMO Į DB PABAIGA  -----------------------

        
   if(klaida)    { 
       log.log(Level.INFO, "____Įrašant į DB įvyko klaida__\t"+ file.getName()+"\tEP"+doknr+"\tKlaida!!!!!"); 
       Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
      row.createCell(0).setCellValue("____Įrašant į DB įvyko klaida__");
       row.createCell(1).setCellValue("EP"+doknr);
       row.createCell(2).setCellValue(file.getName());
        } else {
 log.log(Level.INFO, "Į DB įrašytas\t"+ file.getName()+"\tEP"+doknr);  
  Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
      row.createCell(0).setCellValue("Į DB įrašytas");
       row.createCell(1).setCellValue("EP"+doknr);
       row.createCell(2).setCellValue(file.getName());
        System.out.println("Į DB įrašytas failas\t"+file.getName()+"\tEP"+doknr);
   }
        return 3;   //---?????
    }   //import file

//------------------------------------------------------//    
    public static String formatDate(String date) {
        StringBuilder builder = new StringBuilder(date);
        builder.insert(4, " ");
        builder.insert(7, " ");
        return builder.toString();
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        if (ext == null) {
            return "";
        }
        return ext;
    }

    public static StringBuilder UpdateFile(File file, String extidpatent)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {
        
    PatentasDB pd =  YraDB(extidpatent);
    
    
 String  yra_db = pd.YraDB?"Y_DB":"N_DB";  
 
 if(yra_db.equalsIgnoreCase("Y_DB")) {
 Boolean  galioja  = (pd.lgstappli == 20) || (pd.lgstappli == 30);
 String galioj = "negalioja";   if(galioja) galioj = "galioja_";
        
 log.log(Level.WARNING, "Keitimas \t\t" + extidpatent + "\t" + file.getName() + "\t" +  yra_db + "\t" +  galioj );
      
 }      
        
       if(!Langas.SU_KEITIMAIS) {
           System.out.println("Keitimų įrašymas atjungtas!");  
           return new StringBuilder("0"); }
        
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(file.getAbsolutePath());
        XPath xpath = XPathFactory.newInstance().newXPath();

        System.out.println("---UpdateFile procedūros pradžia: ________");
        
        
      


        //Changed tags B002EP:    ---------------------????????
        String idref = null;
        String btag = null;
        String pak_data = null;
        String pak_status = null;
        System.out.println("Pakeitimai: ");
        Object result = xpath.evaluate("/ep-bulletin/SDOBI/B000/eptags/B002EP/ep-chg-info/child::node()", doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            NamedNodeMap atrs = nodes.item(i).getAttributes();

            int len = atrs.getLength();
            for (int j = 0; j < len; j++) {
                Attr attr = (Attr) atrs.item(j);
                String nodename = attr.getNodeName();
                String nodevalue = attr.getNodeValue();
                System.out.print(" " + nodename + "=\"" + nodevalue + "\"");
                if (nodename.equalsIgnoreCase("btag")) {
                    btag = nodevalue;
                }
                if (nodename.equalsIgnoreCase("date")) {
                    pak_data = nodevalue;
                }
                if (nodename.equalsIgnoreCase("idref")) {
                    idref = nodevalue;
                }
                if (nodename.equalsIgnoreCase("status")) {
                    pak_status = nodevalue;
                }
            }//for atrs
            // Čia kažką daryti su šio ciklo pakeitimu:
            System.out.println();
            System.out.println("Įrašome pakeitimą į duomenų bazę: " + btag + " " + pak_data + " " + idref + " " + pak_status);
            Node n = (Node) doc.getElementById(idref);
            if (n == null) {
                System.out.println("Failed to find node for id: " + idref);
            } else {  // randame keiciamus elementus:
                NodeList pak_nodes = n.getChildNodes();
                String el = null, eltext = null;
                for (int k = 0; k < pak_nodes.getLength(); k++) {
                    el = pak_nodes.item(k).getNodeName();
                    eltext = pak_nodes.item(k).getTextContent();
                    System.out.println("Keičiamas elementas: " + el + "/" + eltext);
                }//for
                if (btag.equalsIgnoreCase("B475")) {
        
                } else System.out.println("NE B475");
                

                
                switch (btag.toUpperCase()) {
            case "B475"://lapse
        //       Keitimai.UpdateB475(extidpatent, n, pak_status);  //pak_data???
                break;
            case "B740"://agent
                B740.UpdateB740(extidpatent, n, pak_status, pak_data);
                break;
            case "B730"://owner
                 B730.UpdateB730(extidpatent, n, pak_status, pak_data);
                break;
            case "B720"://inventor
                B720.UpdateB720(extidpatent, n, pak_status, pak_data);
                break;
           case "B300"://prio
                B300.UpdateB300(extidpatent, n, pak_status, pak_data);
                break;
           case "B510EP"://klasifikacija
                B510EP.UpdateB510EP(extidpatent, n, pak_status, pak_data);
                break;
          case "B239"://revocation
                B239.UpdateB239(extidpatent, n, pak_status, pak_data);
                break;
          case "B540"://revocation
                B540.UpdateB540(extidpatent, n, pak_status, pak_data);
                break;
          case "B210"://revocation
                B210.UpdateB210(extidpatent, n, pak_status, pak_data);
                break;
          case "B220"://appdate
                B220.UpdateB220(extidpatent, n, pak_status, pak_data);
                break;
          case "B430"://pub date
                B430.UpdateB430(extidpatent, n, pak_status, pak_data);
                break;
         case "B861"://pub date
                B861.UpdateB861(extidpatent, n, pak_status, pak_data);
                break;     
         case "B871"://pub date
                B871.UpdateB871(extidpatent, n, pak_status, pak_data);
                break;      
         case "B140"://pub date
                B140.UpdateB140(extidpatent, n, pak_status, pak_data);
                break;      
         case "B620"://pub date
                B620.UpdateB620(extidpatent, n, pak_status, pak_data);
                break;     
             
                
            default: 
               
                break;
        } 
                
                
            }//else 
        }  //for
        
        
        
        String yradb = "";
        String idappli = "";
 //     yradb = YraDB(extidpatent).YraDB?"Yra DB":"Nėra DB";
  //idappli = YraDB(extidpatent).idappli;      
        System.out.println("---UpdateFile procedūroje apdorotas failas:");
        System.out.println("Keitimas "+extidpatent+" "+file.getName()+" "+pak_status+" "+btag+" "+yradb+" "+idappli);
       log.log(Level.OFF, "Keitimas \t"+extidpatent+"\t"+file.getName()+"\t"+pak_status+" "+btag+" "+yradb+" "+idappli);
        return new StringBuilder("str");
    }//UpdateFile
    
    
    
    

    public static java.sql.Date getSqldate(String strDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(EBDimport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new java.sql.Date(date.getTime());

    }
    
    
    public static java.sql.Date addYears(final java.util.Date date, final int years) {
        java.sql.Date calculatedDate = null;

        if (date != null) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, years);
            calculatedDate = new java.sql.Date(calendar.getTime().getTime());
        }

        return calculatedDate;
    }  
    
    
    public static PatentasDB YraDB(String extidpatent){
     PatentasDB pdb = new PatentasDB();
    pdb.YraDB = false;
    pdb.idappli = null;
    pdb.idagent = null;
     pdb.lgstappli = null;
     Session session = null; 
        try {
             session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
        
            Criteria criteria = session.createCriteria(Ptappli.class);  
    criteria.add(Restrictions.eq("extidpatent", "EP"+extidpatent));
 List pt =(List <Ptappli>) criteria.list();
 if((pt!=null)&&(pt.size()>0)){
Ptappli ptappli = (Ptappli)pt.get(0);
pdb.idappli  = ptappli.getIdappli();
pdb.idagent  = ptappli.getIdagent();
 pdb.lgstappli = ptappli.getLgstappli();
 
pdb.YraDB = true;
} 
         session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Tikrinant duomenis įvyko klaida. Dokumento nr. "+ extidpatent);
       
        }
     return pdb;
    }
    
    
      public static PatentasDB YraDBextidappli(String extidappli){
     PatentasDB pdb = new PatentasDB();
    pdb.YraDB = false;
    pdb.idappli = null;
    pdb.idagent = null;
     pdb.lgstappli = null;
     Session session = null; 
        try {
             session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
        
            Criteria criteria = session.createCriteria(Ptappli.class);  
    criteria.add(Restrictions.eq("extidappli", "EP"+extidappli));
 List pt =(List <Ptappli>) criteria.list();
 if((pt!=null)&&(pt.size()>0)){
Ptappli ptappli = (Ptappli)pt.get(0);
pdb.idappli  = ptappli.getIdappli();
pdb.idagent  = ptappli.getIdagent();
 pdb.lgstappli = ptappli.getLgstappli();
 //System.out.println("idappli: "+idappli);
//System.out.println(ptappli.getEngtitle());
pdb.YraDB = true;
} 
         session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įrašant duomenis įvyko klaida. Dokumento paraiskos nr. "+ extidappli);
       
        }
        
         finally { session.flush();  session.close(); }
     return pdb;
    }  
    
    
    
    
    
    
}//class

