/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.paciente;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.controladores.generico.GenericoControlador;
import es.uvigo.esei.dagss.dominio.daos.CitaDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */
@Named(value = "pacienteControlador")
@SessionScoped
public class PacienteControlador extends GenericoControlador implements Serializable {

    private Paciente pacienteActual;
    private String dni;
    private String numeroTarjetaSanitaria;
    private String numeroSeguridadSocial;
    private String password;
    private List<Cita> listaCitas;
    private CalendarManagedBean calendarManagedBean = new CalendarManagedBean();
    private List<String> listaHorasLibres;
    private String horaCita;
    private boolean usandoPassword;

    @Inject
    private AutenticacionControlador autenticacionControlador;

    @EJB
    private CitaDAO citaDAO;
    @EJB
    private PacienteDAO pacienteDAO;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public PacienteControlador() {
    }

    @PostConstruct
    public void inicializar() {
    }

    public Paciente getPacienteActual() {
        return pacienteActual;
    }

    public void setPacienteActual(Paciente pacienteActual) {
        this.pacienteActual = pacienteActual;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroTarjetaSanitaria() {
        return numeroTarjetaSanitaria;
    }

    public void setNumeroTarjetaSanitaria(String numeroTarjetaSanitaria) {
        this.numeroTarjetaSanitaria = numeroTarjetaSanitaria;
    }

    public String getNumeroSeguridadSocial() {
        return numeroSeguridadSocial;
    }

    public void setNumeroSeguridadSocial(String numeroSeguridadSocial) {
        this.numeroSeguridadSocial = numeroSeguridadSocial;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public List<Cita> getListaCitas() {
        return listaCitas;
    }

    public void setListaCitas(List<Cita> listaCitas) {
        this.listaCitas = listaCitas;
    }
    
    public CalendarManagedBean getCalendarManagedBean() {
        return calendarManagedBean;
    }

    public void setCalendarManagedBean(CalendarManagedBean calendarManagedBean) {
        this.calendarManagedBean = calendarManagedBean;
    }
    
    public List<String> getListaHorasLibres() {
        return listaHorasLibres;
    }

    public void setListaHorasLibres(List<String> listaHorasLibres) {
        this.listaHorasLibres = listaHorasLibres;
    }
    
    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }
    
    public boolean isUsandoPassword() {
        return usandoPassword;
    }

    public void setUsandoPassword(boolean usandoPassword) {
        this.usandoPassword = usandoPassword;
    }

    private boolean parametrosAccesoInvalidos() {
        return (((dni == null) && (numeroSeguridadSocial == null) && (numeroTarjetaSanitaria == null))
                || (password == null));
    }

    private Paciente recuperarDatosPaciente() {
        Paciente paciente = null;
        if (dni != null) {
            paciente = pacienteDAO.buscarPorDNI(dni);
        }
        if ((paciente == null) && (numeroSeguridadSocial != null)) {
            paciente = pacienteDAO.buscarPorNumeroSeguridadSocial(numeroSeguridadSocial);
        }
        if ((paciente == null) && (numeroTarjetaSanitaria != null)) {
            paciente = pacienteDAO.buscarPorTarjetaSanitaria(numeroTarjetaSanitaria);
        }
        return paciente;
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado suficientes datos de autenticación", ""));
        } else {
            Paciente paciente = recuperarDatosPaciente();
            if (paciente == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe ningún paciente con los datos indicados", ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(paciente.getId(), password, TipoUsuario.PACIENTE.getEtiqueta().toLowerCase())) {
                    pacienteActual = paciente;
                    
                    if(paciente.getPassword().equals("")) {
                        usandoPassword = false;
                        destino = "/paciente/privado/cambiarPassword";
                    } else {
                        usandoPassword = true;
                        destino = "/paciente/privado/index";
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }
    
    public String recuperarCitasFuturas() {
        listaCitas = citaDAO.listarFuturasCitas(pacienteActual.getId());
        
        return "/paciente/privado/listarCitas";
    }
    
    public String eliminarCita(Long idCita) {
        citaDAO.eliminarCita(idCita);
        
        setFlashMessage("Cita eliminada con éxito");
        
        return "/paciente/privado/index";
    }
    
    public String crearCita() {
        if(calendarManagedBean.getDate().after(new Date())) {
            SimpleDateFormat formatter = new SimpleDateFormat("H:mm");
            Date hora = new Date();
            
            try { hora = formatter.parse(horaCita); } catch (ParseException e) {}
            
            Cita c = new Cita(pacienteActual, pacienteActual.getMedico(), calendarManagedBean.getDate(), hora, 15, EstadoCita.PLANIFICADA);
            citaDAO.crear(c);
            
            setFlashMessage("Cita creada con éxito");
        } else {
            setFlashMessage("La fecha debe ser posterior a la actual");
        }
        
        return "/paciente/privado/index";
    }
    
    public void citasDisponibles() {
        List<Cita> listaCitasOcupadas = citaDAO.citasOcupadas(pacienteActual.getId(), calendarManagedBean.getDate());
        List<String> listaHorasLibresAux = new ArrayList<>();
        listaHorasLibres = new ArrayList<>();
       
        for(int i = 9 ; i < 21 ; i++) {
            listaHorasLibresAux.add(i + ":00");
            listaHorasLibresAux.add(i + ":15");
            listaHorasLibresAux.add(i + ":30");
            listaHorasLibresAux.add(i + ":45");
        }
        
        SimpleDateFormat dt = new SimpleDateFormat("H:mm"); 
        
        for(String citaLibre : listaHorasLibresAux) {
            listaHorasLibres.add(citaLibre);
            for(Cita citaOcupada : listaCitasOcupadas) {
                if(dt.format(citaOcupada.getHora()).equals(citaLibre)) {
                    listaHorasLibres.remove(citaLibre);
                }
            }
        }
    }
    
    public String editarPerfil() {
        pacienteActual = pacienteDAO.actualizar(pacienteActual);
        
        setFlashMessage("Perfil actualizado con éxito");
        
        return "/paciente/privado/index";
    }
}