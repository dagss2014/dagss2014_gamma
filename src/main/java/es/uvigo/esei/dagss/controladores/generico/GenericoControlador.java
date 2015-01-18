package es.uvigo.esei.dagss.controladores.generico;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class GenericoControlador {
    
    public void setFlashMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(message));
        context.getExternalContext().getFlash().setKeepMessages(true);
    } 
}
