/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
@LocalBean
public class TratamientoDAO extends GenericoDAO<Tratamiento> {

    public List<Tratamiento> buscarPorDNIPaciente(String dni) {
        try {
            Query q = em.createQuery("SELECT p FROM Tratamiento AS p "
                    + "  WHERE p.paciente.dni = :dni");
            q.setParameter("dni", dni);
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void guardar(Tratamiento tratamiento) {
        try {
            em.getTransaction().begin();
            em.persist(tratamiento);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            em.close();
        }
    }

}
