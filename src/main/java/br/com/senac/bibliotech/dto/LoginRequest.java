package br.com.senac.bibliotech.dto;



//CORPO DO POST /AUTH/LOGIN

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//NÃO VALIDAMOS TAMANJPO MINIMO DE SENHA - NOO LOGIN UM ASENHA CURTA NAO É ERRO DE FORMATO E ŚI UMA SENHA ERRADA. A REGRA DE TAMANHO VALE NO CADASTREO
public record LoginRequest(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "o email é valido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
