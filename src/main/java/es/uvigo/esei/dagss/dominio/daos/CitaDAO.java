/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;


@Stateless
@LocalBean
public class CitaDAO  extends GenericoDAO<Cita>{    

  public List<Cita> listarCitasHoy(String id){
        //COGER FECHA HOY
       Query q = em.createQuery("SELECT c FROM Cita AS c");
       // q.setParameter("id", id);
        return q.getResultList();
    }
}
