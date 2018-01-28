package com.github.seniocaires.xboxlive.xbox360;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.seniocaires.xboxlive.xbox360.entidade.PaginaListagem;
import com.github.seniocaires.xboxlive.xbox360.entidade.PaginaProduto;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

public class Run {

	public static void main(String[] args) {
		Logger.getGlobal().log(Level.CONFIG, "Iniciando Xbox360");

		Document documentPaginaListagem;
		boolean existePaginaListagemSeguinte = false;
		PaginaListagem ultimaPaginaListagemAcessada;
		do {
			ultimaPaginaListagemAcessada = Service.getInstance().buscarUltimaPaginaListagemAcessada();
			Logger.getGlobal().log(Level.FINE, "Acessando página de listagem: \n" + ultimaPaginaListagemAcessada.getLink());

			try {
				documentPaginaListagem = Jsoup.connect(ultimaPaginaListagemAcessada.getLink()).timeout(6000 * 1000).get();

				for (Element elementProdutoPaginaListagem : buscarProdutos(documentPaginaListagem)) {
					atualizarPagina(elementProdutoPaginaListagem.getElementsByTag("a").get(0).attr("href"), 1);
					Runtime.getRuntime().gc();
				}

				/* Atualizando última página de listagem */;
				ultimaPaginaListagemAcessada.setNumero(ultimaPaginaListagemAcessada.getNumero() + 1);
				ultimaPaginaListagemAcessada.setLink(Service.LINK_PAGINA_LISTA_PRODUTOS + ultimaPaginaListagemAcessada.getNumero());
				ultimaPaginaListagemAcessada.setDataAtualizacao(new Date());
				ultimaPaginaListagemAcessada.setAtiva(Boolean.TRUE);

			} catch (IOException ioException) {
				Logger.getGlobal().log(Level.SEVERE, "Erro ao carregar página de listagem.", ioException);
				existePaginaListagemSeguinte = false;
			}
		} while (existePaginaListagemSeguinte);
	}

	private static void atualizarPagina(String linkSemPaginacao, int numeroPagina) {

		PaginaProduto paginaProduto;
		Document documentPaginaProduto;

		try {

			/* Recuperando página ou criando uma nova. */
			paginaProduto = Service.getInstance().buscarPorLink(URLDecoder.decode("http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, "UTF-8"));
			if (paginaProduto == null || paginaProduto.getId() == null) {
				paginaProduto = new PaginaProduto(URLDecoder.decode("http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, "UTF-8"));
			}

			/* Acessando página */
			Logger.getGlobal().log(Level.FINE, "Acessando página do produto: " + paginaProduto.getLink());
			documentPaginaProduto = Jsoup.connect(paginaProduto.getLink()).timeout(6000 * 1000).get();

			/* Atualizando conteúdo da página */
			paginaProduto.setAtiva(true);
			paginaProduto.setDataAtualizacao(new Date());
			paginaProduto.setNumero(numeroPagina);
			paginaProduto.setHtml(getHtmlComprimido(paginaProduto.getLink()));

			Service.getInstance().salvarPaginaProduto(paginaProduto);

			if (existePaginaSeguinte(documentPaginaProduto)) {
				atualizarPagina(linkSemPaginacao, ++numeroPagina);
			}

		} catch (UnsupportedEncodingException e) {
			Logger.getGlobal().log(Level.SEVERE, "Erro no encoding da url da página." + "http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, e);
		} catch (HttpStatusException hse) {
			Logger.getGlobal().log(Level.WARNING, "Erro ao acessar página do produto." + "http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, hse);
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Erro ao carregar página." + "http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, e);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Erro ao buscar página." + "http://marketplace.xbox.com" + linkSemPaginacao + "?nosplash=1&Page=" + numeroPagina, e);
		}
	}

	public static boolean existePaginaSeguinte(Document pagina) {

		try {
			pagina.getElementsByClass("Next").get(0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static String getHtmlComprimido(String url) {

		StringBuilder retorno = new StringBuilder();
		HtmlCompressor compressor = new HtmlCompressor();
		compressor.setCompressCss(true);
		compressor.setCompressJavaScript(true);

		try {
			retorno.append(Jsoup.connect(url).timeout(6000 * 1000).get().html());
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Erro ao acessar página e comprimir HTML.");
		}

//		return compressor.compress(retorno.toString());
		return retorno.toString();
	}

	public static Elements buscarProdutos(Document pagina) {

		Elements retorno;

		try {

			Element gradeProdutosWE = pagina.getElementsByClass("ProductResults").get(0);

			retorno = gradeProdutosWE.getElementsByClass("grid-6");

		} catch (Exception exception) {
			Logger.getGlobal().log(Level.SEVERE, "Erro ao buscar produtos da página principal.", exception);
			retorno = new Elements();
		}

		return retorno;
	}
}
