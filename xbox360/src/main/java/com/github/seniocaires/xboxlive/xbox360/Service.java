package com.github.seniocaires.xboxlive.xbox360;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.github.seniocaires.xboxlive.xbox360.entidade.PaginaListagem;
import com.github.seniocaires.xboxlive.xbox360.entidade.PaginaProduto;
import com.mongodb.MongoClient;

public class Service {

	private static Service instancia;
	public static final String LINK_PAGINA_LISTA_PRODUTOS = "http://marketplace.xbox.com/pt-BR/Games?pagesize=30&sortby=ReleaseDate&Page=";

	private MongoClient client;
	private Datastore datastore;

	private Service() {
	}

	public static Service getInstance() {
		if (instancia == null) {
			instancia = new Service();
		}
		return instancia;
	}

	private MongoClient getClient() {
		if (client == null) {
			client = new MongoClient("localhost", 27017);
		}
		return client;
	}

	public PaginaListagem buscarUltimaPaginaListagemAcessada() {

		final Morphia morphia = new Morphia();
		morphia.mapPackage("com.github.seniocaires.entidade");
		final Datastore datastore = morphia.createDatastore(getClient(), "xbox360");
		datastore.ensureIndexes();

		final Query<PaginaListagem> query = getDatastore().createQuery(PaginaListagem.class);
		final List<PaginaListagem> ultimaPaginaListagemAcessada = query.asList();

		if (ultimaPaginaListagemAcessada.isEmpty()) {
			PaginaListagem paginaListagem = new PaginaListagem();
			paginaListagem.setNumero(1);
			paginaListagem.setLink(LINK_PAGINA_LISTA_PRODUTOS + 1);
			getDatastore().save(paginaListagem);
			ultimaPaginaListagemAcessada.add(paginaListagem);
		}

		return ultimaPaginaListagemAcessada.get(0);
	}

	public PaginaProduto buscarPorLink(String link) {

		final Morphia morphia = new Morphia();
		morphia.mapPackage("com.github.seniocaires.entidade");
		final Datastore datastore = morphia.createDatastore(getClient(), "xbox360");
		datastore.ensureIndexes();

		final Query<PaginaProduto> query = getDatastore().createQuery(PaginaProduto.class).filter("link =", link);
		final List<PaginaProduto> paginasProduto = query.asList();

		if (paginasProduto.isEmpty()) {
			return null;
		} else {
			return paginasProduto.get(0);
		}
	}

	public void salvarPaginaProduto(PaginaProduto paginaProduto) {

		final Morphia morphia = new Morphia();
		morphia.mapPackage("com.github.seniocaires.entidade");
		final Datastore datastore = morphia.createDatastore(getClient(), "xbox360");
		datastore.ensureIndexes();

		datastore.save(paginaProduto);
	}

	private Datastore getDatastore() {
		if (datastore == null) {
			final Morphia morphia = new Morphia();
			morphia.mapPackage("com.github.seniocaires.entidade");
			datastore = morphia.createDatastore(getClient(), "xbox360-listagem");
			datastore.ensureIndexes();
		}
		return datastore;
	}
}
