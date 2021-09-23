package org.vaadin.example.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author telev
 */
@Entity
@Table(name = "accesos")
@NamedQueries({
    @NamedQuery(name = "Accesos.findAll", query = "SELECT a FROM Accesos a"),
    @NamedQuery(name = "Accesos.findByIdAcceso", query = "SELECT a FROM Accesos a WHERE a.idAcceso = :idAcceso"),
    @NamedQuery(name = "Accesos.findByUsername", query = "SELECT a FROM Accesos a WHERE a.username = :username"),
    @NamedQuery(name = "Accesos.findByPasswordSalt", query = "SELECT a FROM Accesos a WHERE a.passwordSalt = :passwordSalt"),
    @NamedQuery(name = "Accesos.findByPasswordHash", query = "SELECT a FROM Accesos a WHERE a.passwordHash = :passwordHash"),
    @NamedQuery(name = "Accesos.findByActivationCode", query = "SELECT a FROM Accesos a WHERE a.activationCode = :activationCode"),
    @NamedQuery(name = "Accesos.findByActivo", query = "SELECT a FROM Accesos a WHERE a.activo = :activo")})
public class Accesos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAcceso")
    private Integer idAcceso;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "username")
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "passwordSalt")
    private String passwordSalt;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "passwordHash")
    private String passwordHash;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "activationCode")
    private String activationCode;

    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private boolean activo;

    @JoinColumn(name = "registro", referencedColumnName = "idRegistro")
    @ManyToOne(optional = false)
    private Registros registro;

    @JoinColumn(name = "rol", referencedColumnName = "idRol")
    @ManyToOne(optional = false)
    private Roles rol;

    public Accesos() {
    }

    public Accesos(Integer idAcceso) {
        this.idAcceso = idAcceso;
    }

    /*
    public Accesos(Integer idAcceso, String username, String password, boolean activo, Roles rol, Registros registro) {
        this.idAcceso = idAcceso;
        this.username = username;
        this.passwordSalt = RandomStringUtils.random(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
        this.activo = activo;
        this.rol = rol;
        this.registro = registro;
    }
     */
    //Creamos un nuevo constructor específico para el acceso
    public Accesos(String username, String password, Roles rol, Registros registro) {
        this.username = username;
        this.rol = rol;
        this.registro = registro;
        this.passwordSalt = password; // RandomStringUtils.random(32);
        this.passwordHash = password; // DigestUtils.sha1Hex(password + passwordSalt);
        this.activationCode = RandomStringUtils.randomAlphanumeric(32);
    }

    public Integer getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(Integer idAcceso) {
        this.idAcceso = idAcceso;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Registros getRegistro() {
        return registro;
    }

    public void setRegistro(Registros registro) {
        this.registro = registro;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAcceso != null ? idAcceso.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Accesos)) {
            return false;
        }
        Accesos other = (Accesos) object;
        if ((this.idAcceso == null && other.idAcceso != null) || (this.idAcceso != null && !this.idAcceso.equals(other.idAcceso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.vaadin.example.entities.Accesos[ idAcceso=" + idAcceso + " ]";
    }

}
