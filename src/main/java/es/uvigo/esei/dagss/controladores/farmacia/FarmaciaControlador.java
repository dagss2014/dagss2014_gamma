/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.farmacia;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.FarmaciaDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.Direccion;
import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Farmacia;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 *
 * @author ribadas
 */
@Named(value = "farmaciaControlador")
@SessionScoped
public class FarmaciaControlador implements Serializable {

    private Farmacia farmaciaActual;
    private Paciente pacienteActual;
    private String nif;
    private String password;
    private String numeroTarjetaSanitaria;
    private List<Receta> recetas;
    private Long idRecetaAServir;

    

    @Inject
    private AutenticacionControlador autenticacionControlador;

    @EJB
    private PacienteDAO pacienteDAO;
    
    @EJB
    private FarmaciaDAO farmaciaDAO;
    
    @EJB
    private RecetaDAO recetaDAO;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public FarmaciaControlador() {
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Farmacia getFarmaciaActual() {
        return farmaciaActual;
    }

    public void setFarmaciaActual(Farmacia farmaciaActual) {
        this.farmaciaActual = farmaciaActual;
    }
    
    public void setPacienteActual(Paciente pacienteActual) {
        this.pacienteActual = pacienteActual;
    }

    public void setNumeroTarjetaSanitaria(String numeroTarjetaSanitaria) {
        this.numeroTarjetaSanitaria = numeroTarjetaSanitaria;
    }

    public Paciente getPacienteActual() {
        return pacienteActual;
    }

    public String getNumeroTarjetaSanitaria() {
        return numeroTarjetaSanitaria;
    }

    private boolean parametrosAccesoInvalidos() {
        return ((nif == null) || (password == null));
    }
    
    public List<Receta> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas;
    }
    
    private boolean parametrosModificacionDatosInvalidos(){
        Direccion d = farmaciaActual.getDireccion();
        return ((farmaciaActual.getNombreFarmacia()==null)||(d.getDomicilio()==null)
                ||(d.getLocalidad()==null)||(d.getCodigoPostal()==null)
                ||(d.getProvincia()==null));
    }
    
    private boolean parametrosModificacionCredencialesInvalidos(){
        return ((farmaciaActual.getNif()==null)||(this.password==null));
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un nif o una contraseña", ""));
        } else {
            Farmacia farmacia = farmaciaDAO.buscarPorNIF(nif);
            if (farmacia == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe una farmacia con el NIF " + nif, ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(farmacia.getId(), password,
                        TipoUsuario.FARMACIA.getEtiqueta().toLowerCase())) {
                    farmaciaActual = farmacia;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }

            }
        }
        return destino;
    }
    
    public String doModificarDatos() {
        String destino = "index";
        System.out.println("Nombre: "+farmaciaActual.getNombreFarmacia());
        System.out.println("Domicilio: "+farmaciaActual.getDireccion().getDomicilio());
        System.out.println("Localidad: "+farmaciaActual.getDireccion().getDomicilio());
        if (parametrosModificacionDatosInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado alguno de los datos", ""));
        }
        else{
            farmaciaActual=farmaciaDAO.modificarDatos(farmaciaActual);
        }
        return destino;
    }
    
    public String doModificarCredenciales() {
        String destino = "index";
        if (parametrosModificacionCredencialesInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un nif o una contraseña", ""));
        }
        else{
            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
            String passwordCifrado = passwordEncryptor.encryptPassword(password);
            farmaciaActual.setNif(nif);
            farmaciaActual.setPassword(passwordCifrado);
            farmaciaActual=farmaciaDAO.modificarCredenciales(farmaciaActual);
        }
        return destino;
    }
    
    public String doConsultarPaciente(){
        String destino = "index";
        Paciente p = pacienteDAO.buscarPorTarjetaSanitaria(numeroTarjetaSanitaria);
        if(p==null){
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe un paciente con ese numero", ""));
        }
        else{
            pacienteActual=p;
            recetas = recetaDAO.buscarRecetasDeUsuario(p.getId());
            destino="paciente";
        }
        return destino;
    }
    
    public String doServirReceta(String id){
        idRecetaAServir = Long.parseLong(id);
        String destino = "paciente";
        Receta recetaAServir = recetaDAO.buscarPorId(idRecetaAServir);
        EstadoReceta estado = recetaAServir.getEstado();
        switch(estado){
            case SERVIDA:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Esta receta ya ha sido servida", ""));
                break;
            case ANULADA:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Esta receta ha sido anulada", ""));
                break;
            case GENERADA:
                Date fechaActual = new Date();
                Date fechaFinValidez = recetaAServir.getFinValidez();
                if (fechaFinValidez.before(fechaActual)){
                   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Esta receta está caducada", ""));
                }
                else{
                    for(Receta r : recetas){
                        if (r.getId()==idRecetaAServir)
                            r.setEstado(EstadoReceta.SERVIDA);
                    }
                    recetaAServir.setEstado(EstadoReceta.SERVIDA);
                    recetaDAO.servirReceta(idRecetaAServir);
                }
        }
        return destino;
    }
}
