/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.List;
import lt.vinco.ebdimport.entity.Classin;
import lt.vinco.ebdimport.entity.Division;
import lt.vinco.ebdimport.entity.Extstate;
import lt.vinco.ebdimport.entity.History;
import lt.vinco.ebdimport.entity.Invent;
import lt.vinco.ebdimport.entity.Inventor;
import lt.vinco.ebdimport.entity.Own;
import lt.vinco.ebdimport.entity.Priority;
import lt.vinco.ebdimport.entity.Ptappli;
import lt.vinco.ebdimport.entity.Represent;
import lt.vinco.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author vbatulevicius
 */
public class Trinti {

    public static int DeleteClassif(String idappli) {
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            //Istriname  klasifikaciją:   
            String hql = "from Classin where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List oldclass = q.list();
            if (oldclass.size() > 0) {
                for (int i = 0; i < oldclass.size(); i++) {
                    Classin oldcl = (Classin) oldclass.get(i);
                    session.delete(oldcl);
                }//for
            }//if   

            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Klasifikacija ištrinta " + idappli);
        return 0;
    }

    public static int DeleteOwn(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Istriname  own:   
            String hql = "from Own where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List oldgrantees = q.list();
            if (oldgrantees.size() > 0) {
                for (int i = 0; i < oldgrantees.size(); i++) {
                    Own own = (Own) oldgrantees.get(i);
                    if (own != null) {
                        session.delete(own);          /////???????
                    }
                }
            }
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Own ištrintas " + idappli);
        return 0;
    }

    public static int DeleteInventor(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Istriname  inventorius is invent:
            String hql = "from Invent where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List oldinvent = q.list();
            if (oldinvent.size() > 0) {
                for (int i = 0; i < oldinvent.size(); i++) {
                    Invent inv = (Invent) oldinvent.get(i);
//???  Zenono klausti ar reikia istrinti senus inventorius is Inventor lenteles  ???  Reikia ištrinti ir iš inventor
                    //Jei reikia, trinti siame cikle:
                    //Integer idinvent = inv.getIdinvent();
                    Inventor inventor = (Inventor) session.get(Inventor.class, inv.getIdinvent());
                    if (inventor != null) {
                        session.delete(inventor);
                    }
                    if (inv != null) {
                        session.delete(inv);
                    }
                }
            }
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Inventor ištrintas " + idappli);
        return 0;
    }

    public static int DeletePrio(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Istriname prioritetus:
            String hql = "from Priority where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List prios = q.list();
            if (prios.size() > 0) {
                for (int i = 0; i < prios.size(); i++) {
                    Priority prio = (Priority) prios.get(i);
                    if (prio != null) {
                        session.delete(prio);
                    }
                }
            }
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Prioritetas ištrintas " + idappli);
        return 0;
    }

    public static int DeleteExt(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Istriname extstates:
            String hql = "from Extstate where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List exts = q.list();
            if (exts.size() > 0) {
                for (int i = 0; i < exts.size(); i++) {
                    Extstate ext = (Extstate) exts.get(i);
                    if (ext != null) {
                        session.delete(ext);
                    }
                }
            }
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
   session.close();
        }
        System.out.println("Extstates ištrintos " + idappli);
        return 0;
    }

    public static int DeletePctref(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
 //<hibernate.query.factory_class">org.hibernate.hql.ast.ASTQueryTranslatorFactory           
            //Istriname pctref:
            String hql = "delete from Pctref  where idappli = :idappli";   
            Query q = session.createQuery(hql);
            q.setString("idappli", idappli);
           int rowCount = q.executeUpdate();
            System.out.println("Ištrinta Pctref įrašų: " + rowCount);
            
            
            
//            String hql = "from Pctref where idappli = :idappli";
//            Query q = session.createQuery(hql);
//            q.setParameter("idappli", idappli);
//            List pctrefs = q.list();
//            if (pctrefs.size() > 0) {
//                for (int i = 0; i < pctrefs.size(); i++) {
//                    Pctref pctref = (Pctref) pctrefs.get(i);
//                    if (pctref != null) {
//                        session.delete(pctref);
//                    }
//                }
//            }
            
            
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Pctref ištrinta " + idappli);
        return 0;
    }
    
    
    
    
    
    

    public static int DeleteDiv(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
            //Istriname Division:
        //    String hql = "from Division where idappli = :idappli or idapplidiv = :idappli";   ///   -----!!!!!!-----
             String hql = "from Division where idapplidiv = :idappli";   ///   -----!!!!!!-----
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List divs = q.list();
            if (divs.size() > 0) {
                for (int i = 0; i < divs.size(); i++) {
                    Division div = (Division) divs.get(i);
                    if (div != null) {
                        session.delete(div);
                    }
                }
            }
    
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Division ištrinta " + idappli);
        return 0;
    }

    public static int DeleteRep(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Istriname agentą:
            String hql = "from Represent where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List reps = q.list();
            if (reps.size() > 0) {
                for (int i = 0; i < reps.size(); i++) {
                    Represent rep = (Represent) reps.get(i);
                    if (rep != null) {
                        session.delete(rep);
                    }
                }//for
            }//if
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Representative ištrintas " + idappli);
        return 0;
    }

    public static int DeleteHist(String idappli) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //Ištriname istoriją:
            String hql = "from History where idappli = :idappli";
            Query q = session.createQuery(hql);
            q.setParameter("idappli", idappli);
            List hists = q.list();
            if (hists.size() > 0) {
                for (int i = 0; i < hists.size(); i++) {
                    History hist = (History) hists.get(i);
                    if (hist != null) {
                        session.delete(hist);
                    }
                }//for
            }//if
            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Istorija ištrinta " + idappli);
        return 0;
    }

    public static int DeleteFromDB(String idappli) {
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();


            DeleteClassif(idappli);
            DeleteOwn(idappli);
            DeleteInventor(idappli);
            DeletePrio(idappli);
            DeleteExt(idappli);
            DeletePctref(idappli);
            DeleteDiv(idappli);
            DeleteRep(idappli);
            DeleteHist(idappli);


            //Ištriname iš ptappli:  
            Ptappli ptappli = (Ptappli) session.get(Ptappli.class, idappli);
            if (ptappli != null) {
                session.delete(ptappli);
            }

            session.getTransaction().commit();
            //session.close();??
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
        } finally {
//    session.flush();
    session.close();
        }
        System.out.println("Viskas ištrinta: " + idappli);
        return 0;
    }
}//class
