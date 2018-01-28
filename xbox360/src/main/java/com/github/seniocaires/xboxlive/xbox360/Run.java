package com.github.seniocaires.xboxlive.xbox360;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Run {

	public static void main(String[] args) {
		Logger.getGlobal().log(Level.CONFIG, "Iniciando Xbox360");
		
		Logger.getGlobal().log(Level.FINE, "Acessando página de listagem: \n" + Service.getInstance().buscarUltimaPaginaListagemAcessada().getLink());
	}
}
