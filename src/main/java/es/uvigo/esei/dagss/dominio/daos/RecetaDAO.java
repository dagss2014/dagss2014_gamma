/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class RecetaDAO extends GenericoDAO<Receta>{
 
    public List<Receta> buscarRecetasDeUsuario(long pacienteId){
        Query q = em.createQuery("SELECT r FROM Receta AS r "
                + "WHERE r.prescripcion.tratamiento.paciente.id= :pacienteId");
        q.setParameter("pacienteId",pacienteId);
        
        List<Receta> list = q.getResultList();
        return list;
    }
    
    public void servirReceta(long id){
       Query q = em.createQuery("UPDATE Receta r SET r.estadoReceta = :estado "
               + "WHERE r.id= :id");
       q.setParameter("estado", EstadoReceta.SERVIDA);
       q.setParameter("id", id);
       q.executeUpdate();
    }
}
