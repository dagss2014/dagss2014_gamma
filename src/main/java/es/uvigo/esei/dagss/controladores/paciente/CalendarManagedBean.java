package es.uvigo.esei.dagss.controladores.paciente;

import java.util.Date;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean
@SessionScoped
public class CalendarManagedBean {
    
    private Date date = new Date();
 
    public Date getDate() {
        return date;
    }
 
    public void setDate(Date date) {
        this.date = date;
    }
}