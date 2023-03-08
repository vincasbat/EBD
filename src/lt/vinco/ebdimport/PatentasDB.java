/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.vinco.ebdimport;

import java.util.List;
import lt.vinco.ebdimport.entity.Agent;
import lt.vinco.ebdimport.entity.Invent;
import lt.vinco.ebdimport.entity.Inventor;
import lt.vinco.ebdimport.entity.Jcs_owner;
import lt.vinco.ebdimport.entity.Own;
import lt.vinco.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Vincas Batulevičius
 */
public class PatentasDB {

    public Boolean YraDB = false;
    public String idappli = "";
    public Integer idagent = null;
    public Integer lgstappli = 0;

    public String getOwnerNames() {
        String ownerNames = "";
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Own.class);
            criteria.add(Restrictions.eq("id.idappli", idappli));  
            List ow = (List<Own>) criteria.list();
            if ((ow != null) && (ow.size() > 0)) {

                for (Object o : ow) {
                    Own own = (Own) o;
                    int ido = own.getId().getIdowner();
                    Criteria cr = session.createCriteria(Jcs_owner.class);
                    cr.add(Restrictions.eq("idowner", ido));
                    List owner = (List<Jcs_owner>) cr.list();
                    if (owner.size() > 0) {
                        Jcs_owner Owneris = (Jcs_owner) owner.get(0);
                        String nmowner = Owneris.getNmowner().trim();
                        String epidowner = Owneris.getIdowner().toString().trim();
                        ownerNames += epidowner + " " + nmowner + "; ";
                    }

                } //for

            }
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įvyko klaida skaitant ownerius. idappli = " + idappli);
        } finally {
            session.flush();
            session.close();
        }
        return ownerNames;
    }
    
    
  public String getInvNames() {
  String Invs = "";
   Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Invent.class);
            criteria.add(Restrictions.eq("idappli", idappli));  //  ???????????????????????
            List invs = (List<Invent>) criteria.list();
            if ((invs != null) && (invs.size() > 0)) {

                for (Object o : invs) {
                    Invent invent = (Invent) o;
                    int idinvent = invent.getIdinvent();
                    Criteria cr = session.createCriteria(Inventor.class);
                    cr.add(Restrictions.eq("idinvent", idinvent));
                    List inventors = (List<Inventor>) cr.list();
                    if (inventors.size() > 0) {
                        Inventor inventor = (Inventor) inventors.get(0);
                        String nminventor = inventor.getNminventor().trim();
                        String idinventor = inventor.getIdinvent().toString();
                        Invs += idinventor + " " + nminventor + "; ";
                    }

                } //for

            }
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įvyko klaida skaitant inventorius. idappli = " + idappli);
        } finally {
            session.flush();
            session.close();
        }
  
  return Invs;
  }   
  
  
   public String getAgentName() {
  String agent = "";
   Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Agent.class);
            criteria.add(Restrictions.eq("idagent", idagent));  //  
            List agents = (List<Agent>) criteria.list();
            if ((agents != null) && (agents.size() > 0)) {

                for (Object o : agents) {
                    Agent ag = (Agent) o;
                    String nmagent = ag.getNmagent().trim();
                    agent += idagent.toString() + " " + nmagent +"; ";
                } //for

            }
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
            session.getTransaction().rollback();
            System.out.println("Įvyko klaida skaitant agenta idagent = " + idagent);
        } finally {
            session.flush();
            session.close();
        }
  
  return agent;
  }   
  
  
   
}//class
