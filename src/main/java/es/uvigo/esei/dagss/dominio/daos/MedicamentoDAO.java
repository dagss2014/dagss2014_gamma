/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class MedicamentoDAO extends GenericoDAO<Medicamento> {

    public List<Medicamento> buscarPorNombre(String nombre) {
        //COGER FECHA HOY
        Query q = em.createQuery("SELECT m FROM Medicamento AS m WHERE m.nombre LIKE :name");
        q.setParameter("name", "%" + nombre + "%");
        return q.getResultList();
    }
}
