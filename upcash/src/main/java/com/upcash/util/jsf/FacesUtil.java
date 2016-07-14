package com.upcash.util.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtil {

	/**
	 * metodo utilizado na Negocio exception 
	 * para o erro ser exibido na tela
	 * @param msg
	 */
	public static void addErrorMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}
}
