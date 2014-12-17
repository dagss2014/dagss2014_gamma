/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.medico;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.CitaDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.daos.TratamientoDAO;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */
@Named(value = "medicoControlador")
@SessionScoped
public class MedicoControlador implements Serializable {

    private Medico medicoActual;
    private String dni;
    private String numeroColegiado;
    private String password;
    private List<Cita> listaCitas;
    private Tratamiento tratamiento;
    private Paciente paciente;
    private String nombreMedicamento;
    private String indicaciones;
    private Medicamento medicamento;
    private List<Medicamento> searchList;
    private String comentarios;
    private Date fechaInicio;
    private Date fechaFin;
    private List<Tratamiento> listaTratamientos;
    private List<Prescripcion> listaPrescripciones;

    @Inject
    private AutenticacionControlador autenticacionControlador;
    @Inject
    private CitaDAO citaDAO;
    @Inject
    private TratamientoDAO tratamientoDAO;
    @Inject
    private MedicamentoDAO medicamentoDAO;

    @EJB
    private MedicoDAO medicoDAO;
    @Inject
    private PacienteDAO pacienteDAO;
    private Cita cita;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public MedicoControlador() {

    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }

    public List<Cita> getListaCitas() {
        return listaCitas;
    }

    public void setListaCitas(List<Cita> listaCitas) {
        this.listaCitas = listaCitas;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }
    private int dosis;

    public String getnombreMedicamento() {
        return nombreMedicamento;
    }

    public void setnombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Tratamiento> getListaTratamientos() {
        return listaTratamientos;
    }

    public void setListaTratamientos(List<Tratamiento> listaTratamientos) {
        this.listaTratamientos = listaTratamientos;
    }

    private boolean parametrosAccesoInvalidos() {
        return (((dni == null) && (numeroColegiado == null)) || (password == null));
    }

    private Medico recuperarDatosMedico() {
        Medico medico = null;
        if (dni != null) {
            medico = medicoDAO.buscarPorDNI(dni);
        }
        if ((medico == null) && (numeroColegiado != null)) {
            medico = medicoDAO.buscarPorNumeroColegiado(numeroColegiado);
        }
        return medico;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un DNI ó un número de colegiado o una contraseña", ""));
        } else {
            Medico medico = recuperarDatosMedico();
            if (medico == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe ningún médico con los datos indicados", ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(medico.getId(), password,
                        TipoUsuario.MEDICO.getEtiqueta().toLowerCase())) {
                    medicoActual = medico;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }

    //Acciones
 
    public String doListarCitas() {
        listaCitas = citaDAO.listarCitasHoy(dni);
        return "/medico/privado/citas";
    }

    public String doPacienteAtendido() {
        this.cita.setEstado(EstadoCita.COMPLETADA);
        citaDAO.actualizar(this.cita);
        listaCitas = citaDAO.listarCitasHoy(dni);

        return "/medico/privado/citas";
    }

    public String doAtender(Cita cita) {
        this.cita = cita;
        paciente = pacienteDAO.buscarPorDNI(cita.getPaciente().getDni());
        listaTratamientos = tratamientoDAO.buscarPorDNIPaciente(cita.getPaciente().getDni());
        return "/medico/privado/atencionPaciente";
    }

    public String doAddPrescripcion() {
        if (searchList != null) {
            for (Medicamento m : searchList) {
                if (m.getNombre().equals(nombreMedicamento)) {
                    medicamento = m;
                    break;
                }
            }
            listaPrescripciones.add(new Prescripcion(indicaciones, tratamiento, medicamento, dosis));
            tratamiento.setPrescripciones(listaPrescripciones);
        }
        return "/medico/privado/nuevoTratamiento";
    }

    public String doNewTratamiento() {
        comentarios = "";
        fechaFin= new Date();
        fechaInicio = new Date();
        tratamiento = new Tratamiento(paciente, medicoActual, comentarios, fechaInicio, fechaFin);
        return "/medico/privado/nuevoTratamiento";
    }

    public String doGuardarTratamiento() {
        tratamiento.setComentarios(comentarios);
        tratamiento.setFechaInicio(fechaInicio);
        tratamiento.setFechaFin(fechaFin);
        listaTratamientos.add(tratamiento);
        tratamientoDAO.actualizar(tratamiento);
        return "/medico/privado/atencionPaciente";
    }

    public String doNewPrescripcion() {
        listaPrescripciones = tratamiento.getPrescripciones();
        nombreMedicamento = "";
        dosis = 0;
        indicaciones = "";
        return "/medico/privado/formularioPrescripcion";
    }

    public List<String> completeText(String query) {
        List<String> results = new ArrayList<>();
        searchList = medicamentoDAO.buscarPorNombre(query);
        for (Medicamento m : searchList) {
            results.add(m.getNombre());
        }
        return results;
    }

}
