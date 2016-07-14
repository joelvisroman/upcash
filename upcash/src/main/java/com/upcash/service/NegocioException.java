package com.upcash.service;

public class NegocioException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public NegocioException(String msg) {
		super(msg);
	}
	/*
	 * quando quiser que mostre na mesma página a mensagem de erro, 
	 * utilizar essa exceção
	 */

}
