/*
 Proyecto Java EE, DAGSS-2014
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;


@Stateless
@LocalBean
public class CitaDAO extends GenericoDAO<Cita>{    

    public List<Cita> listarCitasHoy(long id){
        
        Query q = em.createQuery("SELECT c FROM Cita AS c where c.medico.id=:id");
        q.setParameter("id", id);
        return q.getResultList();
    }
    
    public List<Cita> listarFuturasCitas(Long idPaciente) {
        Date fechaActual = new Date();
        Query q = em.createQuery("SELECT c FROM Cita AS c WHERE c.paciente.id = :idPaciente AND ((c.fecha > :fecha) OR (c.fecha = :fecha AND c.hora > :hora))");
        q.setParameter("idPaciente", idPaciente);
        q.setParameter("fecha", fechaActual);
        q.setParameter("hora", fechaActual);
        return q.getResultList();
    }
    
    public void eliminarCita(Long idCita) {
        Query q = em.createQuery("DELETE FROM Cita AS c WHERE c.id = :idCita");
        q.setParameter("idCita", idCita);
        q.executeUpdate();
    }
    
    public List<Cita> citasOcupadas(Long idPaciente, Date fecha) {
        Query q = em.createQuery("SELECT c FROM Cita AS c WHERE c.paciente.id = :idPaciente AND c.fecha = :fecha");
        q.setParameter("idPaciente", idPaciente);
        q.setParameter("fecha", fecha);
        return q.getResultList();
    }
}
