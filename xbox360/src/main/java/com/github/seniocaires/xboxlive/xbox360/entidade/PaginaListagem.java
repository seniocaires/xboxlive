package com.github.seniocaires.xboxlive.xbox360.entidade;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("paginaslistagem")
public class PaginaListagem {

    @Id
    private ObjectId id;
	
	private Integer numero;

	private String link;

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
