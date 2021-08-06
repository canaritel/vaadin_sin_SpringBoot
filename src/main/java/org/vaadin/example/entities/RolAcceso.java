package org.vaadin.example.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author telev
 */
@Entity
@Table(name = "rol_acceso")
@NamedQueries({
    @NamedQuery(name = "RolAcceso.findAll", query = "SELECT r FROM RolAcceso r"),
    @NamedQuery(name = "RolAcceso.findByIdAcceso", query = "SELECT r FROM RolAcceso r WHERE r.idAcceso = :idAcceso"),
    @NamedQuery(name = "RolAcceso.findByUsuario", query = "SELECT r FROM RolAcceso r WHERE r.usuario = :usuario"),
    @NamedQuery(name = "RolAcceso.findByClaveUsuario", query = "SELECT r FROM RolAcceso r WHERE r.claveUsuario = :claveUsuario"),
    @NamedQuery(name = "RolAcceso.findByRol", query = "SELECT r FROM RolAcceso r WHERE r.rol = :rol")})
public class RolAcceso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_acceso")
    private Integer idAcceso;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "usuario")
    private String usuario;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "clave_usuario")
    private String claveUsuario;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 13)
    @Column(name = "rol")
    private String rol;

    public RolAcceso() {
    }

    public RolAcceso(Integer idAcceso) {
        this.idAcceso = idAcceso;
    }

    public RolAcceso(Integer idAcceso, String usuario, String claveUsuario, String rol) {
        this.idAcceso = idAcceso;
        this.usuario = usuario;
        this.claveUsuario = claveUsuario;
        this.rol = rol;
    }

    public Integer getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(Integer idAcceso) {
        this.idAcceso = idAcceso;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
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
        if (!(object instanceof RolAcceso)) {
            return false;
        }
        RolAcceso other = (RolAcceso) object;
        if ((this.idAcceso == null && other.idAcceso != null) || (this.idAcceso != null && !this.idAcceso.equals(other.idAcceso))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.vaadin.example.backend.entity.RolAcceso[ idAcceso=" + idAcceso + " ]";
    }
    
}
