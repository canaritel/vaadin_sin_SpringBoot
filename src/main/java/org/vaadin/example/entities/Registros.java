/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.example.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author telev
 */
@Entity
@Table(name = "registros")
@NamedQueries({
    @NamedQuery(name = "Registros.findAll", query = "SELECT r FROM Registros r"),
    @NamedQuery(name = "Registros.findByIdRegistro", query = "SELECT r FROM Registros r WHERE r.idRegistro = :idRegistro"),
    @NamedQuery(name = "Registros.findByNombre", query = "SELECT r FROM Registros r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Registros.findByApellidos", query = "SELECT r FROM Registros r WHERE r.apellidos = :apellidos"),
    @NamedQuery(name = "Registros.findByEmail", query = "SELECT r FROM Registros r WHERE r.email = :email"),
    @NamedQuery(name = "Registros.findByTelefono", query = "SELECT r FROM Registros r WHERE r.telefono = :telefono"),
    @NamedQuery(name = "Registros.findByFechaNacimiento", query = "SELECT r FROM Registros r WHERE r.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Registros.findByOcupacion", query = "SELECT r FROM Registros r WHERE r.ocupacion = :ocupacion")})
public class Registros implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idRegistro")
    private Integer idRegistro;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "apellidos")
    private String apellidos;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Size(max = 128)
    @Column(name = "ocupacion")
    private String ocupacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registro")
    private List<Accesos> accesosList;

    public Registros() {
    }

    public Registros(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Registros(Integer idRegistro, String nombre, String apellidos, String email, String telefono, Date fechaNacimiento) {
        this.idRegistro = idRegistro;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public List<Accesos> getAccesosList() {
        return accesosList;
    }

    public void setAccesosList(List<Accesos> accesosList) {
        this.accesosList = accesosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRegistro != null ? idRegistro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registros)) {
            return false;
        }
        Registros other = (Registros) object;
        if ((this.idRegistro == null && other.idRegistro != null) || (this.idRegistro != null && !this.idRegistro.equals(other.idRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.vaadin.example.entities.Registros[ idRegistro=" + idRegistro + " ]";
    }
    
}
