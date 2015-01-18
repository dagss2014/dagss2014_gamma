/*
 Proyecto Java EE, DAGSS-2013
 */

package es.uvigo.esei.dagss.controladores.autenticacion;

import es.uvigo.esei.dagss.controladores.generico.GenericoControlador;
import es.uvigo.esei.dagss.dominio.daos.UsuarioDAO;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import es.uvigo.esei.dagss.dominio.entidades.Usuario;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author ribadas
 */
@Named(value = "autenticacionControlador")
@SessionScoped
public class AutenticacionControlador extends GenericoControlador implements Serializable {
    private boolean usuarioAutenticado;
    private TipoUsuario tipoUsuario;
    private Usuario usuario;
    private String newPassword;
    private String newPassword2;
    
    @EJB
    private UsuarioDAO usuarioDAO;
        
    
    public AutenticacionControlador() {
    }

    public boolean isUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public void setUsuarioAutenticado(boolean usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        this.newPassword2 = newPassword2;
    }
    
    public boolean autenticarUsuario(Long idUsuario, String passwordEnClaro, String tipo) {
        if (usuarioDAO.autenticarUsuario(idUsuario, passwordEnClaro, tipo)) {
            usuarioAutenticado = true;
            usuario = usuarioDAO.buscarPorId(idUsuario);
            tipoUsuario = usuario.getTipoUsuario();
            return true;
        }
        else {
            usuarioAutenticado = false;
            usuario = null;
            tipoUsuario = null;            
            return false;
        }
    }
    
    public String doLogOut() {
        usuarioDAO.actualizarUltimoAcceso(usuario.getId());
        usuarioAutenticado = false;
        usuario = null;
        tipoUsuario = null;
        
        // Finalizar la sesion        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        // Volver a la página principal
        return "/index";
    }
    
    public String actualizarPassword() {
        if(!newPassword.equals("") && newPassword.equals(newPassword2)) {
            usuarioDAO.actualizarPassword(usuario.getId(), newPassword);
            setFlashMessage("Contraseña cambiada con éxito");
        } else {
            setFlashMessage("Las dos contraseñas deben coincidir y ser distintas de vacío");
        }
        
        newPassword = "";
        newPassword2 = "";
        
        return "/index";
    }
    
}
