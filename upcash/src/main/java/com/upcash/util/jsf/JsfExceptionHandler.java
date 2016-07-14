package com.upcash.util.jsf;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.upcash.service.NegocioException;

public class JsfExceptionHandler extends ExceptionHandlerWrapper {

	private static Log log = LogFactory.getLog(JsfExceptionHandler.class);
	
	private ExceptionHandler wrapped;

	public JsfExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();

		while (events.hasNext()) {
			ExceptionQueuedEvent event = events.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();// pega o contexo 
			
			Throwable exception = context.getException();

			NegocioException negocioException = getNegocioException(exception);
			
			boolean handled = false;
			
			try {
				if (exception instanceof ViewExpiredException) {
					handled = true;
					redirect("/");
				}else if(negocioException != null){ //caso seja negocioException exibir mensagem na tela  
					handled = true;
					FacesUtil.addErrorMessage(negocioException.getMessage());
				}else{//caso seja qualquer outra exceção direcionar para pagina de erro
					handled = true;
					log.error("Erro de sistema: "+ exception.getMessage(), exception);//cria o arquivo de log com as informações
					redirect("/Erro.xhtml");
				}
			} finally {
				if(handled){
					events.remove();//remove o evento da lista para não ser tratado em outro lugar
				}
			}
		}
		getWrapped().handle();//volta o tratamento para o padrao do jsf
	}

	/**
	 * Validar se a exceção é do tipo negocioexception 
	 * @param exception
	 * @return
	 */
	private NegocioException getNegocioException(Throwable exception) {
		if(exception instanceof NegocioException){
			return (NegocioException) exception;
		}else if(exception.getCause() != null){
			return getNegocioException(exception.getCause());
		}
		/*é possível que a NegocioException não seja a exceçao pai,
		 por isso temos que verificar na pilha de exceçoes
		 			
		*/
		return null;
	}

	private void redirect(String page) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			String contextPath = externalContext.getRequestContextPath();/* retorna o contexto da página */

			externalContext.redirect(contextPath + page);
			facesContext.responseComplete(); /* resposta está completa evitar outro processamento do jsf no ciclo de vida*/
		} catch (IOException e) {
			throw new FacesException(e);
		}

	}
}
