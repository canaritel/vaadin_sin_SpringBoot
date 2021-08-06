/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.example.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "juego")
@NamedQueries({
    @NamedQuery(name = "Juego.findAll", query = "SELECT j FROM Juego j"),
    @NamedQuery(name = "Juego.findByIdJuego", query = "SELECT j FROM Juego j WHERE j.idJuego = :idJuego"),
    @NamedQuery(name = "Juego.findByTitulo", query = "SELECT j FROM Juego j WHERE j.titulo = :titulo"),
    @NamedQuery(name = "Juego.findBySistemaOperativo", query = "SELECT j FROM Juego j WHERE j.sistemaOperativo = :sistemaOperativo"),
    @NamedQuery(name = "Juego.findByFechaJuego", query = "SELECT j FROM Juego j WHERE j.fechaJuego = :fechaJuego"),
    @NamedQuery(name = "Juego.findByPrecio", query = "SELECT j FROM Juego j WHERE j.precio = :precio")})
public class Juego implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idJuego")
    private Integer idJuego;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "titulo")
    private String titulo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "sistema_operativo")
    private String sistemaOperativo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_juego")
    @Temporal(TemporalType.DATE)
    private Date fechaJuego;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio")
    private BigDecimal precio;
    @Lob
    @Column(name = "imagen")
    private byte[] imagen;
    @JoinColumn(name = "distribuidor", referencedColumnName = "idDistribuidor")
    @ManyToOne(optional = false)
    private Distribuye distribuidor;
    @JoinColumn(name = "usuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false)
    private Usuario usuario;

    public Juego() {
    }

    public Juego(Integer idJuego) {
        this.idJuego = idJuego;
    }

    public Juego(Integer idJuego, String titulo, String sistemaOperativo, Date fechaJuego, BigDecimal precio) {
        this.idJuego = idJuego;
        this.titulo = titulo;
        this.sistemaOperativo = sistemaOperativo;
        this.fechaJuego = fechaJuego;
        this.precio = precio;
    }

    public Integer getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(Integer idJuego) {
        this.idJuego = idJuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public Date getFechaJuego() {
        return fechaJuego;
    }

    public void setFechaJuego(Date fechaJuego) {
        this.fechaJuego = fechaJuego;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Distribuye getDistribuidor() {
        return distribuidor;
    }

    public void setDistribuidor(Distribuye distribuidor) {
        this.distribuidor = distribuidor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idJuego != null ? idJuego.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Juego)) {
            return false;
        }
        Juego other = (Juego) object;
        if ((this.idJuego == null && other.idJuego != null) || (this.idJuego != null && !this.idJuego.equals(other.idJuego))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.vaadin.example.backend.entity.Juego[ idJuego=" + idJuego + " ]";
    }
    
}
